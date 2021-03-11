package socialnetwork.repository.database;

import socialnetwork.domain.Entity;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.Paginator;
import socialnetwork.repository.paging.PagingRepository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class UserDBRepository implements PagingRepository<Long,User> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<User> validator;
    Connection connection;
    PreparedStatement statement;


    public UserDBRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
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
            statement = connection.prepareStatement("select * from Users U where U.id="+aLong.intValue());
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String email=resultSet.getString("email");
                String password=resultSet.getString("password");

                User u = new User(firstName, lastName,email,password);
                u.setId(id);
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
            statement = connection.prepareStatement("SELECT * from Users");
            ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    String firstName = resultSet.getString("firstName");
                    String lastName = resultSet.getString("lastName");
                    String email=resultSet.getString("email");
                    String password=resultSet.getString("password");

                    User u = new User(firstName, lastName,email,password);
                    u.setId(id);
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
            statement = connection.prepareStatement("INSERT INTO Users values(" + entity.getId() + ", '" + entity.getFirstName() + "', '" + entity.getLastName() + "','"+entity.getEmail() + "','" + entity.getPassword() + "')");
            statement.execute();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public User delete(Long aLong) {
        if(aLong == null)
            throw new IllegalArgumentException("Id can't be null");
        User u = findOne(aLong);
        if(u == null)
            throw new RepositoryException("Inexistent user\n");
        validator.validate(u);
        try{
            statement = connection.prepareStatement("delete from Users where id="+aLong);
            statement.execute();
        }
        catch(SQLException e){
            e.printStackTrace();
            throw new RepositoryException("Inexistent user\n");
        }
        return u;
    }

    @Override
    public User update(User entity) {
        return null;
    }

    private int getSize(){
        Iterable<User> users = findAll();
        int nr=0;
        for(User f : users)
            nr++;
        return nr;
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        Paginator<User> paginator = new Paginator<User>(pageable, this.findAll());
        return paginator.paginate();
    }
}


