package socialnetwork.repository.database;

import socialnetwork.domain.User;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;
import socialnetwork.repository.paging.PagingRepository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class UserLoggedDBRepository implements Repository<Long, User> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<User> validator;
    Connection connection;
    PreparedStatement statement;
    private final PagingRepository<Long,User> repoUser;

    public UserLoggedDBRepository(String url, String username, String password, Validator<User> validator,PagingRepository<Long,User> repoUser) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        this.repoUser=repoUser;
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
    public User findOne(Long aLong) {
        if(aLong == null)
            throw new IllegalArgumentException("Id can't be null");
        try{
            statement = connection.prepareStatement("select * from UserLogged where idUser="+aLong.intValue());
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                Long id = resultSet.getLong("idUser");

                User u =repoUser.findOne(id);
                validator.validate(u);
                return u;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();
        try{
            statement = connection.prepareStatement("SELECT * from UserLogged");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("idUser");

                User u = repoUser.findOne(id);
                users.add(u);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public User save(User entity) {
        if(entity == null)
            throw new IllegalArgumentException("Id can't be null");
        validator.validate(entity);
        try{
            statement = connection.prepareStatement("INSERT INTO UserLogged values(" + entity.getId() + ")");
            statement.execute();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public User delete(Long aLong) {
        try{
            statement = connection.prepareStatement("delete from UserLogged");
            statement.execute();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User update(User entity) {
        return null;
    }
}
