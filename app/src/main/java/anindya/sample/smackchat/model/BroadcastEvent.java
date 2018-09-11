package anindya.sample.smackchat.model;



public class BroadcastEvent {

    public String item;
    public String category;
    public String message;
    public ChatEvent chatEvent;

    public BroadcastEvent(String item, String category, String message) {
        this.item = item;
        this.category = category;
        this.message = message;
    }

    public BroadcastEvent(ChatEvent chatEvent) {
        this.chatEvent = chatEvent;
    }

    public BroadcastEvent(String item, ChatEvent chatEvent) {
        this.item = item;
        this.chatEvent = chatEvent;
    }
}


