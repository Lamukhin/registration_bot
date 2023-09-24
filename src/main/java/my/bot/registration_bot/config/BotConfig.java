package my.bot.registration_bot.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;



@Configuration
@EnableScheduling
@EnableJpaRepositories("my.bot.registration_bot.dao")
@PropertySource("application.properties")
public class BotConfig {
	
	@Value("${bot.name}")
	String botName;
	
	@Value("${bot.token}")
	String token;
	
	@Value("${bot.admins_user_id}")
	List<String> adminsUserId;
	
	public List<String> getAdminsUserId() {
		return adminsUserId;
	}

	public BotConfig() {}
	
	public String getBotName() {
		return botName;
	}
	public void setBotName(String botName) {
		this.botName = botName;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
}
