package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exception.IncorrectMessageException;
import pro.sky.telegrambot.model.Notification;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;

    private final NotificationTaskRepository notificationTaskRepository;

    private final Pattern pattern = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)");
    private final Pattern patternText = Pattern.compile("(\\D+)+([a-zA-Zа-яА-Я0-9]+)+(\\D+)");

    TelegramBotUpdatesListener(NotificationTaskRepository notificationTaskRepository, TelegramBot telegramBot) {
        this.notificationTaskRepository = notificationTaskRepository;
        this.telegramBot = telegramBot;
    }

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyy HH:mm");

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);

            Matcher matcherText = patternText.matcher(update.message().text());
            if (matcherText.matches()) {
                if (update.message().text().equals("/start")) {
                    telegramBot.execute(new SendMessage(update.message().chat().id(),
                            "Hello " + update.message().chat().firstName()));
                    return;
                } else if (update.message().text().equals("/stop")) {
                    telegramBot.execute(new SendMessage(update.message().chat().id(),
                            "Goodbye " + update.message().chat().firstName()));
                    return;
                } else if (update.message().text().equals("/delete")) {
                    Notification notification = notificationTaskRepository.findById(update.message().chat().id())
                            .orElseThrow(() -> new IncorrectMessageException("Notification not found"));
                    notificationTaskRepository.delete(notification);
                }
                createNotification(update);
            }
            throw new IncorrectMessageException("В данном запросе отсутствует текст!");
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void createNotification(Update update) {

        Matcher matcher = pattern.matcher(update.message().text());
        if (matcher.matches()) {
            LocalDateTime dateTime = LocalDateTime.parse(matcher.group(1), dateTimeFormatter);
            Notification notificationTask = new Notification();
            notificationTask.setNotificationDate(dateTime);
            notificationTask.setChatId(update.message().chat().id());
            notificationTask.setMessage(matcher.group(3));
            notificationTaskRepository.save(notificationTask);
        }
    }
}
