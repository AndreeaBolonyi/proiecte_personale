package socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Request extends Entity<Long>{
    private Long id1;
    private Long id2;
    private String status;
    private LocalDateTime date;

    public Request(Long id1, Long id2,String status,LocalDateTime date){
        this.id1=id1;
        this.id2=id2;
        this.status=status;
        this.date=date;
    }

    @Override
    public String toString() {
        return this.getId() + ";" + this.id1 + ";" + this.id2 + ";" + status + ";" + date;
    }

    public Long getId1() {
        return id1;
    }

    public void setId1(Long id1) {
        this.id1 = id1;
    }

    public Long getId2() {
        return id2;
    }

    public void setId2(Long id2) {
        this.id2 = id2;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDate(){ return this.date;}

    public void setDate(LocalDateTime date){ this.date = date;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return Objects.equals(id1, request.id1) &&
                Objects.equals(id2, request.id2) &&
                Objects.equals(status, request.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id1, id2, status);
    }
}
