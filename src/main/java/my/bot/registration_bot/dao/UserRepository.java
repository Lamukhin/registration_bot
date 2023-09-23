package my.bot.registration_bot.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import my.bot.registration_bot.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long>{
	public UserEntity findByChatId(long chatId);
	
}
