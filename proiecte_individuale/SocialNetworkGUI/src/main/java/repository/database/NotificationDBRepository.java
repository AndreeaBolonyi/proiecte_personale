package socialnetwork.repository.database;

import socialnetwork.domain.Event;
import socialnetwork.domain.Notification;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.Paginator;
import socialnetwork.repository.paging.PagingRepository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class NotificationDBRepository implements PagingRepository<Long, Notification> {
    private final String url;
    private final String username;
    private final String password;
    private final Repository<Long, Event> eventRepository;
    Connection connection;
    PreparedStatement statement;

    public NotificationDBRepository(String url, String username, String password,Repository<Long, Event> eventRepository){
        this.url=url;
        this.username=username;
        this.password=password;
        this.eventRepository=eventRepository;
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
    public Page<Notification> findAll(Pageable pageable) {
        Paginator<Notification> paginator = new Paginator<>(pageable, this.findAll());
        return paginator.paginate();
    }

    @Override
    public Notification findOne(Long aLong) {
        if(aLong == null)
            throw new IllegalArgumentException("Id can't be null\n");
        try{
            statement = connection.prepareStatement("select * from Notifications where id="+aLong.intValue());
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                while(resultSet.next()){
                    Long id = resultSet.getLong("id");
                    String message = resultSet.getString("message");
                    Long idEvent = resultSet.getLong("idEvent");


                    Notification n = new Notification(message, eventRepository.findOne(idEvent));
                    n.setId(id);
                    return n;
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Notification> findAll() {
        Set<Notification> notifications = new HashSet<>();
        try{
            statement = connection.prepareStatement("select * from Notifications;");
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                Long id = resultSet.getLong("id");
                String message = resultSet.getString("message");
                Long idEvent = resultSet.getLong("idEvent");


                Notification n = new Notification(message, eventRepository.findOne(idEvent));
                n.setId(id);
                notifications.add(n);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return notifications;
    }

    @Override
    public Notification save(Notification entity) {
        if(entity == null)
            throw new IllegalArgumentException("Null message\n");
        for(Notification m : findAll()) {
            if (m.getId().equals(entity.getId()))
                throw new RepositoryException("Existent id\n");
        }
        try{
            statement = connection.prepareStatement("insert into Notifications(id,idEvent,message) values (" + entity.getId() + "," +entity.getEvent().getId() + ",'" + entity.getMessage() +"')");
            statement.execute();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Notification delete(Long aLong) {
        if(aLong == null)
            throw new IllegalArgumentException("Id can't be null\n");
        Notification entity = findOne(aLong);
        if(entity == null)
            throw new RepositoryException("Inexistent notification\n");
        try{
            statement = connection.prepareStatement("delete from Notifications where id="+aLong.intValue());
            statement.execute();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Notification update(Notification entity) {
        return null;
    }
}
