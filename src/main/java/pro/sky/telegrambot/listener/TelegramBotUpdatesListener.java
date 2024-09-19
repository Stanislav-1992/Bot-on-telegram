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
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.NotificationServiceImpl;

import javax.annotation.PostConstruct;
import javax.management.Notification;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

import static org.springframework.data.util.Optionals.ifPresentOrElse;
import static pro.sky.telegrambot.util.CommandConst.*;

@Service
@EnableScheduling
public class TelegramBotUpdatesListener implements UpdatesListener {

    Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final static Pattern PATTERN = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
    private final static DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final NotificationTaskRepository notificationTaskRepository;


    private final TelegramBot telegramBot;
    private final NotificationServiceImpl notificationService;

    public TelegramBotUpdatesListener(NotificationTaskRepository notificationTaskRepository,
                                      TelegramBot telegramBot, NotificationServiceImpl notificationService) {
        this.notificationTaskRepository = notificationTaskRepository;
        this.telegramBot = telegramBot;
        this.notificationService = notificationService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Scheduled(cron ="0 0/1 * * * *")
    public void setNotificationMessage(){
        notificationService.sendNotificationMessage();
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);

            Message message = update.message();

            if (message.text().startsWith(START_CMD)){
                logger.info(START_CMD + " " + LocalDateTime.now());
                notificationService.sendNotificationMessage(getChatId(message), WELCOME + message.from().firstName() + " ");
                notificationService.sendNotificationMessage(getChatId(message), HELP_MSG);
            } else{
                try {
                    notificationService.parseMessage(message).text()).ifPresentOrElse(
                            task -> scheduledNotification(getChatId(message), task),
                            () -> notificationService.sendNotificationMessage(getChatId(message), INVALID_MESSAGE)
                    );
                } catch (IncorrectMessageException i) {
                    notificationService.sendNotificationMessage(getChatId(message),
                            "Сообщение не соответствует требуемому формату!");
                }
            }

            /*Long chatId = update.message().chat().id();
            String message = update.message().text();

            if ("/start".equals(message)){
                notificationService.send(chatId,"Привет хозяин!");
            } else {
                Matcher matcher = PATTERN.matcher(message);
                if (matcher.matches() ){
                    String task = matcher.group(3);
                    LocalDateTime localDateTime = LocalDateTime.parse(matcher.group(1),DATE_TIME_FORMAT);

                    notificationTaskRepository.save(new NotificationTask(
                            task,chatId,localDateTime));

                    notificationService.send(chatId,"Напоминание сохранено");
                } else {
                    notificationService.send(chatId,"Неверный формат сообщения! Попробуйте изменить и отправить снова.");
                }
            }*/
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }

        private void scheduledNotification(Long chatId, Notification notification) {
            notificationService.scheduledNotification(notification, chatId);
            notificationService.sendNotificationMessage(chatId, "The task:" + notification.getMessage() + "is created");
        }

    private long getChatId(Message message) {
        return message.chat().id();
    }
}
