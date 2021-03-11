package socialnetwork.domain;

import java.time.LocalDateTime;

public class RequestDTO {
    private Long id1;
    private User user;
    private String status;
    private LocalDateTime date;

    public RequestDTO(Long id1, User user, String status, LocalDateTime date){
        this.id1=id1;
        this.user=user;
        this.status=status;
        this.date=date;
    }

    public Long getId1() {
        return id1;
    }

    public void setId1(Long id1) {
        this.id1 = id1;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "RequestDTO{" +
                "id1=" + id1 +
                ", user=" + user +
                ", status='" + status + '\'' +
                ", date=" + date +
                '}';
    }
}
