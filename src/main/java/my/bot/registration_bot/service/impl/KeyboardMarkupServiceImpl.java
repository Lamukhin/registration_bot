package my.bot.registration_bot.service.impl;

import static my.bot.registration_bot.text.Texts.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import my.bot.registration_bot.service.KeyboardMarkupService;

@Service
public class KeyboardMarkupServiceImpl implements KeyboardMarkupService {

	@Override
	public InlineKeyboardMarkup registerOrHelpInlineMarkup() {
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

	@Override
	public ReplyKeyboardMarkup shareContactKeyboardMarkup() {
		ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
		List<KeyboardRow> keyboardRows = new ArrayList<>();
		KeyboardRow row = new KeyboardRow();
		KeyboardButton button = new KeyboardButton();
		button.setText("Поделиться контактами");
		button.setRequestContact(true);
		row.add(button);
		keyboardRows.add(row);
		keyboardMarkup.setResizeKeyboard(true);
		keyboardMarkup.setOneTimeKeyboard(true);
		keyboardMarkup.setKeyboard(keyboardRows);
		return keyboardMarkup;
	}

	@Override
	public InlineKeyboardMarkup getLinkInlineMarkup() {
		InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
		List<InlineKeyboardButton> rowInLine = new ArrayList<>();
		var getLinkButton = new InlineKeyboardButton();
		getLinkButton.setText("Получить ссылку на конференцию");
		getLinkButton.setUrl(LINK);
		rowInLine.add(getLinkButton);
		rowsInLine.add(rowInLine);
		markupInLine.setKeyboard(rowsInLine);
		return markupInLine;
	}
	
	@Override
	public InlineKeyboardMarkup getFileInlineMarkup() {
		InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
		List<InlineKeyboardButton> rowInLine = new ArrayList<>();
		var getLinkButton = new InlineKeyboardButton();
		getLinkButton.setText("Получить файл");
		getLinkButton.setCallbackData(GET_FILE);
		rowInLine.add(getLinkButton);
		rowsInLine.add(rowInLine);
		markupInLine.setKeyboard(rowsInLine);
		return markupInLine;
	}

}
