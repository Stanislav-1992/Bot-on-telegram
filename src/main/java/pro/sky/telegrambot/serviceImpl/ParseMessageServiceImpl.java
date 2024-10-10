package pro.sky.telegrambot.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.sky.telegrambot.exception.IncorrectMessageException;
import pro.sky.telegrambot.model.Notification;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.ParseMessageService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseMessageServiceImpl implements ParseMessageService {

    private final Logger log = LoggerFactory.getLogger(ParseMessageServiceImpl.class);
    private static final String REGEX_MSG = "(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)";

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyy HH:mm");

    private final NotificationTaskRepository repository;

    public ParseMessageServiceImpl(NotificationTaskRepository repository) {
        this.repository = repository;
    }


    @Override
    public Optional<Notification> parseMessage(String message) throws IncorrectMessageException {
        Notification notification = null;
        Pattern pattern = Pattern.compile(REGEX_MSG);
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            String messageToSave = matcher.group(3);
            LocalDateTime notificationLocalDateTime = LocalDateTime.parse(matcher.group(1), dateTimeFormatter);
            if (notificationLocalDateTime.isAfter(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))) {
                notification = new Notification(messageToSave, notificationLocalDateTime);
                log.info("Saving {} to DB", notification);
                repository.save(notification);
            } else {
                log.error("Date is incorrect");
                throw new IncorrectMessageException("Incorrect date");
            }
        }
        return Optional.ofNullable(notification);
    }
}
