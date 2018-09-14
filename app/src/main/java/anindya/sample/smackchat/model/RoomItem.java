package anindya.sample.smackchat.model;


import java.util.List;

public class RoomItem {

    public String jid;
    public String name;
    public String owner;
    public String nick;
    public String description;
    public int occupantsCount;
    public List<String> occupants;

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOccupantsCount() {
        return occupantsCount;
    }

    public void setOccupantsCount(int occupantsCount) {
        this.occupantsCount = occupantsCount;
    }

    public List<String> getOccupants() {
        return occupants;
    }

    public void setOccupants(List<String> occupants) {
        this.occupants = occupants;
    }
}
