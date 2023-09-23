package my.bot.registration_bot.service;

import org.telegram.telegrambots.meta.api.objects.Contact;

import my.bot.registration_bot.dto.UserDto;

public interface DataTransferService {
	boolean checkIfRegistered(long chatId);
	void registerUser(UserDto currentUser);
	void checkIfFullInfoIsProvided(long chatId);
	void saveContactToUserDto(long chatId, Contact contact);
	
}
