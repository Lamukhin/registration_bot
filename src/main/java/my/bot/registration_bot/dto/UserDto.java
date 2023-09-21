package my.bot.registration_bot.dto;

import lombok.Data;

@Data
public class UserDto {

	private long chatId;
	private String userName;
	private String firstName;
	private String userFullName;
	private String phoneNumber;
	private String instagramNickname;
	private boolean alreadyRegistred = false;

	public UserDto() {
	}

	public UserDto(long chatId, String userName, String firstName) {
		super();
		this.chatId = chatId;
		this.firstName = firstName;
		this.userName = userName;
	}

	public boolean isFull() {
		if ((chatId == 0) 
			|| (userName == null)
			|| (firstName == null)
			|| (userFullName == null)
			|| (phoneNumber == null)
			|| (instagramNickname == null)) 
		{
			return false;
		} else {			
			return true;
		}
	}

}
