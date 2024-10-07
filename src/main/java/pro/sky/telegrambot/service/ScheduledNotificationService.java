package pro.sky.telegrambot.service;

import pro.sky.telegrambot.model.Notification;

public interface ScheduledNotificationService {

    void scheduledNotification(Notification notification, Long chatId);
}
