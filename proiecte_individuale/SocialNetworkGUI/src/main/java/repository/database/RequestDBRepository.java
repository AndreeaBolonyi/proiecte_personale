package socialnetwork.repository.database;

import socialnetwork.domain.Request;
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
import java.util.HashSet;
import java.util.Set;

public class RequestDBRepository implements PagingRepository<Long, Request> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Request> validator;
    Connection connection;
    PreparedStatement statement;

    public RequestDBRepository(String url, String username, String password, Validator<Request> validator) {
        this.url=url;
        this.username=username;
        this.password=password;
        this.validator=validator;
    }

    @Override
    public Request findOne(Long aLong) {
        if(aLong == null)
            throw new IllegalArgumentException("Id can't be null\n");
        try{
            connection = DriverManager.getConnection(url,username,password);
            statement = connection.prepareStatement("select * from Requests where id="+aLong.intValue());
            ResultSet resultSet=statement.executeQuery();

            if(resultSet.next()){
                Long id = resultSet.getLong("id");
                Long id1= resultSet.getLong("id1");
                Long id2= resultSet.getLong("id2");
                String status = resultSet.getString("status");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();

                Request request = new Request(id1,id2,status,date);
                request.setId(id);
                validator.validate(request);
                return request;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Request> findAll() {
        Set<Request> requests = new HashSet<>();
        try{
            connection = DriverManager.getConnection(url,username,password);
            statement = connection.prepareStatement("select * from Requests");
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                Long id = resultSet.getLong("id");
                Long id1= resultSet.getLong("id1");
                Long id2= resultSet.getLong("id2");
                String status = resultSet.getString("status");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();

                Request request = new Request(id1,id2,status,date);
                request.setId(id);
                requests.add(request);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return requests;
    }

    @Override
    public Request save(Request entity) {
        if(entity == null)
            throw new IllegalArgumentException("Null request\n");
        validator.validate(entity);
        for(Request r : findAll())
            if(r.getId() == entity.getId())
                throw new RepositoryException("Existent id\n");
        try{
            connection = DriverManager.getConnection(url,username,password);
            statement = connection.prepareStatement("insert into Requests values("+entity.getId()+","+ entity.getId1()+","+entity.getId2()+",'"+entity.getStatus()+ "', '" + entity.getDate()+ "' )");
            statement.execute();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Request delete(Long aLong) {
        if(aLong == null)
            throw new IllegalArgumentException("Id can't be null\n");
        Request r = findOne(aLong);
        if(r == null)
            throw new RepositoryException("Inexistent request\n");
        try{
            connection = DriverManager.getConnection(url,username,password);
            statement = connection.prepareStatement("delete from Requests where id="+aLong.intValue());
            statement.execute();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return r;
    }

    @Override
    public Request update(Request entity) {
        return null;
    }

    private int getSize(){
        Iterable<Request> requests = findAll();
        int nr=0;
        for(Request f : requests)
            nr++;
        return nr;
    }

    @Override
    public Page<Request> findAll(Pageable pageable) {
        Paginator<Request> paginator = new Paginator<Request>(pageable, this.findAll());
        return paginator.paginate();
    }
}
