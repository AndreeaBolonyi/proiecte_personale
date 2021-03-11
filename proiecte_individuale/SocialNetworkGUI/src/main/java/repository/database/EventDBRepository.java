package socialnetwork.repository.database;

import socialnetwork.domain.Event;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventDBRepository implements PagingRepository<Long, Event> {
    private final String url;
    private final String username;
    private final String password;
    private final Repository<Long, User> userRepository;
    private final Validator<Event> validator;
    Connection connection;
    PreparedStatement statement;

    public EventDBRepository(String url, String username, String password,Repository<Long,User> userRepository, Validator<Event> validator){
        this.url=url;
        this.username=username;
        this.password=password;
        this.userRepository=userRepository;
        this.validator=validator;
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
    public Event findOne(Long aLong) {
        if(aLong == null)
            throw new IllegalArgumentException("Id can't be null\n");
        try{
            statement = connection.prepareStatement("select * from Events where id="+aLong.intValue());
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                Long id = resultSet.getLong("id");
                String description = resultSet.getString("description");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Long idOwner = resultSet.getLong("idOwner");
                List<User> participants = new ArrayList<>();

                PreparedStatement s = connection.prepareStatement("select * from Participants");
                ResultSet r = s.executeQuery();
                while(r.next()){
                    Long idUser = r.getLong("idUser");
                    Long idEvent = r.getLong("idEvent");
                    if(idEvent.equals(id)){
                        User u = userRepository.findOne(idUser);
                        participants.add(u);
                    }
                }

                Event m = new Event(date,description,participants, userRepository.findOne(idOwner));
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
    public Iterable<Event> findAll() {
        Set<Event> events = new HashSet<>();
        try{
            statement = connection.prepareStatement("select * from Events;");
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                Long id = resultSet.getLong("id");
                String description = resultSet.getString("description");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Long idOwner = resultSet.getLong("idOwner");
                List<User> participants = new ArrayList<>();

                PreparedStatement s = connection.prepareStatement("select * from Participants");
                ResultSet r = s.executeQuery();
                while(r.next()){
                    Long idUser = r.getLong("idUser");
                    Long idEvent = r.getLong("idEvent");
                    if(idEvent.equals(id)){
                        User u = userRepository.findOne(idUser);
                        participants.add(u);
                    }
                }

                Event m = new Event(date,description,participants, userRepository.findOne(idOwner));
                m.setId(id);
                events.add(m);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public Event save(Event entity) {
        if(entity == null)
            throw new IllegalArgumentException("Null message\n");
        for(Event m : findAll()) {
            if (m.getId().equals(entity.getId()))
                throw new RepositoryException("Existent id\n");
            if(m.getDate().equals(entity.getDate()))
                throw new RepositoryException("Is already an event at that time\n");
        }
        validator.validate(entity);
        try{
            statement = connection.prepareStatement("insert into Events(id,description,date,idOwner) values (" + entity.getId() + ",'" +entity.getDescription() + "','" + entity.getDate() +"'," + entity.getOwner().getId()+ ")");
            statement.execute();
            for(User u : entity.getParticipants()){
                int id = sizeParticipants()+1;
                PreparedStatement s = connection.prepareStatement("insert into Participants values("+id + "," + u.getId() + "," + entity.getId() + ")");
                s.execute();
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Event delete(Long aLong) {
        if(aLong == null)
            throw new IllegalArgumentException("Id can't be null\n");
        Event entity = findOne(aLong);
        if(entity == null)
            throw new RepositoryException("Inexistent event\n");
        try{
            statement = connection.prepareStatement("delete from Participants where idEvent="+aLong.intValue());
            statement.execute();

            statement = connection.prepareStatement("delete from Events where id="+aLong.intValue());
            statement.execute();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Event update(Event entity) {
        return null;
    }

    private int sizeParticipants(){
        int nr=0;
        try{
            statement = connection.prepareStatement("select * from Participants");
            ResultSet r = statement.executeQuery();
            while(r.next()){
                Long id = r.getLong("id");
                if(id >= nr)
                    nr=id.intValue();
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return nr+1;
    }

    @Override
    public Page<Event> findAll(Pageable pageable) {
        Paginator<Event> paginator = new Paginator<>(pageable, this.findAll());
        return paginator.paginate();
    }
}
