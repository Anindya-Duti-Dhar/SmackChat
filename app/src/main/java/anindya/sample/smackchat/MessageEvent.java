package anindya.sample.smackchat;

public class MessageEvent {

    public final String from;
    public final String message;

    public MessageEvent(String from, String message) {
        this.message = message;
        this.from = from;
    }
}