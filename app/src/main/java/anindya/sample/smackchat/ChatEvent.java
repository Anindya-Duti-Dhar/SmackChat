package anindya.sample.smackchat;

/**
 * Created by user on 5/8/2017.
 */

public class ChatEvent {
    public final String from;
    public final String message;
    public final String subject;

    public ChatEvent(String from, String message, String subject) {
        this.message = message;
        this.from = from;
        this.subject = subject;
    }
}
