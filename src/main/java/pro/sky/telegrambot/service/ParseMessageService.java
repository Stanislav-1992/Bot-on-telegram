package pro.sky.telegrambot.service;

import pro.sky.telegrambot.exception.IncorrectMessageException;
import pro.sky.telegrambot.model.Notification;
import java.util.Optional;

public interface ParseMessageService {

    Optional<Notification> parseMessage(String notificationBotMessage) throws IncorrectMessageException;
}
