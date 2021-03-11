package socialnetwork.events;

import socialnetwork.domain.User;

public class UserChangeEvent implements Event{
    private EventType type;
    private User data, oldData;

    public UserChangeEvent(EventType type, User Data){
        this.type=type;
        this.data=Data;
    }

    public UserChangeEvent(EventType type, User Data,User oldData){
        this.type=type;
        this.data=Data;
        this.oldData=oldData;
    }

    public EventType getType() {
        return type;
    }

    public User getData() {
        return data;
    }

    public User getOldData() {
        return oldData;
    }
}
