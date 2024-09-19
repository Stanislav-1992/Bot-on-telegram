package pro.sky.telegrambot.service;

import pro.sky.telegrambot.exception.IncorrectMessageException;

import javax.management.Notification;
import java.util.Optional;

public interface NotificationService {

    void scheduledNotification(Notification notification, Long chatId);

    Optional<Notification> parseMessage(String notificationBotMessage) throws IncorrectMessageException;

    void sendNotificationMessage();

    void sendMessage(Long chatId, String messageText);

    void sendMessage(Notification notification);

}
