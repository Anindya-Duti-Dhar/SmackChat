package anindya.sample.smackchat;

/**
 * Created by user on 5/8/2017.
 */

public class ChatEvent {
         public final String from;
    public final String message;

    public ChatEvent(String from, String message) {
        this.message = message;
        this.from = from;
    }
}
