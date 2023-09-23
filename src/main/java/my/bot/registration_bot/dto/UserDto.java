package my.bot.registration_bot.dto;

import lombok.Data;

@Data
public class UserDto {

	private long userId;
	private long chatId;
	
	private String firstName;
	private String lastName;
	private String userName;
	
	private String phoneNumber;
	private String instagramNickname;
	
	private boolean alreadyRegistred = false;
	private boolean alreadyGotInstaNick = false;
	private boolean alreadyGotContact = false;

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
			|| (userId == 0)
			|| (firstName == null)
			|| (userName == null)
			|| (phoneNumber == null)
			|| (instagramNickname == null)) 
		{
			return false;
		} else {			
			return true;
		}
	}

}
