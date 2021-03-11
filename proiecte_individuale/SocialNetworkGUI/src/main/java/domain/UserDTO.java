package socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class UserDTO {
    private String firstName;
    private String lastName;
    private LocalDateTime date;

    public UserDTO(String firstName, String lastName, LocalDateTime date){
        this.firstName=firstName;
        this.lastName=lastName;
        this.date=date;
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return firstName + '|' + lastName + '|' + date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(firstName, userDTO.firstName) &&
                Objects.equals(lastName, userDTO.lastName) &&
                Objects.equals(date, userDTO.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, date);
    }
}
