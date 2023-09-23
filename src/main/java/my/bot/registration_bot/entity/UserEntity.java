package my.bot.registration_bot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity(name="usersDataTable")
@Data
public class UserEntity {
	@Id
	private long userId; //уникальный id чата между пользователем и ботом
	private long chatId; //уникальный id пользователя, присвоенный ему при регистрации
	
	private String firstName; //поле в "имя", указанное в тг
	private String lastName; //поле в "фамилия", указанное в тг
	private String userName; //никнейм в телеграме, например @durov
	
	private String phoneNumber; 
	private String instagramNickname;
	
	
}
