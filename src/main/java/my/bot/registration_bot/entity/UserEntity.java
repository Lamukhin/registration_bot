package my.bot.registration_bot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity(name="usersDataTable")
@Data
public class UserEntity {
	@Id
	private long chatId;
	private String firstName;
	private String userName;
	private String userFullName;
	private String phoneNumber;
	private String instagramNickname;
	
	
}
