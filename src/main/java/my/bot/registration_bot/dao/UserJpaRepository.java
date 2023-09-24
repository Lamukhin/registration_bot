package my.bot.registration_bot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import my.bot.registration_bot.entity.UserEntity;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
	UserEntity findByChatId(long chatId);

	/*@Query(value = "COPY (SELECT * from users_data_table) To 'E:\\eclipse-workspace\\registration_bot\\src\\main\\resources\\output.csv' (FORMAT CSV, ENCODING 'Windows-1251');",
			nativeQuery = true)
	void importAllDataToCvsFile(long chatId);*/

}
