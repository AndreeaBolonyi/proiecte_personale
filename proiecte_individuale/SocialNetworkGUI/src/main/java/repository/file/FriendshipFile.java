package socialnetwork.repository.file;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.Validator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FriendshipFile extends  AbstractFileRepository<Tuple<Long,Long>,Friendship>{

    public FriendshipFile(String fileName, Validator<Friendship> validator){
        super(fileName, validator);
    }

    @Override
    public Friendship extractEntity(List<String> attributes){
        String date=attributes.get(2).replace("T"," ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Friendship f =new Friendship(Long.parseLong(attributes.get(0)), Long.parseLong(attributes.get(1)), LocalDateTime.parse(date,formatter));
        f.setId(new Tuple<>(Long.parseLong(attributes.get(0)), Long.parseLong(attributes.get(1))));
        return f;
    }

    @Override
    protected String createEntityAsString(Friendship entity){
        return entity.toString();
    }
}
