package my.bot.registration_bot.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
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

	@Autowired
	private MessageService messageService;
	@Autowired
	private DataTransferService dataTransferService;
	@Autowired
	private UserRepository userRepository;

	final BotConfig config;

	// Long - это chatId, чтобы бот понимал, из какого чата ему "пришло".
	// UserDto - все нужные данные, получаемые от пользователя в процессе общения с
	// ботом.

	private static Map<Long, UserDto> usersAndMessages = new HashMap<>();

	// инициализация бота с конфигурацией и листом команд
	public TelegramBot(BotConfig botConfig) {
		this.config = botConfig;
		List<BotCommand> listOfCommands = new ArrayList<>();
		listOfCommands.add(new BotCommand("/start", "Начать использовать бота"));
		listOfCommands.add(new BotCommand("/help", "Обратиться за помощью"));
		try {
			this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
		} catch (TelegramApiException ex) {
			log.error("Error setting bot's command list : " + ex.getMessage());
		}
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (isText(update)) {
			reactionOnText(update);
		} else if (isCallback(update)) {
			reactionOnCallback(update);
		} else if (isContact(update)) {
			reactionOnContact(update);
		}
	}

	private void reactionOnText(Update update) {
		String messageText = update.getMessage().getText().trim();
		long chatId = update.getMessage().getChatId();
		String userFirstName = update.getMessage().getChat().getFirstName();
		String userNickname = update.getMessage().getChat().getUserName();

		if (messageText.equals("/start")) {
			startWorking(chatId, userFirstName, userNickname);
		} else if (messageText.equals("/help")) {
			sendNewMessage(chatId, CONTACTS);
		} else if (messageText.startsWith("@") && !checkCyrillic(messageText)) {
			instagramNickHandling(chatId, messageText);
		}
	}

	private void reactionOnCallback(Update update) {
		String callbackData = update.getCallbackQuery().getData();
		long chatId = update.getCallbackQuery().getMessage().getChatId();
		long messageId = update.getCallbackQuery().getMessage().getMessageId();

		if (callbackData.equals(REGISTRATION)) {
			registrationCallbackHandling(chatId, messageId);
		} else if (callbackData.equals(HELP)) {
			editCurrentMessage(chatId, messageId, CONTACTS);
		}
	}

	private void reactionOnContact(Update update) {
		long chatId = update.getMessage().getChatId();
		if (checkIfRightContactRecieved(update)) {
			if (usersAndMessages.get(chatId).isAlreadyGotContact() == false) {
				dataTransferService.saveContactToUserDto(chatId, update.getMessage().getContact());
				usersAndMessages.get(chatId).setAlreadyGotContact(true);
				sendNewMessage(chatId, SEND_INSTAGRAM);
				log.warn("Получили контакт, вот он: " + update.getMessage().getContact().toString());
			} else {
				sendNewMessage(chatId, DEFAULT);
			}
		} else {
			sendNewMessage(chatId, "Отправьте ваш контакт для дальнейшей регистрации");
		}
	}

	private void registrationCallbackHandling(long chatId, long messageId) {
		if (dataTransferService.checkIfRegistered(chatId)) {
			editCurrentMessage(chatId, messageId, YOU_REGISTERED_BEFORE);
		} else {
			sendNewMessage(chatId, REGISTRATION);
		}

	}

	private void instagramNickHandling(long chatId, String messageText) {
		if (usersAndMessages.get(chatId).isAlreadyGotInstaNick() == false) {
			usersAndMessages.get(chatId).setInstagramNickname(messageText);
			dataTransferService.checkIfFullInfoIsProvided(chatId);
			usersAndMessages.get(chatId).setAlreadyGotInstaNick(true);
			sendNewMessage(chatId, YOU_HAVE_REGISTERED);
			log.warn("Итоговое ДТО: " + usersAndMessages.get(chatId).toString());
		} else {
			sendNewMessage(chatId, DEFAULT);
		}
	}

	private void startWorking(long chatId, String userFirstName, String userNickname) {
		String hello = EmojiParser.parseToUnicode("Привет, " + userFirstName + "! :blush:");
		sendNewMessage(chatId, hello);
		log.info("Поздоровались с " + userFirstName);

		sendNewMessage(chatId, REG_OR_HELP_CHOICE);
		getUsersAndMessages().put(chatId, new UserDto(chatId, userNickname, userFirstName));
		log.warn("Сохранили первичную инфу о юзере:" + usersAndMessages.get(chatId).toString());

	}

	public static boolean checkCyrillic(String input) {
		Pattern pattern = Pattern.compile("[а-яА-ЯёЁ]");
		Matcher matcher = pattern.matcher(input);
		return matcher.find();
	}

	/*
	 * Этот метод призван проверить (хоть и не на 100%), что полученный контакт
	 * принадлежит тому, кто его отправил. Потому что по факту ограничения на это в
	 * ТГ нет.
	 */
	private boolean checkIfRightContactRecieved(Update update) {
		String firstNameOfSender = update.getMessage().getChat().getFirstName();
		String firstNameOfRecievedContact = update.getMessage().getContact().getFirstName();
		return firstNameOfRecievedContact.equals(firstNameOfSender);
	}

	private boolean isContact(Update update) {
		return update.hasMessage() && update.getMessage().hasContact();
	}

	private boolean isCallback(Update update) {
		return update.hasCallbackQuery();
	}

	private boolean isText(Update update) {
		return update.hasMessage() && update.getMessage().hasText();
	}

	private void sendNewMessage(long chatId, String textToSend) {
		SendMessage messageToSend = messageService.createNewMessage(chatId, textToSend);
		try {
			execute(messageToSend);
		} catch (TelegramApiException ex) {
			log.error("Не удалось отправить новое сообщение: " + ex.getMessage());
		}
	}

	private void editCurrentMessage(long chatId, long messageId, String textToSend) {
		EditMessageText messageToEdit = messageService.editCurrentMessage(chatId, messageId, textToSend);
		try {
			execute(messageToEdit);
		} catch (TelegramApiException ex) {
			log.error("Не удалось отредактировать сообщение: " + ex.getMessage());
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

	public static Map<Long, UserDto> getUsersAndMessages() {
		return usersAndMessages;
	}

	/*
	 * // для тестов
	 * 
	 * @Scheduled(cron = "0 * * * * *") private void sendNotificationTest() { var
	 * users = userRepository.findAll(); for (UserEntity user : users) {
	 * sendNewMessage(user.getChatId(), HALF_AN_HOUR); } }
	 */

	// за 3 дня: 13:00 (в Москве будет 16:00), UTC+3, 24.09.2023
	@Scheduled(cron = "0 0 13 24 9 ?", zone = "UTC")
	private void sendNotification_BeforeThreeDays() {
		var users = userRepository.findAll();
		for (UserEntity user : users) {
			sendNewMessage(user.getChatId(), THREE_DAYS_BEFORE);
		}
	}

	// в день мероприятия: 06:00 (в Москве будет 09:00), UTC+3, 27.09.2023
	@Scheduled(cron = "0 0 6 27 9 ?", zone = "UTC")
	private void sendNotification_Today() {
		var users = userRepository.findAll();
		for (UserEntity user : users) {
			sendNewMessage(user.getChatId(), TODAY);
		}
	}

	// за 30 минут до начала: 12:30 (в Москве будет 15:30), UTC+3, 27.09.2023
	@Scheduled(cron = "0 30 12 27 9 ?", zone = "UTC")
	private void sendNotification_InHalfAnHour() {
		var users = userRepository.findAll();
		for (UserEntity user : users) {
			sendNewMessage(user.getChatId(), HALF_AN_HOUR);
		}
	}

	// после ивента: 15:00 (в Москве будет 18:00), UTC+3, 27.09.2023
	@Scheduled(cron = "0 0 15 27 9 ?", zone = "UTC")
	private void sendNotification_After() {
		var users = userRepository.findAll();
		for (UserEntity user : users) {
			//реализовать передачу файла в sendNewMessage, комменты написал
			sendNewMessage(user.getChatId(), AFTER_EVENT);
		}
	}
}