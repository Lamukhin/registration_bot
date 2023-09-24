package my.bot.registration_bot.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public interface KeyboardMarkupService {
	InlineKeyboardMarkup registerOrHelpInlineMarkup();
	ReplyKeyboardMarkup shareContactKeyboardMarkup();
	InlineKeyboardMarkup getLinkInlineMarkup();
	InlineKeyboardMarkup getFileInlineMarkup();
	InlineKeyboardMarkup yesOrNoChoice();
}
