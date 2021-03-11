package socialnetwork.service;

import socialnetwork.domain.*;
import socialnetwork.events.EventType;
import socialnetwork.events.UserChangeEvent;
import socialnetwork.observer.Observable;
import socialnetwork.observer.Observer;
import socialnetwork.password.Crypt;
import socialnetwork.repository.Repository;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.PageableImplementation;
import socialnetwork.repository.paging.PagingRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

public class Service implements Observable<UserChangeEvent> {
    private User currentUser;
    private final PagingRepository<Long, User> repoUsers;
    private final PagingRepository<Tuple<Long,Long>, Friendship> repoFriendships;
    private final PagingRepository<Long, Request> repoRequests;
    private final PagingRepository<Long, Message> repoMessages;
    private final PagingRepository<Long,Event> repoEvents;
    private final PagingRepository<Long,Notification> repoNotifications;
    private final Repository<Long,User> repoLoggedUser;
    private final ArrayList<Observer<UserChangeEvent>> observers;
    private final UserService userService;
    private final NetworkService networkService;
    private final FriendshipService friendshipService;
    private int sizeUsers = 1;
    private int sizeFriends = 1;
    private int sizeMessages = 1;
    private int sizeReceived = 1;
    private int sizeSent = 1;
    private int page=0;

    public Service(PagingRepository<Tuple<Long,Long>, Friendship> repoFriendships, PagingRepository<Long, Request> repoRequests, PagingRepository<Long, Message> repoMessages,PagingRepository<Long, User> repo,PagingRepository<Long,Event> repoEvents, PagingRepository<Long,Notification> repoNotifications,Repository<Long,User> repoLoggedUser){
        this.repoUsers = repo;
        this.repoFriendships=repoFriendships;
        this.repoRequests=repoRequests;
        this.repoMessages=repoMessages;
        this.repoEvents=repoEvents;
        this.repoNotifications=repoNotifications;
        this.repoLoggedUser=repoLoggedUser;
        this.observers=new ArrayList<>();
        this.userService = new UserService(repoFriendships,repoUsers);
        this.networkService = new NetworkService(repoFriendships,repoUsers,repoMessages,repoRequests);
        this.friendshipService = new FriendshipService(repoFriendships,repoUsers);
    }

    public void setPageSizeUsers(int size) {
        this.sizeUsers = size;
    }

    public Set<User> getUsersOnPage(int page) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.sizeUsers);
        Page<User> studentPage = repoUsers.findAll(pageable);
        return studentPage.getContent().collect(Collectors.toSet());
    }

    public Set<Message> getMessagesOnPage(int page, User friend) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.sizeMessages);
        Predicate<Message> filterFromCurrent = x -> (x.getFrom().equals(currentUser) && x.getTo().contains(friend));
        Predicate<Message> filterToCurrent = x -> (x.getTo().contains(currentUser) && x.getFrom().equals(friend));
        return repoMessages.findAll(pageable).getContent().filter(filterFromCurrent.or(filterToCurrent)).collect(Collectors.toSet());
    }

    public void setPageSizeMessages(int size) {
        this.sizeMessages = size;
    }

    public void setPageSizeFriends(int size) {
        this.sizeFriends = size;
    }

    public Set<User> getFriendsOnPage(int page) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.sizeFriends);
        Predicate<Friendship> filterLeft = x -> x.getId().getLeft().equals(currentUser.getId());
        Predicate<Friendship> filterRight = x -> x.getId().getRight().equals(currentUser.getId());
        Set<Friendship> aux = repoFriendships.findAll(pageable).getContent().filter(filterLeft.or(filterRight)).collect(Collectors.toSet());
        Set<User> rez = new HashSet<>();
        for(Friendship f : aux){
            if(f.getId().getLeft().equals(currentUser.getId())){
                User u = repoUsers.findOne(f.getId().getRight());
                rez.add(u);
            }
            else
            if(f.getId().getRight().equals(currentUser.getId())){
                User u = repoUsers.findOne(f.getId().getLeft());
                rez.add(u);
            }
        }
        return rez;
    }

    public void setPageSizeReceived(int size) {
        this.sizeReceived = size;
    }

    public Set<Request> getReceivedOnPage(int page) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.sizeReceived);
        Predicate<Request> filterReceived = x -> x.getId2().equals(currentUser.getId());
        return repoRequests.findAll(pageable).getContent().filter(filterReceived).collect(Collectors.toSet());
    }


    public void setPageSizeSent(int size) {
        this.sizeSent = size;
    }

    public Set<Request> getSentOnPage(int page) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.sizeSent);
        Predicate<Request> filterSent = x -> x.getId1().equals(currentUser.getId());
        return repoRequests.findAll(pageable).getContent().filter(filterSent).collect(Collectors.toSet());
    }

    public int sizeFindAll(){
        int numar=0;
        for(User ignored : repoUsers.findAll())
            numar++;
        return numar;
    }

    public int sizeFindAllReceived(){
        int numar=0;
        for(Request r : repoRequests.findAll()){
            User u = repoUsers.findOne(r.getId2());
            if(u.equals(currentUser))
                numar++;
        }
        return numar;
    }

    public int sizeFindAllSent(){
        int numar=0;
        for(Request r : repoRequests.findAll()){
            User u = repoUsers.findOne(r.getId1());
            if(u.equals(currentUser))
                numar++;
        }
        return numar;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public Iterable<User> getAllUsers(){
        return repoUsers.findAll();
    }

    public UserService getUserService(){
        return userService;
    }

    public FriendshipService getFriendshipService(){return friendshipService;}

    public NetworkService getNetworkService(){ return networkService;}

    public Friendship deleteFriend(User u){
        Friendship f = getFriendshipService().removeFriendship(currentUser.getId(), u.getId());
        if(f!=null){
            notifyObservers(new UserChangeEvent(EventType.DELETE,u));
        }
        return f;
    }

    public Friendship addFriend(User u){
        try{
            Friendship f = getFriendshipService().addFriendship(currentUser.getId(), u.getId());
            if(f!=null)
                notifyObservers(new UserChangeEvent(EventType.ADD,u));
            return f;
        }
        catch(ServiceException e){
            return null;
        }
    }

    public User findUser(String firstName, String lastName, String email){
        for(User u : getUserService().getAll()){
            if(u.getFirstName().equals(firstName) && u.getLastName().equals(lastName) && u.getEmail().equals(email))
                return u;
        }
        return null;
    }

    public Request sendRequest(User u){
        try{
            Request r = getNetworkService().sendRequest(currentUser.getId(), u.getId());
            if(r!=null)
                notifyObservers(new UserChangeEvent(EventType.ADD, u));
            return r;
        }
        catch(RuntimeException e){
            return null;
        }
    }

    public Request deleteRequest(Request aux){
        try{
            if(aux.getStatus().equals("pending")){
                Request r = repoRequests.delete(aux.getId());
                if (r!=null)
                    notifyObservers(new UserChangeEvent(EventType.DELETE, repoUsers.findOne(aux.getId1())));
                return null;
            }
            else
                return null;
        }
        catch(ServiceException e){
            return null;
        }
    }

    public Request acceptRequest(Long id,Request aux){
        try{
            Request r = getNetworkService().answerRequest(id,aux,"da");
            if(r!=null)
                notifyObservers(new UserChangeEvent(EventType.ADD, repoUsers.findOne(aux.getId1())));
            return r;

        }catch(ServiceException e){
            return null;
        }
    }

    public Request rejectRequest(Long id,Request aux){
        try{
            Request r = getNetworkService().answerRequest(id,aux,"nu");
            if(r!=null)
                notifyObservers(new UserChangeEvent(EventType.DELETE, repoUsers.findOne(aux.getId1())));
            return r;

        }catch(ServiceException e){
            return null;
        }
    }

    public Message sendMessage(List<Long> list,String message, Long idReply){
        try{
            Message reply = null;
            if(idReply != null)
                reply = repoMessages.findOne(idReply);
            Message m = getNetworkService().addMessage(currentUser.getId(),list,message,reply);
            if(m!=null)
                notifyObservers(new UserChangeEvent(EventType.ADD, currentUser));
            return m;
        }
        catch(ServiceException e){
            return null;
        }
    }

    public List<UserView> findUserFriends(){
        List<UserView> friends=new ArrayList<>();
        for(Friendship f : getFriendshipService().getAll()){
            if(f.getId().getLeft().equals(currentUser.getId())){
                User u = getUserService().getUser(f.getId().getRight());
                friends.add(new UserView(u.getFirstName(),u.getLastName(),u.getEmail()));
            }
            if(f.getId().getRight().equals(currentUser.getId())){
                User u = getUserService().getUser(f.getId().getLeft());
                friends.add(new UserView(u.getFirstName(),u.getLastName(),u.getEmail()));
            }
        }
        return friends;
    }

    public Iterable<Event> findAllEvents(){
        return repoEvents.findAll();
    }

    public Event addEvent(LocalDate date){
        Event r = null;
        for (Event e : findAllEvents()) {
            if (e.getDate().toLocalDate().equals(date)) {
                if (!e.getParticipants().contains(getCurrentUser())) {
                    List<User> list = new ArrayList<>(e.getParticipants());
                    list.add(getCurrentUser());
                    Event aux = new Event(e.getDate(), e.getDescription(), list,e.getOwner());
                    aux.setId(e.getId());
                    repoEvents.delete(e.getId());
                    repoEvents.save(aux);
                    r = aux;
                }
                else
                    throw new ServiceException("Participati deja la acest eveniment\n");
            }
        }
        if(r == null)
            throw new ServiceException("Nu exista niciun eveniment in acea zi\n");
        else
            notifyObservers(new UserChangeEvent(EventType.ADD, currentUser));
        return r;
    }

    public Event deleteEvent(LocalDate date){
        Event r = null;
        for (Event e : findAllEvents()) {
            if (e.getDate().toLocalDate().equals(date)) {
                if (e.getParticipants().contains(getCurrentUser())) {
                    List<User> list = new ArrayList<>();
                    for (User u : e.getParticipants())
                        if(!u.equals(currentUser))
                            list.add(u);
                    Event aux = new Event(e.getDate(), e.getDescription(), list,e.getOwner());
                    aux.setId(e.getId());
                    repoEvents.delete(e.getId());
                    repoEvents.save(aux);
                    r = aux;
                }
                else
                    throw new ServiceException("Nu participati la acest eveniment\n");
            }
        }
        if(r == null)
            throw new ServiceException("Nu exista niciun eveniment in acea zi\n");
        else
            notifyObservers(new UserChangeEvent(EventType.DELETE, currentUser));
        return r;
    }

    public Event deleteEventOwner(Event e){
        for(Notification n : findAllNotifications()){
            if(n.getEvent().equals(e)){
                Notification newOne = new Notification("->"+e.getDescription()+" is deleted",e);
                newOne.setId(findNewId());
                newNotification(newOne);
            }
        }
        Event aux = repoEvents.delete(e.getId());
        if(aux != null)
            notifyObservers(new UserChangeEvent(EventType.DELETE,currentUser));
        return aux;
    }

    public Event addNewEvent(Event e){
        Long nr=0L;
        for(Event n : repoEvents.findAll()) {
            if (n.getId() >= nr)
                nr = n.getId();
        }
        e.setId(nr+1);
        Event rez = repoEvents.save(e);
        if(rez != null)
            notifyObservers(new UserChangeEvent(EventType.ADD,currentUser));
        return rez;
    }

    public Notification newNotification(Notification newOne){
        try{
            Notification n = repoNotifications.save(newOne);
            if(n!=null)
                notifyObservers(new UserChangeEvent(EventType.ADD, currentUser));
            return n;

        }catch(ServiceException e){
            return null;
        }
    }

    public void tomorrow(){
        for(Event e : findAllEvents()){
            if(e.getParticipants().contains(getCurrentUser())){
                LocalDate tomorrow = LocalDate.now().plus(1, DAYS);
                if(tomorrow.equals(e.getDate().toLocalDate())){
                    Notification n = new Notification("Do not forget about it",e);
                    n.setId(findNewId());
                    boolean exist=false;
                    for(Notification aux : findAllNotifications())
                        if(aux.getMessage().equals(n.getMessage())){
                            exist=true;
                            break;
                        }
                    if(!exist)
                        newNotification(n);
                }
            }
        }
    }

    public List<Notification> allNotificationCurrentUser(){
        List<Notification> all = new ArrayList<>();
        for(Notification n : findAllNotifications())
            if(n.getEvent().getParticipants().contains(currentUser))
                all.add(n);
            return all;
    }

    public Iterable<Notification> findAllNotifications(){
        return repoNotifications.findAll();
    }

    public Long findNewId(){
        Long nr=0L;
        for(Notification n : repoNotifications.findAll()) {
            if (n.getId() >= nr)
                nr = n.getId();
        }
        return nr+1;
    }

    public User newUserToRemember(String email, String password){
        User u = null;
        for(User user : getAllUsers()){
            if(user.getEmail().equals(email) && Crypt.checkPassword(password,user.getPassword())){
                u=user;
                break;
            }
        }
        repoLoggedUser.delete(null);
        repoLoggedUser.save(u);
        return u;
    }

    public User getUserLogged(){
        Iterable<User> all = repoLoggedUser.findAll();
        for(User u : all)
            return u;
        return null;
    }

    public void deleteLoggedUser(){
        repoLoggedUser.delete(null);
    }

    @Override
    public void addObserver(Observer<UserChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<UserChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(UserChangeEvent t) {
        observers.forEach(x->x.update(t));
    }
}
