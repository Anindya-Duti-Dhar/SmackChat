package anindya.sample.smackchat;

import java.util.ArrayList;

public class MessageEvent {

    ArrayList<ChatItem> chatItem;

    public MessageEvent(ArrayList<ChatItem> chatItem) {
        this.chatItem = chatItem;
    }
}