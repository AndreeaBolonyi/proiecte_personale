package socialnetwork.repository.file;

import socialnetwork.domain.Message;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.Validator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MessageFile extends AbstractFileRepository<Long, Message> {
    public MessageFile(String fileName, Validator<Message> validator) {
        super(fileName, validator);
    }

    @Override
    public Message extractEntity(List<String> attributes) {
        Long idMessage=Long.parseLong(attributes.get(0));

        String[] aux = attributes.get(1).split(",");
        Long id=Long.parseLong(aux[0]);
        User u = new User(aux[1],aux[2],aux[3],aux[4]); u.setId(id);

        List<User> l=new ArrayList<>();
        aux=attributes.get(2).split(",");
        for(String a : aux){
            String[] aux2=a.split(" ");
            Long id1 = Long.parseLong(aux2[0]);
            User u1=new User(aux2[1],aux2[2],aux2[3],aux2[4]); u1.setId(id1);
            l.add(u1);
        }

        String text=attributes.get(3);

        String date=attributes.get(4).replace("T"," ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime=LocalDateTime.parse(date,formatter);

        Message reply=null;
        if(!attributes.get(5).equals("null")){
            Long idReply=Long.parseLong(attributes.get(5));
            for(Message m : findAll())
                if(m.getId() == idReply){
                    reply=m; break;
                }
            reply.setId(idReply);
        }
        Message rez =  new Message(u,l,text,dateTime,reply);
        rez.setId(idMessage);
        return rez;
    }

    @Override
    protected String createEntityAsString(Message entity) {
        return entity.toString();
    }
}
