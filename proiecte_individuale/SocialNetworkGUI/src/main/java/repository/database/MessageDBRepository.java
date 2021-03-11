package socialnetwork.repository.database;

import socialnetwork.domain.Message;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.Paginator;
import socialnetwork.repository.paging.PagingRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class MessageDBRepository implements PagingRepository<Long,Message> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Message> validator;
    private final Repository<Long,User> userRepository;
    Connection connection;
    PreparedStatement statement;

    public MessageDBRepository(String url, String username, String password, Validator<Message> validator,Repository<Long,User> userRepository){
        this.url=url;
        this.username=username;
        this.password=password;
        this.validator=validator;
        this.userRepository=userRepository;
        init();
    }

    private void init(){
        try{
            connection = DriverManager.getConnection(url,username,password);
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public Message findOne(Long aLong) {
        if(aLong == null)
            throw new IllegalArgumentException("Id can't be null\n");
        try{
            statement = connection.prepareStatement("select * from Messages where id="+aLong.intValue());
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                Long id = resultSet.getLong("id");
                Long idFrom = resultSet.getLong("idFrom");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Long idReply = resultSet.getLong("idReply");
                List<User> to = new ArrayList<>();

                PreparedStatement s = connection.prepareStatement("select * from ToUsers");
                ResultSet r = s.executeQuery();
                while(r.next()){
                    Long idUser = r.getLong("idUser");
                    Long idMessage = r.getLong("idMessage");
                    if(idMessage.equals(id)){
                        User u = userRepository.findOne(idUser);
                        to.add(u);
                    }
                }

                User from = userRepository.findOne(idFrom);
                Message reply = findOne(idReply);

                Message m = new Message(from,to,message,date,reply);
                m.setId(id);
                return m;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        try{
            statement = connection.prepareStatement("select * from Messages;");
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                Long id = resultSet.getLong("id");
                Long idFrom = resultSet.getLong("idFrom");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Long idReply = resultSet.getLong("idReply");
                List<User> to = new ArrayList<>();

                PreparedStatement s = connection.prepareStatement("select * from ToUsers");
                ResultSet r = s.executeQuery();
                while(r.next()){
                    Long idUser = r.getLong("idUser");
                    Long idMessage = r.getLong("idMessage");
                    if(idMessage.equals(id)){
                        User u = userRepository.findOne(idUser);
                        to.add(u);
                    }
                }

                User from = userRepository.findOne(idFrom);
                Message reply = findOne(idReply);

                Message m = new Message(from,to,message,date,reply);
                m.setId(id);
                messages.add(m);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        Collections.sort(messages,Comparator.comparing(Message::getDate));
        return messages;
    }

    @Override
    public Message save(Message entity) {
        if(entity == null)
            throw new IllegalArgumentException("Null message\n");
        for(Message m : findAll()) {
            if (m.getId().equals(entity.getId()))
                throw new RepositoryException("Existent id\n");
        }
        validator.validate(entity);
        try{
            if(entity.getReply() == null){
                statement = connection.prepareStatement("insert into Messages values (" + entity.getId() + "," + entity.getFrom().getId() +
                        ",'" + entity.getMessage() + "','" + entity.getDate() + "'," + entity.getReply() + ")");
            }
            else{
                statement = connection.prepareStatement("insert into Messages values (" + entity.getId() + "," + entity.getFrom().getId() +
                        ",'" + entity.getMessage() + "','" + entity.getDate() + "'," + entity.getReply().getId() + ")");
            }
            statement.execute();
            for(User u : entity.getTo()){
                int id = sizeToUsers()+1;
                PreparedStatement s = connection.prepareStatement("insert into ToUsers values("+id + "," + u.getId() + "," + entity.getId() + ")");
                s.execute();
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Message delete(Long aLong) {
        if(aLong == null)
            throw new IllegalArgumentException("Id can't be null\n");
        Message entity = findOne(aLong);
        if(entity == null)
            throw new RepositoryException("Inexistent message\n");
        validator.validate(entity);
        try{
            statement = connection.prepareStatement("delete from ToUsers where idMessage="+aLong.intValue());
            statement.execute();

            statement = connection.prepareStatement("delete from Messages where id="+aLong.intValue());
            statement.execute();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Message update(Message entity) {
        return null;
    }

    private int sizeToUsers(){
        int nr=0;
        try{
            statement = connection.prepareStatement("select * from ToUsers");
            ResultSet r = statement.executeQuery();
            while(r.next())
                nr++;
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return nr;
    }

    @Override
    public Page<Message> findAll(Pageable pageable) {
        Paginator<Message> paginator = new Paginator<>(pageable, this.findAll());
        return paginator.paginate();
    }
}
