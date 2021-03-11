package socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Message extends Entity<Long>{
    private User from;
    private List<User> to;
    String message;
    LocalDateTime date;
    Message reply;

    public Message(User from, List<User> to, String message, LocalDateTime date,Message reply) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.reply=reply;
    }

    @Override
    public String toString() {
        return this.getId() + ";" + from.getId() + "," + from.getFirstName()+ "," + from.getLastName() + ";" + listToString() + ";" + message + ";" + date + ";" +reply;
    }

    private String listToString(){
        String rez="";
        for(User u : to)
            rez+= u.getId() + " " + u.getFirstName() + " " + u.getLastName() + ",";
        return rez;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return Objects.equals(from, message1.from) &&
                Objects.equals(to, message1.to) &&
                Objects.equals(message, message1.message) &&
                Objects.equals(date, message1.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, message, date);
    }

    public Message getReply(){
        return this.reply;
    }
    public void setReply(Message m){
        this.reply=m;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public List<User> getTo() {
        return to;
    }

    public void setTo(List<User> to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
