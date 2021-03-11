package socialnetwork.service;

import socialnetwork.domain.*;
import socialnetwork.repository.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class NetworkService {
    private final Repository<Tuple<Long,Long>, Friendship> friendshipRepository;
    private final Repository<Long, User> userRepository;
    private final Repository<Long, Message> messageRepository;
    private final Repository<Long, Request> requestRepository;

    public NetworkService(Repository<Tuple<Long,Long>, Friendship> friendshipRepository, Repository<Long, User> userRepository,Repository<Long, Message> messageRepository, Repository<Long,Request> requestRepository){
        this.friendshipRepository=friendshipRepository;
        this.userRepository=userRepository;
        this.messageRepository=messageRepository;
        this.requestRepository=requestRepository;
    }

    /**
     * take all the messages between two users
     * @param id1 long
     * @param id2 long
     * @return list<Message> which represent a conversation between two user
     * @throws ServiceException
     */
    public List<Message> getConversation(Long id1, Long id2){
        if(id1 == null || id2 == null)
            throw new ServiceException("Id can't be null\n");
        User u1= userRepository.findOne(id1);
        User u2=userRepository.findOne(id2);
        if(u1 == null || u2==null)
            throw new ServiceException("Users can't be null\n");

        List<Message> messages=new ArrayList<>();
        List<Message> rez=new ArrayList<>();
        Iterable<Message> aux=messageRepository.findAll();
        for(Message m :  aux)
            messages.add(m);

        for(Message m : messages){
            if(m.getFrom().getId().equals(u1.getId())) {
                for(User u : m.getTo())
                    if(u.getId().equals(u2.getId())){
                        rez.add(m);
                        break;
                    }
            }
            if(m.getFrom().getId().equals(u2.getId())) {
                for(User u : m.getTo())
                    if(u.getId().equals(u1.getId())){
                        rez.add(m);
                        break;
                    }
            }
        }
        Collections.sort(rez,Comparator.comparing(Message::getDate));
        return rez;
    }

    /**
     * add a new message between an user and a list of users
     * @param idFrom long
     * @param l list of Long
     * @param message string
     * @param reply Message object
     * @throws ServiceException if users are invalid or message is invalid
     */
    public Message addMessage(Long idFrom, List<Long> l, String message,Message reply){
        if(idFrom == null)
            throw new ServiceException("Id can't be null");
        for(Long id : l) {
            if (id == null)
                throw new ServiceException("Id can't be null");
        }

        User from = userRepository.findOne(idFrom);
        List<User> to=new ArrayList<>();
        for(Long id : l){
            to.add(userRepository.findOne(id));
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formatDateTime = now.format(formatter);
        LocalDateTime time=LocalDateTime.parse(formatDateTime,formatter);

        Message m = new Message(from,to,message, time,reply);
        Long nr=0L;
        for(Message M : getAllMessages())
            if(M.getId()>=nr)
                nr=M.getId();
        Long n = nr+2;
        m.setId(n);
        return messageRepository.save(m);
    }

    /**
     * add a new request
     * @param id1 long
     * @param id2 long
     * @throws ServiceException if id1 or id2 is not valid
     */
    public Request sendRequest(Long id1, Long id2){
        if(id1 == null || id2 == null)
            throw new ServiceException("Id can't be null\n");
        for(Request r : requestRepository.findAll())
            if(r.getId1().equals(id1) && r.getId2().equals(id2))
                throw new ServiceException("It's already send a request\n");
        Long nr=0L;
        for(Request r : getAllRequests())
            if(r.getId()>nr)
                nr=r.getId();
        Long n = nr+1;
        Request request = new Request(id1,id2,"pending",LocalDateTime.now());
        request.setId(n);
        return requestRepository.save(request);
    }

    /**
     * update the status from a request
     * @param id long
     * @param r Request object
     * @param answer string
     * @throws ServiceException if id is null
     */
    public Request answerRequest(Long id,Request r,String answer){
        if(id == null)
            throw new ServiceException("Id can't be null\n");
        String status;
        if(answer.equals("da")) {
            status = "accepted";

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String formatDateTime = now.format(formatter);
            LocalDateTime time=LocalDateTime.parse(formatDateTime,formatter);
            Friendship f = new Friendship(r.getId1(),r.getId2(),time);
            f.setId(new Tuple<>(r.getId1(),r.getId2()));

            friendshipRepository.save(f);
        }
        else
            status="rejected";
        Long rId= r.getId();
        requestRepository.delete(rId);
        Request request = new Request(r.getId1(), r.getId2(), status,LocalDateTime.now());
        request.setId(rId);
        return requestRepository.save(request);
    }

    /**
     *
     * @param id long
     * @return all requests for a user
     * @throws ServiceException if id is null
     */
    public List<Request> userRequests(Long id){
        if(id == null)
            throw new ServiceException("Id can't be null\n");

        Iterable<Request> requestIterable = requestRepository.findAll();
        List<Request> rez= new ArrayList<>();
        for(Request r : requestIterable) {
            if (r.getId2().equals(id) && r.getStatus().equals("pending"))
                rez.add(r);
        }
        return rez;
    }

    public Message findMessage(Long id){
        return messageRepository.findOne(id);
    }

    public Iterable<Message> getAllMessages(){
        return messageRepository.findAll();
    }

    public Iterable<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    private int[][] adjacencyMatrix(Iterable<Friendship> friendships, int nr){
        int[][] matrix = new int[nr][nr];
        for(Friendship f : friendships)
            matrix[f.getId().getLeft().intValue()][f.getId().getRight().intValue()]=matrix[f.getId().getRight().intValue()][f.getId().getLeft().intValue()]=1;
        return matrix;
    }

    private int thereIsAnUnvisitedNode(int v[], int n){
        for(int i=1; i<=n; i++)
            if(v[i] == 0)
                return i; //primul nod nevizitat
        return 0; //nu mai exista noduri nevizitate
    }

    private int bfs(int c[], int v[], int[][] matrice, int n, int nodStart, int[] aux){
        int componenteConexe=1, k=0;
        v[nodStart]=1;
        int prim=1, ultim=1;
        c[ultim]=nodStart;
        while(prim <= ultim){
            for(int i=1; i<=n; i++)
                if(matrice[c[prim]][i] == 1)
                    if(v[i] == 0){
                        ultim++;
                        c[ultim]=i;
                        aux[k++]=i;
                        v[i]=1;
                    }
            prim++;
        }
        return componenteConexe;
    }

    /**
     * @return number of communities(int)
     */
    public int numberOfCommunities(){
        int nrUsers=0;
        if (userRepository.findAll() instanceof Collection)
            nrUsers = ((Collection<?>) userRepository.findAll()).size();

        int matrix[][]=adjacencyMatrix(friendshipRepository.findAll(), nrUsers+1);
        int numar, c[]=new int[nrUsers+1], v[]=new int[nrUsers+1], startNode=1, aux[]=new int[nrUsers+1];
        numar = bfs(c,v,matrix,nrUsers,startNode,aux);
        while(thereIsAnUnvisitedNode(v,nrUsers)!=0){
            startNode=thereIsAnUnvisitedNode(v,nrUsers);
            numar+=bfs(c,v,matrix,nrUsers,startNode,aux);
        }
        return numar;
    }

    /**
           @return component connected to the longest road
     */
    private int[] componentNodes(){
        int nrUsers=0;
        if (userRepository.findAll() instanceof Collection)
            nrUsers = ((Collection<?>) userRepository.findAll()).size();
        int[] rez=new int[nrUsers+1], rezultat = new int[nrUsers+1];
        int matrix[][]=adjacencyMatrix(friendshipRepository.findAll(), nrUsers+1);
        int c[]=new int[nrUsers+1], v[]=new int[nrUsers+1], startNode=1,lungime=0, laux;
        bfs(c,v,matrix,nrUsers,startNode,rez);
        for(int a : rez){
            if(a != 0)
                lungime++;
            else
                break;
        }
        while(thereIsAnUnvisitedNode(v,nrUsers)!=0){
            startNode=thereIsAnUnvisitedNode(v,nrUsers);
            bfs(c,v,matrix,nrUsers,startNode,rez);
            laux=rez.length;
            if(laux > lungime){
                lungime=laux;
                rezultat=rez;
            }
        }
        return rezultat;
    }

    /**
          @return list of users (users who make up the required path)
     */
    public List<User> largestCommunity(){
        List<User> users=new ArrayList<>();
        int id=componentNodes()[0];
        User start= userRepository.findOne((long)id);
        for(User friend : start.getFriends()){
            users.add(friend);
            for(User u : friend.getFriends())
                if(!users.contains(u))
                    users.add(u);
        }
        return users;
    }
}
