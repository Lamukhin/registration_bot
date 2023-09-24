package my.bot.registration_bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

@Configuration
@Data
@PropertySource("application.properties")
public class ConnectionConfig {
	
	@Value("${db.place_to_save}")
	String linkToSave;
	
	@Value("${spring.datasource.url}")
	String dbUrl;
	
	@Value("${spring.datasource.username}")
	String dbUsername;
	
	@Value("${spring.datasource.password}")
	String dbPassword;
	
	@Value("${file.upload}")
	String blankFile;
}
