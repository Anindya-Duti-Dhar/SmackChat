package anindya.sample.smackchat.model;


import org.jivesoftware.smack.packet.Message;

public class ChatItem {

    public Message.Type chatMessageType;
    public String chatSubject;
    public String chatText;
    public String chatMessageID;
    public String chatTimeStamp;
    public String chatUserName;
    public boolean chatIsTapped;

    public ChatItem(Message.Type chatMessageType, String chatSubject, String chatText, String chatMessageID, String chatTimeStamp, String chatUserName, boolean chatIsTapped) {
        this.chatMessageType = chatMessageType;
        this.chatSubject = chatSubject;
        this.chatText = chatText;
        this.chatMessageID = chatMessageID;
        this.chatTimeStamp = chatTimeStamp;
        this.chatUserName = chatUserName;
        this.chatIsTapped = chatIsTapped;
    }

    public ChatItem() {
    }

    public Message.Type getChatMessageType() {
        return chatMessageType;
    }

    public void setChatMessageType(Message.Type chatMessageType) {
        this.chatMessageType = chatMessageType;
    }

    public String getChatSubject() {
        return chatSubject;
    }

    public void setChatSubject(String chatSubject) {
        this.chatSubject = chatSubject;
    }

    public String getChatText() {
        return chatText;
    }

    public void setChatText(String chatText) {
        this.chatText = chatText;
    }

    public String getChatMessageID() {
        return chatMessageID;
    }

    public void setChatMessageID(String chatMessageID) {
        this.chatMessageID = chatMessageID;
    }

    public String getChatTimeStamp() {
        return chatTimeStamp;
    }

    public void setChatTimeStamp(String chatTimeStamp) {
        this.chatTimeStamp = chatTimeStamp;
    }

    public String getChatUserName() {
        return chatUserName;
    }

    public void setChatUserName(String chatUserName) {
        this.chatUserName = chatUserName;
    }

    public boolean isChatIsTapped() {
        return chatIsTapped;
    }

    public void setChatIsTapped(boolean chatIsTapped) {
        this.chatIsTapped = chatIsTapped;
    }
}
