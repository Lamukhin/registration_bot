package my.bot.registration_bot.service.impl;

import static my.bot.registration_bot.service.TelegramBot.*;
import static my.bot.registration_bot.text.Texts.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Contact;

import lombok.extern.slf4j.Slf4j;
import my.bot.registration_bot.dao.UserRepository;
import my.bot.registration_bot.dto.UserDto;
import my.bot.registration_bot.entity.UserEntity;
import my.bot.registration_bot.service.DataTransferService;
import my.bot.registration_bot.service.MessageService;

@Slf4j
@Service
public class DataTransferServiceImpl implements DataTransferService {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	MessageService messageService;

	@Override
	public void registerUser(UserDto currentUser) {
		UserEntity userEntity = new UserEntity();
		userEntity.setChatId(currentUser.getChatId());
		userEntity.setUserId(currentUser.getUserId());

		userEntity.setFirstName(currentUser.getFirstName());
		userEntity.setLastName(currentUser.getLastName());
		userEntity.setUserName(currentUser.getUserName());

		userEntity.setPhoneNumber(currentUser.getPhoneNumber());
		userEntity.setInstagramNickname(currentUser.getInstagramNickname());
		userRepository.save(userEntity);
		log.info("Зарегистрирован: " + userEntity);

	}

	@Override
	public void saveContactToUserDto(long chatId, Contact contact) {
		//я не уверен, что не будет проблем из-за статического импорта из нестатичесого класса
		getUsersAndMessages().get(chatId).setUserId(contact.getUserId());
		getUsersAndMessages().get(chatId).setLastName(contact.getLastName());
		getUsersAndMessages().get(chatId).setPhoneNumber(contact.getPhoneNumber());

	}

	@Override
	public void checkIfFullInfoIsProvided(long chatId) {
		if (getUsersAndMessages().get(chatId).isFull() && !getUsersAndMessages().get(chatId).isAlreadyRegistred()) {
			messageService.createNewMessage(chatId, YOU_HAVE_REGISTERED);
			getUsersAndMessages().get(chatId).setAlreadyRegistred(true);
			registerUser(getUsersAndMessages().get(chatId));
		}
	}

	@Override
	public boolean checkIfRegistered(long chatId) {
		if (userRepository.findByChatId(chatId) == null) {
			return false;
		}
		return true;
	}

}
