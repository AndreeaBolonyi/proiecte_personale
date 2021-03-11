package socialnetwork.repository.database;

import org.postgresql.util.PSQLException;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
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

public class FriendshipDBRepository implements PagingRepository<Tuple<Long,Long>,Friendship> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Friendship> validator;
    Connection connection;
    PreparedStatement statement;

    public FriendshipDBRepository(String url, String username, String password, Validator<Friendship> validator){
        this.url=url;
        this.username=username;
        this.password=password;
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
    public Friendship findOne(Tuple<Long, Long> tuple) {
        if(tuple.getLeft() == null || tuple.getRight() == null)
            throw new IllegalArgumentException("Id can't be null\n");
        Friendship f=null;
        try{
            statement = connection.prepareStatement("select F.id, F.id1, F.id2, F.date from Friendships F where F.id1 = "+tuple.getLeft().intValue() + "and F.id2 = "+ tuple.getRight().intValue());
            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.next()) {
                statement = connection.prepareStatement("select F.id, F.id1, F.id2, F.date from Friendships F where F.id1 = " + tuple.getRight().intValue() + "and F.id2 = " + tuple.getLeft().intValue());
                resultSet = statement.executeQuery();
            }

            Long id = resultSet.getLong("id1");
            Long id1 = resultSet.getLong("id1");
            Long id2 = resultSet.getLong("id2");
            LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();

            f = new Friendship(id1, id2, date);
            f.setId(new Tuple<>(id1, id2));
        }
        catch(SQLException e){
           e.printStackTrace();
        }
        return f;
    }

    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();
        try{
            statement = connection.prepareStatement("select * from Friendships");
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();

                Friendship f = new Friendship(id1,id2,date);
                f.setId(new Tuple<>(id1,id2));
                friendships.add(f);
            }
        }
        catch(SQLException e){
            //e.printStackTrace();
        }
        return friendships;
    }

    @Override
    public Friendship save(Friendship entity) {
        if(entity == null)
            throw new IllegalArgumentException("Id can't be null");
        for(Friendship f : findAll())
            if(f.getId() == entity.getId())
                throw new RepositoryException("Id exists\n");
        try{
            int id = getId().intValue();
            statement = connection.prepareStatement("INSERT INTO Friendships values(" + id + " , " + entity.getId().getLeft().intValue() + " , " + entity.getId().getRight().intValue() + " , '" + entity.getDate()+ "' ) ");
            statement.execute();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Friendship delete(Tuple<Long, Long> tuple) {
        Friendship f = findOne(tuple);
        if(f == null)
            throw new RepositoryException("Inexistent friendship\n");
        try{
            statement = connection.prepareStatement("delete from Friendships where id1 = "+tuple.getLeft().intValue() + "and id2 = "+ tuple.getRight().intValue());
            statement.execute();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return f;
    }

    @Override
    public Friendship update(Friendship entity) {
        return null;
    }

    private Long getId(){
        Iterable<Friendship> friendships = findAll();
        Long nr=0L;
        try {
            statement = connection.prepareStatement("select * from Friendships");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                if(id > nr)
                    nr=id;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return nr+1;
    }

    @Override
    public Page<Friendship> findAll(Pageable pageable) {
        Paginator<Friendship> paginator = new Paginator<Friendship>(pageable, this.findAll());
        return paginator.paginate();
    }
}
