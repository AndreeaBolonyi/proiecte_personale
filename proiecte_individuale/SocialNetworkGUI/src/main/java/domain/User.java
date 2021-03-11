package socialnetwork.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User extends Entity<Long>{
    private String firstName;
    private String lastName;
    private List<User> friends;
    private String email;
    private String password;

    public User(String firstName, String lastName,String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.friends=new ArrayList<>();
        this.email=email;
        this.password=password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> aux) { this.friends=aux; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     *  add a friend
     * @param u- User
     */
    public void addFriend(User u){
        this.friends.add(u);
    }

    /**
     *  remove a friend
     * @param u- User
     */
    public void removeFriend(User u){
        if(friends.contains(u))
            this.friends.remove(u);
    }

    @Override
    public String toString() {
        //return "First name: " + this.getFirstName() + ",Last name:" + this.getLastName() + ",Friends:" +listToString();
        return this.getFirstName() + " " + this.getLastName() + " " + this.getEmail();
    }

    private String listToString(){
        String rez="";
        for(User u : friends)
            rez+= u.getFirstName() + " " + u.getLastName() + ",";
        return rez;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User that = (User) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getFriends());
    }
}