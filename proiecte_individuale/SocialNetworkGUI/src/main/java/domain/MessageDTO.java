package socialnetwork.domain;

import java.time.LocalDateTime;

public class MessageDTO extends Entity<Long>{
    private User from;
    private User to;
    private String message;
    private LocalDateTime date;
    private Message reply;

    public MessageDTO(User from, User to, String message, LocalDateTime date, Message reply) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.reply = reply;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
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

    public Message getReply() {
        return reply;
    }

    public void setReply(Message Reply) {
        this.reply = Reply;
    }

    @Override
    public String toString() {
        /*if(reply == null)
            return from.getFirstName() + " " + from.getLastName() + " sent "
                + message + " to " + to.getFirstName() + " " + to.getLastName() +
                " on " + date.toString().replace("T"," ");
        else
            return from.getFirstName() + " " + from.getLastName() + " sent "
                + message + " to " + to.getFirstName() + " " + to.getLastName() +
                " on " + date.toString().replace("T"," ") +
                " replied to message " + reply.getMessage();*/
        if(reply == null)
            return message + "\n" + date.toString().replace("T"," ");
        else
            return "Replied to:" + reply.getMessage() + "\n" + message + "\n" + date.toString().replace("T"," ");
    }
}
