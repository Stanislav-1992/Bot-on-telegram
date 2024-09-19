package pro.sky.telegrambot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TelegramBotApplicationTests {

	@Autowired
	NotificationTaskRepository notificationTaskRepository;

	@SpyBean

	@Test
	void contextLoads() {
	}

}
