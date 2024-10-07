package pro.sky.telegrambot.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.sky.telegrambot.model.Notification;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.ScheduledNotificationService;

public class ScheduledNotificationServiceImpl implements ScheduledNotificationService {

    private final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationTaskRepository repository;

    public ScheduledNotificationServiceImpl(NotificationTaskRepository repository) {
        this.repository = repository;
    }

    public void scheduledNotification(Notification notification, Long chatId) {
        Notification savedNotification = repository.save(notification);
        log.info("Notification {} scheduled", savedNotification);
    }
}
