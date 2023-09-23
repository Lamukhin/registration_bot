package my.bot.registration_bot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public interface MessageService {
	public SendMessage createNewMessage(long chatId, String textToSend);
	public EditMessageText editCurrentMessage(long chatId, long messageId, String textToSend);
}
