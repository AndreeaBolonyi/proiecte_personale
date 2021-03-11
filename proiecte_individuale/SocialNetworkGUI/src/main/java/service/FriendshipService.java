package socialnetwork.service;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.UserDTO;
import socialnetwork.repository.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FriendshipService {
    private final Repository<Tuple<Long,Long>, Friendship> friendshipRepository;
    private final Repository<Long, User> userRepository;

    public FriendshipService(Repository<Tuple<Long,Long>, Friendship> friendshipRepository, Repository<Long, User> userRepository){
        this.friendshipRepository=friendshipRepository;
        this.userRepository=userRepository;
        initFriends();
    }

    /**
        initialize friendships between users
     */
    private void initFriends(){
        for(Friendship f : getAll()){
            User u1=userRepository.findOne(f.getId().getLeft());
            User u2=userRepository.findOne(f.getId().getRight());
            u1.addFriend(u2);
            u2.addFriend(u1);
        }
    }

    /**
        @param id1 long
             id2 long
        @return Friendship object(builds a friendship)
        @throws ServiceException if id1 or id2 is null or if users are null
     */
    public Friendship addFriendship(Long id1, Long id2)  throws ServiceException{
        if(id1 == null || id2 == null)
            throw new ServiceException("Id can't be null");
        for(Friendship f : friendshipRepository.findAll()){
            if(f.getId().getLeft().equals(id1) && f.getId().getRight().equals(id2) || f.getId().getLeft().equals(id2) && f.getId().getRight().equals(id1))
                throw new ServiceException("Friendship already exists\n");
        }
        User u1=userRepository.findOne(id1);
        User u2=userRepository.findOne(id2);
        if(u1 == null || u2 == null)
            throw new ServiceException("Users must exist");
        u1.addFriend(u2);
        u2.addFriend(u1);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formatDateTime = now.format(formatter);
        LocalDateTime time=LocalDateTime.parse(formatDateTime,formatter);

        Friendship f=new Friendship(id1, id2, time);
        f.setId(new Tuple<>(id1,id2));
        return friendshipRepository.save(f);
    }

    /**
         @param id1 long
                id2 long
         @return Friendship object(delete a friendship)
         @throws ServiceException if id1 or id2 is null or if users are null
     */
    public Friendship removeFriendship(Long id1, Long id2)  throws ServiceException{
        if(id1 == null || id2 == null)
            throw new ServiceException("Id can't be null");
        User u1=userRepository.findOne(id1);
        User u2=userRepository.findOne(id2);
        if(u1 == null || u2 == null)
            throw new ServiceException("Users must exist");
        u1.removeFriend(u2);
        u2.removeFriend(u1);
        return friendshipRepository.delete(new Tuple<>(id1,id2));
    }

    /**
     * @param id long
     * @return list<String>
     * @throws ServiceException
     */
    public List<String> findFriends(Long id){
        if(id == null)
            throw new ServiceException("Id can't be null\n");

        List<Friendship> friendshipList=new ArrayList<>();
        for(Friendship f : friendshipRepository.findAll())
            friendshipList.add(f);

        Predicate<Friendship> left=x-> x.getId().getLeft().equals(id);
        Predicate<Friendship> right=x-> x.getId().getRight().equals(id);

        List<String> rez1 = friendshipList.stream()
                .filter(left)
                .map(x->{
                    UserDTO u=null;
                    for(User a : userRepository.findAll())
                        if(a.getId().equals(x.getId().getRight())){
                            u = new UserDTO(a.getFirstName(), a.getLastName(), x.getDate());
                            break;
                        }
                    return u.toString();
                })
                .collect(Collectors.toList());

        List<String> rez2 = friendshipList.stream()
                .filter(right)
                .map(x->{
                    UserDTO u=null;
                    for(User a : userRepository.findAll())
                        if(a.getId().equals(x.getId().getLeft())){
                            u = new UserDTO(a.getFirstName(), a.getLastName(), x.getDate());
                            break;
                        }
                    return u.toString();
                })
                .collect(Collectors.toList());

        return Stream.concat(rez1.stream(), rez2.stream()).collect(Collectors.toList());
    }

    /**
     * @param id long
     * @param luna int
     * @return list<String>
     * @throws ServiceException
     */
    public List<String> findFriends2(Long id,int luna){
        if(id == null)
            throw new ServiceException("Id can't be null\n");
        if(luna<1 || luna>12)
            throw new ServiceException("Invalid month\n");
        List<Friendship> friendshipList=new ArrayList<>();
        for(Friendship f : friendshipRepository.findAll())
            friendshipList.add(f);

        Predicate<Friendship> left=x-> x.getId().getLeft().equals(id);
        Predicate<Friendship> right=x-> x.getId().getRight().equals(id);
        Predicate<Friendship> month=x->x.getDate().getMonth().getValue() == luna;

        List<String> rez1 = friendshipList.stream()
                .filter(left.and(month))
                .map(x->{
                    UserDTO u=null;
                    for(User a : userRepository.findAll())
                        if(a.getId().equals(x.getId().getRight())){
                            u = new UserDTO(a.getFirstName(), a.getLastName(), x.getDate());
                            break;
                        }
                    return u.toString();
                })
                .collect(Collectors.toList());

        List<String> rez2 = friendshipList.stream()
                .filter(right.and(month))
                .map(x->{
                    UserDTO u=null;
                    for(User a : userRepository.findAll())
                        if(a.getId().equals(x.getId().getLeft())){
                            u = new UserDTO(a.getFirstName(), a.getLastName(), x.getDate());
                            break;
                        }
                    return u.toString();
                })
                .collect(Collectors.toList());

        return Stream.concat(rez1.stream(), rez2.stream()).collect(Collectors.toList());
    }

    public Iterable<Friendship> getAll(){ return friendshipRepository.findAll(); }
}
