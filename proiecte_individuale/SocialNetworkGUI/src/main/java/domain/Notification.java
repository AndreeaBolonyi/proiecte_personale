package socialnetwork.domain;

public class Notification extends Entity<Long>{
    private String message;
    private Event event;

    public Notification(String m, Event e){
        this.message=m;
        this.event=e;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return message;
    }
}
