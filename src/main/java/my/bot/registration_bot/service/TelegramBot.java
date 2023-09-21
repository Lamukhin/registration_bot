package my.bot.registration_bot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.vdurmont.emoji.EmojiParser;

import lombok.extern.slf4j.Slf4j;
import my.bot.registration_bot.config.BotConfig;
import my.bot.registration_bot.dao.UserRepository;
import my.bot.registration_bot.dto.UserDto;
import my.bot.registration_bot.entity.UserEntity;

import static my.bot.registration_bot.text.Texts.*;

@Slf4j
@Service
public class TelegramBot extends TelegramLongPollingBot {

	final BotConfig config;
	//вот это поле было static, но сегодня уже я убрал. 
	//смысл в том, что в эту дто мы наполняем данные, спрашивая их у юзера.
	//а потом при заполнении кидаем в бд
	//вопрос: если одновременно к боту будут обращаться 10 пользователей, то инфомрация
	//внутри статической версии будет постоянно перезаписываться?
	private /*static*/ UserDto currentUser;

	@Autowired
	private UserRepository userRepository;

	public TelegramBot(BotConfig botConfig) {
		this.config = botConfig;
		List<BotCommand> listOfCommands = new ArrayList<>();
		listOfCommands.add(new BotCommand("/start", "Начать использовать бота"));
		try {
			this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
		} catch (TelegramApiException ex) {
			log.error("Error setting bot's command list : " + ex.getMessage());
		}

	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage() && update.getMessage().hasText()) {
			String messageText = update.getMessage().getText().trim();
			long chatId = update.getMessage().getChatId();
			String userFirstName = update.getMessage().getChat().getFirstName();

			if (messageText.equals("/start")) {
				startCommandReceived(chatId, userFirstName);
				sendNewMessage(chatId, REG_HELP_CHOICE);
				currentUser = new UserDto(chatId, update.getMessage().getChat().getUserName(),
						update.getMessage().getChat().getFirstName());
				log.warn(currentUser.toString());
			} else

			if (messageText.matches("^[а-яА-ЯёЁ ]+")) {
				currentUser.setUserFullName(messageText);
				// можно добавить через этот же флаг деление на добавлено или обновлено, но это
				// не обязательно
				sendNewMessage(chatId, "Ваше имя добавлено или обновлено.\n");
				checkIfFullInfoIsProvided(chatId);
				log.warn(currentUser.toString());
				if (!currentUser.isAlreadyRegistred()) {
					sendNewMessage(chatId, "Введите ваш номер телефона, начинающийся с +7:");
				}
			} else

			if (messageText.startsWith("+7")) {
				currentUser.setPhoneNumber(messageText);
				sendNewMessage(chatId, "Ваш номер добавлен или обновлён.\n");
				checkIfFullInfoIsProvided(chatId);
				log.warn(currentUser.toString());
				if (!currentUser.isAlreadyRegistred()) {
					sendNewMessage(chatId, "Введите ссылку на ваш никнейм в Instagram, начинающийся с символа '@':");
				}
			} else

			if (messageText.startsWith("@")) {
				currentUser.setInstagramNickname(messageText);
				sendNewMessage(chatId, "Ваш никнейм в Instagram добавлен или обновлён.\n");
				checkIfFullInfoIsProvided(chatId);
				log.warn(currentUser.toString());
			}

			else {
				sendNewMessage(chatId, DEFAULT);
			}
		} else if (update.hasCallbackQuery()) {
			String callbackData = update.getCallbackQuery().getData();
			long chatId = update.getCallbackQuery().getMessage().getChatId();
			long messageId = update.getCallbackQuery().getMessage().getMessageId();
			if (callbackData.equals(REGISTRATION)) {
				sendNewMessage(chatId, "Введите ваши ФИО:");
			} else if (callbackData.equals(HELP)) {
				editCurrentMessage(chatId, messageId, CONTACTS);
			}
		}
	}

	private void sendNewMessage(long chatId, String textToSend) {
		SendMessage messageToSend = new SendMessage();
		messageToSend.setChatId(String.valueOf(chatId));
		messageToSend.setText(textToSend);
		if (textToSend.equals(REG_HELP_CHOICE)) {
			InlineKeyboardMarkup markupInLine = chooseAnOption();
			messageToSend.setReplyMarkup(markupInLine);
		}
		try {
			execute(messageToSend);
		} catch (TelegramApiException ex) {
			log.error("Не удалось отправить новое сообщение: " + ex.getMessage());
		}
	}

	private void editCurrentMessage(long chatId, long messageId, String textToSend) {
		EditMessageText messageToEdit = new EditMessageText();
		messageToEdit.setChatId(String.valueOf(chatId));
		messageToEdit.setText(textToSend);
		messageToEdit.setMessageId((int) messageId);
		if (textToSend.equals(CONTACTS)) {
			InlineKeyboardMarkup markupInLine = chooseAnOption();
			messageToEdit.setReplyMarkup(markupInLine);
		}
		try {
			execute(messageToEdit);
		} catch (TelegramApiException ex) {
			log.error("Error occurred: " + ex.getMessage());
		}

	}

	private void registerUser(UserDto currentUser) {
		UserEntity userEntity = new UserEntity();
		userEntity.setChatId(currentUser.getChatId());
		userEntity.setFirstName(currentUser.getFirstName());
		userEntity.setUserName(currentUser.getUserName());
		userEntity.setUserFullName(currentUser.getUserFullName());
		userEntity.setPhoneNumber(currentUser.getPhoneNumber());
		userEntity.setInstagramNickname(currentUser.getInstagramNickname());
		userRepository.save(userEntity);
		log.info("Зарегистрирован: " + userEntity);
	}

	private void startCommandReceived(long chatId, String name) {
		String answer = EmojiParser.parseToUnicode("Привет, " + name + "! :blush:");
		sendNewMessage(chatId, answer);
		log.info("Поздоровались с " + name);
	}

	private InlineKeyboardMarkup chooseAnOption() {
		InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
		List<InlineKeyboardButton> rowInLine = new ArrayList<>();
		var registrButton = new InlineKeyboardButton();
		registrButton.setText("Регистрация");
		registrButton.setCallbackData(REGISTRATION);
		var helpButton = new InlineKeyboardButton();
		helpButton.setText("Помощь");
		helpButton.setCallbackData(HELP);
		rowInLine.add(registrButton);
		rowInLine.add(helpButton);
		rowsInLine.add(rowInLine);
		markupInLine.setKeyboard(rowsInLine);
		return markupInLine;
	}

	private void checkIfFullInfoIsProvided(long chatId) {
		if (currentUser.isFull() && !currentUser.isAlreadyRegistred()) {
			sendNewMessage(chatId, YOU_REGISTERED + "\n" + currentUser.toString());
			currentUser.setAlreadyRegistred(true);
			registerUser(currentUser);
		}
	}

	@Override
	public String getBotUsername() {
		return config.getBotName();
	}

	@Override
	public String getBotToken() {
		return config.getToken();
	}

}
