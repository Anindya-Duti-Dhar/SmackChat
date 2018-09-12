package anindya.sample.smackchat.model;


import org.jivesoftware.smack.packet.Message;

public class ChatEvent {

    public Message.Type type;
    public String subject;
    public String message;
    public String messageID;
    public String from;
    public String timeStamp;

    public ChatEvent(Message.Type type, String subject, String message, String messageID, String timeStamp, String from) {
        this.type = type;
        this.subject = subject;
        this.message = message;
        this.messageID = messageID;
        this.from = from;
        this.timeStamp = timeStamp;
    }
}
