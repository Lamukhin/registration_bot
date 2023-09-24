package my.bot.registration_bot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public interface MessageService {
	SendMessage createNewMessage(long chatId, String textToSend);
	EditMessageText editCurrentMessage(long chatId, long messageId, String textToSend);
	SendDocument sendBlankFile(long chatId);
	SendDocument sendCsvFile(long chatId);
}
