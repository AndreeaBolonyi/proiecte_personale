package socialnetwork.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import socialnetwork.domain.*;
import socialnetwork.events.UserChangeEvent;
import socialnetwork.observer.Observer;
import socialnetwork.repository.RepositoryException;
import socialnetwork.service.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.lang.*;

public class userPageController implements Observer<UserChangeEvent>{
    private Service service;
    ObservableList<UserView> modelUsers = FXCollections.observableArrayList();
    private int page;
    private int pageFriends;

    @FXML
    private Button account;
    @FXML
    private RadioButton friends;
    @FXML
    private AnchorPane rootAnchorPane;
    @FXML
    private TableColumn<UserView,String> tableColumnFirstName;
    @FXML
    private TableColumn<UserView,String> tableColumnLastName;
    @FXML
    private TableColumn<UserView,String> tableColumnEmail;
    @FXML
    private TableView<UserView> tableViewUsers;
    @FXML
    private TextField textFieldSearch;
    @FXML
    private Button addFriend;
    @FXML
    private Button removeFriend;
    @FXML
    private Button sendRequest;
    @FXML
    private Pagination paginationTableView;


    @FXML
    public void initialize(){
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableViewUsers.setItems(modelUsers);

        textFieldSearch.textProperty().addListener(x->handleFilter());
        addFriend.setText("Add friend");
        sendRequest.setText("Send request");

        paginationTableView.currentPageIndexProperty().addListener(x -> {
            if(friends.isSelected()){
                this.pageFriends=paginationTableView.getCurrentPageIndex();
                handleFriends();
            }
            else{
                this.page = paginationTableView.getCurrentPageIndex();
                initModel();
            }
        });
    }

    @FXML
    public void handleLogoutButtonAction(){
        try{
            //setup
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/startPage.fxml"));
            AnchorPane pane = loader.load();

            //controller
            startPageController ctrl = loader.getController();
            ctrl.setService(service);

            rootAnchorPane.getChildren().setAll(pane);
        }
        catch(Exception e){
            MessageAlert.showErrorMessage(null,"Nu se poate deschide fereastra");
        }
    }

    @FXML
    public void handleAddFriend(){
        UserView aux = tableViewUsers.getSelectionModel().getSelectedItem();
        User user = service.findUser(aux.getFirstName(),aux.getLastName(), aux.getEmail());
        if(user==null){
            MessageAlert.showErrorMessage(null,"Trebuie selectat un prieten");
        }
        else{
            Friendship f = service.addFriend(user);
            if(f==null)
                MessageAlert.showErrorMessage(null,"Sunteti deja prieteni");
            else
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Add","Prietenie realizata!");
        }
    }

    @FXML
    public void handleDeleteFriend(){
        UserView aux = tableViewUsers.getSelectionModel().getSelectedItem();
        User user = service.findUser(aux.getFirstName(),aux.getLastName(), aux.getEmail());
        if(user==null){
            MessageAlert.showErrorMessage(null,"Trebuie selectat un prieten");
        }
        else{
            try{
                Friendship f = service.deleteFriend(user);
                if(f == null)
                    MessageAlert.showErrorMessage(null,"Nu sunteti prieteni, deci nu il puteti sterge din lista de prieteni!");
                else{
                    MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Delete","Prietenul a fost sters!");
                    initModel();
                }
            }
            catch(RepositoryException e){
                MessageAlert.showErrorMessage(null,"Nu sunteti prieteni, deci nu il puteti sterge din lista de prieteni!");
            }
        }
    }

    @FXML
    public void handleFriends(){
        if(friends.isSelected()){
            //paginationTableView.setCurrentPageIndex(1);
            //pageFriends=1;
            //List<User> aux=service.getUserService().getFriends(service.getCurrentUser());
            List<UserView> friends = new ArrayList<>();
            for(User u : service.getFriendsOnPage(pageFriends))
                friends.add(new UserView(u.getFirstName(),u.getLastName(),u.getEmail()));
            modelUsers.setAll(friends);
            addFriend.setText("");
            removeFriend.setText("Remove friend");
            sendRequest.setText("");
        }
        else{
            initModel();
            addFriend.setText("Add friend");
            removeFriend.setText("");
            sendRequest.setText("Send request");
        }
    }

    @FXML
    public void handleMessages(){
        try{
            //setup
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/messagesPage.fxml"));
            AnchorPane pane = loader.load();

            //controller
            messagesPageController ctrl = loader.getController();
            ctrl.setService(service);

            rootAnchorPane.getChildren().setAll(pane);
        }
        catch(Exception e){
            //MessageAlert.showErrorMessage(null,"Nu se poate deschide fereastra");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRequests(){
        try{
            //setup
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/requestsPage.fxml"));
            AnchorPane pane = loader.load();

            //controller
            requestsPageController ctrl = loader.getController();
            ctrl.setService(service);

            rootAnchorPane.getChildren().setAll(pane);
        }
        catch(Exception e){
            MessageAlert.showErrorMessage(null,"Nu se poate deschide fereastra");
        }
    }

    @FXML
    public void handleSendRequest(){
        UserView aux = tableViewUsers.getSelectionModel().getSelectedItem();
        User user = service.findUser(aux.getFirstName(),aux.getLastName(), aux.getEmail());
        if(user==null){
            MessageAlert.showErrorMessage(null,"Trebuie selectat un user");
        }
        else{
            Request r = service.sendRequest(user);
            if(r==null)
                MessageAlert.showErrorMessage(null,"Ati trimis deja o cerere catre aceasta persoana");
            else
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Send","Cerere trimisa");
        }
    }

    @FXML
    public void handleActivity(){
        try{
            //setup
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/activityPage.fxml"));
            AnchorPane pane = loader.load();

            //controller
            activityController ctrl = loader.getController();
            ctrl.setService(service);

            rootAnchorPane.getChildren().setAll(pane);
        }
        catch(Exception e){
            MessageAlert.showErrorMessage(null,"Nu se poate deschide fereastra");
        }
    }

    @FXML
    public void handleEvents(){
        try{
            //setup
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/eventsPage.fxml"));
            AnchorPane pane = loader.load();

            //controller
            eventsPageController ctrl = loader.getController();
            ctrl.setService(service);

            rootAnchorPane.getChildren().setAll(pane);
        }
        catch(Exception e){
            MessageAlert.showErrorMessage(null,"Nu se poate deschide fereastra");
        }
    }

    private void handleFilter() {
        Predicate<UserView> firstNameFilterU = x->x.getFirstName().startsWith(textFieldSearch.getText().toUpperCase());
        Predicate<UserView> lastNameFilterU = x->x.getLastName().startsWith(textFieldSearch.getText().toUpperCase());
        Predicate<UserView> firstNameFilter = x->x.getFirstName().startsWith(textFieldSearch.getText());
        Predicate<UserView> lastNameFilter = x->x.getLastName().startsWith(textFieldSearch.getText());
        List<UserView> users=new ArrayList<>();
        for(User x : service.getAllUsers())
            users.add(new UserView(x.getFirstName(),x.getLastName(),x.getEmail()));
        modelUsers.setAll(users
                .stream()
                .filter(firstNameFilterU.or(lastNameFilterU).or(firstNameFilter).or(lastNameFilter))
                .collect(Collectors.toList())
        );
    }

    public void setService(Service service) throws InterruptedException {
        this.service = service;
        account.setText(service.getCurrentUser().getFirstName() + " " + service.getCurrentUser().getLastName());
        service.setPageSizeUsers(7);
        service.setPageSizeFriends(7);
        paginationTableView.setPageCount(service.sizeFindAll()/7+1);
        this.page =1;
        this.pageFriends=1;
        initModel();
        service.addObserver(this);
        service.tomorrow();
    }

    private List<UserView> findUsersList(){
        return service.getUsersOnPage(this.page).stream()
                .map(x->new UserView(x.getFirstName(),x.getLastName(),x.getEmail()))
                .collect(Collectors.toList());
    }

    @Override
    public void update(UserChangeEvent event) { initModel();
         }

    private void initModel(){
        modelUsers.setAll(findUsersList());
    }
}
