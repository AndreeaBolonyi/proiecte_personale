package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import socialnetwork.domain.Message;
import socialnetwork.domain.MessageDTO;
import socialnetwork.domain.User;
import socialnetwork.domain.UserView;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.events.UserChangeEvent;
import socialnetwork.observer.Observer;
import socialnetwork.repository.RepositoryException;
import socialnetwork.service.Service;
import socialnetwork.service.ServiceException;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class messagesPageController implements Observer<UserChangeEvent> {
    private Service service;
    ObservableList<UserView> modelUsers = FXCollections.observableArrayList();
    ObservableList<MessageDTO> modelMessages = FXCollections.observableArrayList();
    private int page;
    private UserView selectedItem=null;

    @FXML
    private ListView<UserView> listViewUsers;
    @FXML
    private ListView<MessageDTO> listViewMessages;
    @FXML
    private TextField textFieldSearch;
    @FXML
    private TextField textFieldMessage;
    @FXML
    private AnchorPane rootAnchorPane;
    @FXML
    private ScrollPane messages;
    @FXML
    private Pagination pagination;


    @FXML
    public void initialize(){
        listViewUsers.setItems(modelUsers);
        textFieldSearch.textProperty().addListener(x->handleFilter());
        listViewUsers.getSelectionModel().selectedItemProperty().addListener(x -> {
            this.selectedItem = listViewUsers.getSelectionModel().getSelectedItem();
            this.page=1;
            pagination.setCurrentPageIndex(1);
            modelMessages.clear();
            initModel2();
            listViewMessages.setItems(modelMessages);
            listViewMessages.setCellFactory(param -> {
                ListCell<MessageDTO> cell = new ListCell<MessageDTO>(){
                    Label lblTextLeft = new Label();
                    HBox hBoxLeft = new HBox(lblTextLeft);

                    Label lblTextRight = new Label();
                    HBox hBoxRight = new HBox(lblTextRight);

                    {
                        hBoxLeft.setAlignment(Pos.CENTER_LEFT);
                        hBoxLeft.setSpacing(5);
                        hBoxRight.setAlignment(Pos.CENTER_RIGHT);
                        hBoxRight.setSpacing(5);
                    }
                    @Override
                    protected void updateItem(MessageDTO item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty)
                        {
                            setText(null);
                            setGraphic(null);
                        }
                        else{
                            if(!item.getFrom().equals(service.getCurrentUser()))
                            {
                                lblTextLeft.setText(item.toString());
                                setGraphic(hBoxLeft);
                            }
                            else{
                                lblTextRight.setText(item.toString());
                                setGraphic(hBoxRight);
                            }
                        }
                    }
                };
                return cell;
            });
        });
        textFieldMessage.setPromptText("Insert message");
        pagination.currentPageIndexProperty().addListener(x -> {
            this.page = pagination.getCurrentPageIndex();
            modelMessages.clear();
            initModel2();
            listViewMessages.setItems(modelMessages);
        });
    }

    @FXML
    public void handleBack(){
        try{
            //setup
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/userPage.fxml"));
            AnchorPane pane = loader.load();

            //controller
            userPageController ctrl = loader.getController();
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
        modelUsers.setAll(service.findUserFriends()
                .stream()
                .filter(firstNameFilterU.or(lastNameFilterU).or(firstNameFilter).or(lastNameFilter))
                .collect(Collectors.toList())
        );
    }

    public void handleSendMessage(){
        MessageDTO selected = listViewMessages.getSelectionModel().getSelectedItem();
        List<Long> lista = new ArrayList<>();
        //this.selectedItem = listViewUsers.getSelectionModel().getSelectedItem();
        User aux = service.findUser(selectedItem.getFirstName(),selectedItem.getLastName(),selectedItem.getEmail());
        lista.add(aux.getId());
        Message reply = null;
        try{
            if(selected != null){
                for(Message m : service.getNetworkService().getAllMessages()){
                    if(m.getFrom().equals(selected.getFrom()) && m.getMessage().equals(selected.getMessage()) && m.getDate().equals(selected.getDate()))
                        reply = m;
                }
                service.sendMessage(lista,textFieldMessage.getText(),reply.getId());
            }
            else{
                service.sendMessage(lista,textFieldMessage.getText(),null);
            }
        }
        catch(ServiceException e){
            MessageAlert.showErrorMessage(null,e.toString());
        }
        catch(ValidationException e){
            MessageAlert.showErrorMessage(null,e.toString());
        }
        catch(RepositoryException e){
            MessageAlert.showErrorMessage(null,e.toString());
        }
        textFieldMessage.clear();
        initModel2();
    }

    private void initModel1(){
        modelUsers.setAll(service.findUserFriends());
    }

    private void initModel2(){
        if(selectedItem != null){
            User aux = service.findUser(selectedItem.getFirstName(),selectedItem.getLastName(),selectedItem.getEmail());
            //List<Message> conversation = service.getNetworkService().getConversation(service.getCurrentUser().getId(),aux.getId());
            Set<Message> auxConversation = service.getMessagesOnPage(this.page, aux);
            Set<Message> conversation = auxConversation.stream().sorted(Comparator.comparing(Message::getDate)).collect(Collectors.toSet());
            for(Message m : conversation){
                MessageDTO a;
                if(m.getReply() != null)
                    a = new MessageDTO(m.getFrom(),m.getTo().get(0),m.getMessage(),m.getDate(),m.getReply());
                else
                    a = new MessageDTO(m.getFrom(),m.getTo().get(0),m.getMessage(),m.getDate(),null);
                modelMessages.add(a);
            }
        }
    }

    private void initMessages(){
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        for(MessageDTO m : modelMessages){
            Label text = new Label(m.toString());
            HBox hbox = new HBox();
            hbox.getChildren().add(text);
            if(m.getFrom().equals(service.getCurrentUser()))
                hbox.setAlignment(Pos.BASELINE_RIGHT);
            else
                hbox.setAlignment(Pos.BASELINE_LEFT);
            vbox.getChildren().addAll(hbox);
        }
        messages.setContent(vbox);
    }

    public void setService(Service srv){
        this.service=srv;
        service.addObserver(this);
        initModel1();
        this.page=1;
        service.setPageSizeMessages(5);
        int nr=0; for(Message m : service.getNetworkService().getAllMessages()) nr++;
        pagination.setPageCount(nr/5+1);
        pagination.setCurrentPageIndex(this.page);
    }

    @Override
    public void update(UserChangeEvent event) { initModel1(); initModel2();}
}
