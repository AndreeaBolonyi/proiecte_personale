package socialnetwork.service;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.repository.Repository;

import java.util.ArrayList;
import java.util.List;


public class UserService {
    private final Repository<Tuple<Long,Long>, Friendship> friendshipRepository;
    private final Repository<Long, User> userRepository;

    public UserService(Repository<Tuple<Long,Long>, Friendship> friendshipRepository, Repository<Long, User> userRepository){
        this.friendshipRepository=friendshipRepository;
        this.userRepository=userRepository;
    }

    /**
        add a user
        @param firstName String
            lastName String
        @throws ServiceException
     */
    public void addUser(String firstName, String lastName,String email, String password)  throws ServiceException{
        User u=new User(firstName,lastName,email,password);
        Long nr=0L;
        for(User usr : getAll())
            if(usr.getId()>nr)
                nr=usr.getId();
        Long l = nr+1;
        u.setId(l);
        userRepository.save(u);
    }

    /**
        remove a user
        @param id long
        @throws ServiceException
     */
    public void removeUser(Long id) throws ServiceException {
        if (id == null)
            throw new ServiceException("Id can't be null");
        Iterable<Friendship> friends = friendshipRepository.findAll();
        List<Friendship> friendships = new ArrayList<>();
        for (Friendship f : friends)
            friendships.add(f);

        for (Friendship f : friendships) {
                if (f.getId().getRight().equals(id))
                friendshipRepository.delete(new Tuple<>(f.getId().getLeft(), id));

                if (f.getId().getLeft().equals(id))
                    friendshipRepository.delete(new Tuple<>(id, f.getId().getRight()));

            }
        userRepository.delete(id);
        }

    public Iterable<User> getAll(){
        return userRepository.findAll();
    }

    /**
     * get a user by his id
     * @param id long
     * @return User object
     * @throws  ServiceException if id is null
     */
    public User getUser(Long id){
        if(id == null)
            throw  new ServiceException("Id can't be null\n");
        return userRepository.findOne(id);
    }

    /**
     *
     * @param u user
     * @return list of users which is the list of friends for that user
     * @throws ServiceException if user doesn't exist
     */
    public List<User> getFriends(User u){
        if(u == null)
            throw new ServiceException("Inexistent user\n");
        List<User> rez=new ArrayList<>();
        for(Friendship f : friendshipRepository.findAll()){
            if(f.getId().getRight().equals(u.getId()))
                rez.add(userRepository.findOne(f.getId().getLeft()));
            else
                if(f.getId().getLeft().equals(u.getId()))
                    rez.add(userRepository.findOne(f.getId().getRight()));
        }
        return rez;
    }
}
