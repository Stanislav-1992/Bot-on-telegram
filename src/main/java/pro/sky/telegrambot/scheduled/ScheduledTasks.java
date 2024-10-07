package pro.sky.telegrambot.scheduled;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.service.NotificationService;


@Component
public class ScheduledTasks {

    @Autowired
    private TelegramBot telegramBot;

    private final NotificationService notificationService;

    public ScheduledTasks(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 09 09 * * ?")
    public void run() {
        System.out.println("Good morning");
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void sendNotificationMessage(){
        notificationService.sendNotificationMessage();
    }
}