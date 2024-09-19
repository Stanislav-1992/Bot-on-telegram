package pro.sky.telegrambot.exception;

public class IncorrectMessageException extends RuntimeException {

    public IncorrectMessageException(String message) {
        super("Incorrect message: " + message);
    }
}
