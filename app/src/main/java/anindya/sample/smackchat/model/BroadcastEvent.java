package anindya.sample.smackchat.model;



public class BroadcastEvent {

    public String item;
    public String category;
    public String message;
    public ChatItem chatItem;

    public BroadcastEvent(String item, String category, String message) {
        this.item = item;
        this.category = category;
        this.message = message;
    }

    public BroadcastEvent(String item, ChatItem chatItem) {
        this.item = item;
        this.chatItem = chatItem;
    }
}


