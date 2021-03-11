package socialnetwork.repository.file;

import socialnetwork.domain.Request;
import socialnetwork.domain.validators.Validator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RequestFile extends AbstractFileRepository<Long, Request>{
    public RequestFile(String fileName, Validator<Request> validator) {
        super(fileName, validator);
    }

    @Override
    public Request extractEntity(List<String> attributes) {
        Long id=Long.parseLong(attributes.get(0));
        Long id1=Long.parseLong(attributes.get(1));
        Long id2=Long.parseLong(attributes.get(2));
        String date=attributes.get(4).replace("T"," ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Request request=new Request(id1,id2,attributes.get(3), LocalDateTime.parse(date,formatter));
        request.setId(id);
        return request;
    }

    @Override
    protected String createEntityAsString(Request entity) {
        return entity.toString();
    }
}
