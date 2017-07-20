package anindya.sample.smackchat.model;


public class ChatEvent {
    public final String from;
    public final String message;
    public final String subject;
    public final String messageID;

    public ChatEvent(String from, String message, String subject, String messageID) {
        this.message = message;
        this.from = from;
        this.subject = subject;
        this.messageID = messageID;
    }
}
