package my.bot.registration_bot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


import lombok.extern.slf4j.Slf4j;
import my.bot.registration_bot.service.TelegramBot;

@Slf4j
@Component
public class BotInitializer {
	@Autowired
	TelegramBot bot;
	@EventListener({ContextRefreshedEvent.class})
	public void init() throws TelegramApiException {
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
		try {
			telegramBotsApi.registerBot(bot);
		} catch (TelegramApiException ex) {
			log.error("Error occurred: " + ex.getMessage());
		}
	}
	

}
