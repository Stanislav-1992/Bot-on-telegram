package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exception.IncorrectMessageException;
import pro.sky.telegrambot.model.Notification;
import pro.sky.telegrambot.service.NotificationService;
import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import static pro.sky.telegrambot.util.CommandConst.*;

@Service
@EnableScheduling
public class TelegramBotUpdatesListener implements UpdatesListener {

    Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;
    private final NotificationService notificationService;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationService notificationService) {
        this.telegramBot = telegramBot;
        this.notificationService = notificationService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void sendNotificationMessage(){
        notificationService.sendNotificationMessage();
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {

            logger.info("Processing update: {}", update);
            Message message = update.message();

            if (message.text().startsWith(START_CMD)) {
                logger.info(START_CMD + " " + LocalDateTime.now());
                notificationService.sendMessage(getChatId(message), WELCOME + message.from().firstName() + " ");
                notificationService.sendMessage(getChatId(message), HELP_MSG);
            } else {
                try {
                    notificationService.parseMessage(message.text()).ifPresentOrElse(
                            task -> scheduledNotification(getChatId(message), task),
                            () -> notificationService.sendMessage(getChatId(message), INVALID_MESSAGE)
                    );
                } catch (IncorrectMessageException i) {
                    notificationService.sendMessage(getChatId(message),
                            "Сообщение не соответствует требуемому формату!");
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void scheduledNotification(Long chatId, Notification notification) {
        notificationService.scheduledNotification(notification, chatId);
        notificationService.sendMessage(chatId, "The task:" + notification.getMessage() + "is created");
    }
    private long getChatId(Message message) {
        return message.chat().id();
    }
}
