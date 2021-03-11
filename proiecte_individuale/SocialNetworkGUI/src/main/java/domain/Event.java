package socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Event extends Entity<Long>{
    private LocalDateTime date;
    private List<User> participants;
    private String description;
    private User owner;

    public Event(LocalDateTime date, String description, List<User> participants,User owner){
        this.date=date;
        this.description=description;
        this.participants=participants;
        this.owner=owner;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public String toString(){
        return description + " on " + date.toString().replace("T"," ") + "\nand the owner is " + owner.getFirstName() + " " + owner.getLastName();
    }
}
