package pro.sky.telegrambot.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "chat_id")
    private Long chatId;
    private String message;
    @Column(name = "notification_date")
    private LocalDateTime notificationDate;


    public void setMessage(String message) {
        this.message = message;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Notification() {
    }

    public void setNotificationDate(LocalDateTime notificationDate) {
        this.notificationDate = notificationDate;
    }

    public Notification(String notificationMessage, LocalDateTime notificationDate) {
        this.message = notificationMessage;
        this.notificationDate = notificationDate;
    }


    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getNotificationDate() {
        return notificationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(getChatId(), that.getChatId()) &&
                Objects.equals(getMessage(), that.getMessage()) &&
                Objects.equals(getNotificationDate(), that.getNotificationDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, getChatId(), getMessage(), getNotificationDate());
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", message='" + message + '\'' +
                ", notificationDate=" + notificationDate +
                ", notificationSent=" +
                ", status=" +
                '}';
    }
}

