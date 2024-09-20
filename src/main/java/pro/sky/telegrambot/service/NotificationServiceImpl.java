package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exception.IncorrectMessageException;
import pro.sky.telegrambot.model.Notification;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private static final String REGEX_MSG = "(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)";

    private final NotificationTaskRepository repository;
    private final TelegramBot telegramBot;

    public NotificationServiceImpl(NotificationTaskRepository repository, TelegramBot telegramBot) {
        this.repository = repository;
        this.telegramBot = telegramBot;
    }

    @Override
    public void scheduledNotification(pro.sky.telegrambot.model.Notification notification, Long chatId) {
        notification.setChatId(chatId);
        Notification savedNotification = repository.save(notification);
        log.info("Notification {} scheduled", savedNotification);
    }

    @Override
    public Optional<Notification> parseMessage(String message) throws IncorrectMessageException {
        Notification notification = null;
        Pattern pattern = Pattern.compile(REGEX_MSG);
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()){
            String messageToSave = matcher.group(3);
            LocalDateTime notificationLocalDateTime = LocalDateTime.parse(matcher.group(1),
                    DateTimeFormatter.ofPattern("dd.MM.yyy HH:mm"));
            if (notificationLocalDateTime.isAfter(LocalDateTime.now())) {
                notification = new Notification(messageToSave, notificationLocalDateTime);
                log.info("Saving {} to DB", notification);
                repository.save(notification);
            } else{
                log.error("Date is incorrect");
                throw new IncorrectMessageException("Incorrect date");
            }
        }
        return Optional.ofNullable(notification);
    }

    @Override
    public void sendNotificationMessage() {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Collection<Notification> notifications = repository.findByNotificationDate(currentTime);
        notifications.forEach(task -> {
            sendMessage(task);
            task.setAsSent();
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

