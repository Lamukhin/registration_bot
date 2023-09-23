package my.bot.registration_bot.service.impl;

import static my.bot.registration_bot.text.Texts.*;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import my.bot.registration_bot.dao.UserRepository;
import my.bot.registration_bot.entity.UserEntity;
import my.bot.registration_bot.service.KeyboardMarkupService;
import my.bot.registration_bot.service.MessageService;

@Service
public class MessageServiceImpl implements MessageService {

	@Autowired
	KeyboardMarkupService keyboardMarkupService;

	@Autowired
	UserRepository userRepository;

	@Override
	public SendMessage createNewMessage(long chatId, String textToSend) {
		SendMessage messageToSend = new SendMessage();
		messageToSend.setChatId(String.valueOf(chatId));
		messageToSend.setText(textToSend);
		if (textToSend.equals(REG_OR_HELP_CHOICE)) {
			InlineKeyboardMarkup markupInLine = keyboardMarkupService.registerOrHelpInlineMarkup();
			messageToSend.setReplyMarkup(markupInLine);
		}
		if (textToSend.equals(REGISTRATION)) {
			ReplyKeyboardMarkup keyboardMarkup = keyboardMarkupService.shareContactKeyboardMarkup();
			messageToSend.setReplyMarkup(keyboardMarkup);
		}
		if (textToSend.equals(SEND_INSTAGRAM)) {
			ReplyKeyboardRemove remove = new ReplyKeyboardRemove();
			remove.setRemoveKeyboard(true);
			messageToSend.setReplyMarkup(remove);
		}
		if (textToSend.equals(YOU_HAVE_REGISTERED)) {
			InlineKeyboardMarkup markupInLine = keyboardMarkupService.getLinkInlineMarkup();
			messageToSend.setReplyMarkup(markupInLine);
		}
		if (textToSend.equals(AFTER_EVENT)) {
			InlineKeyboardMarkup markupInLine = keyboardMarkupService.getFileInlineMarkup();
			messageToSend.setReplyMarkup(markupInLine);
		}
		return messageToSend;
	}

	@Override
	public EditMessageText editCurrentMessage(long chatId, long messageId, String textToSend) {
		EditMessageText messageToEdit = new EditMessageText();
		messageToEdit.setChatId(String.valueOf(chatId));
		messageToEdit.setText(textToSend);
		messageToEdit.setMessageId((int) messageId);
		if (textToSend.equals(CONTACTS)) {
			InlineKeyboardMarkup markupInLine = keyboardMarkupService.registerOrHelpInlineMarkup();
			messageToEdit.setReplyMarkup(markupInLine);
		}
		if (textToSend.equals(YOU_REGISTERED_BEFORE)) {
			messageToEdit.setText(
					YOU_REGISTERED_BEFORE + 
					printUserData(chatId) +
					"Ссылка на конференцию: " + LINK +
					"\nНачало 27 сентября ровно в 16:00 (по Мск)." +
					"\n\nЕсли ты хочешь исправить личную информацию, свяжись с нашим администратором через /help");
		}
		return messageToEdit;
	}
	
	@Override
	public SendDocument sendFile(long chatId) {
		File file = new File("E:\\eclipse-workspace\\registration_bot\\src\\main\\resources\\blank_ocenki_sotrudnikov_BF.xlsx");
		SendDocument sendDocumentRequest = new SendDocument();
	    sendDocumentRequest.setChatId(chatId);
	    sendDocumentRequest.setDocument(new InputFile(file));
		return sendDocumentRequest;
	}

	private String printUserData(long chatId) {
		UserEntity user = userRepository.findByChatId(chatId);
		StringBuilder userToString = new StringBuilder();
		userToString
				.append("Никнейм в Telegram: ").append(user.getUserName()).append("\n")
				.append("Телефон: ").append(user.getPhoneNumber()).append("\n")
				.append("Никнейм в Instagram: ").append(user.getInstagramNickname()).append("\n");
		return userToString.toString();
	}


}
