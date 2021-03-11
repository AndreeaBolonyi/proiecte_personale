package socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.Objects;


public class Friendship extends Entity<Tuple<Long,Long>> {
    LocalDateTime date;
    Tuple<Long,Long> tuple;

    public Friendship(Long id1, Long id2, LocalDateTime date) {
        this.tuple=new Tuple<>(id1,id2);
        this.date=date;
    }

    /*
        return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }


    @Override
    public String toString(){
        return tuple.toString() + ";" + date.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(tuple, that.tuple);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, tuple);
    }
}
