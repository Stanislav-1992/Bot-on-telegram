package pro.sky.telegrambot.serviceImpl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.Notification;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.NotificationService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationTaskRepository repository;
    private final TelegramBot telegramBot;

    public NotificationServiceImpl(NotificationTaskRepository repository, TelegramBot telegramBot) {
        this.repository = repository;
        this.telegramBot = telegramBot;
    }

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyy HH:mm");

    @Override
    public void sendNotificationMessage() {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Collection<Notification> notifications = repository.findByNotificationDate(currentTime);
        notifications.forEach(task -> {
            sendMessage(task);
            log.info("Notification was sent {}", task);
        });
        repository.saveAll(notifications);
        log.info("Notification were saved");
    }

    @Override
    public void sendMessage(Long chatId, String messageText) {
        SendMessage sendMessage = new SendMessage(chatId, messageText);
        telegramBot.execute(sendMessage);
        log.info("Message was send: {}", messageText);
    }

    @Override
    public void sendMessage(Notification notification) {
        sendMessage(notification.getChatId(), notification.getMessage());
    }
}

