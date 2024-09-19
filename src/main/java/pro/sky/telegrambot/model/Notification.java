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
    private LocalDateTime notificationDate;
    private LocalDateTime notificationSent;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status = NotificationStatus.SCHEDULED;

    public Notification() {
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

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(LocalDateTime notificationDate) {
        this.notificationDate = notificationDate;
    }

    public LocalDateTime getNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(LocalDateTime notificationSent) {
        this.notificationSent = notificationSent;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public void setAsSent() {
        this.status = NotificationStatus.SENT;
        this.notificationSent = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        Notification that = (Notification) o;
        return Objects.equals(id, that.id) && Objects.equals(getChatId(), that.getChatId()) &&
                Objects.equals(getMessage(), that.getMessage()) &&
                Objects.equals(getNotificationDate(), that.getNotificationDate()) &&
                Objects.equals(getNotificationSent(), that.getNotificationSent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, getChatId(), getMessage(), getNotificationDate(), getNotificationSent(), getStatus);
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", message='" + message + '\'' +
                ", notificationDate=" + notificationDate +
                ", notificationSent=" + notificationSent +
                ", status=" + status +
                '}';
    }
}

