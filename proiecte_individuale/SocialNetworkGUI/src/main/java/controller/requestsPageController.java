package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import socialnetwork.domain.Request;
import socialnetwork.domain.RequestDTO;
import socialnetwork.domain.User;
import socialnetwork.events.UserChangeEvent;
import socialnetwork.observer.Observer;
import socialnetwork.service.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class requestsPageController implements Observer<UserChangeEvent> {
    private Service service;
    private User currentUser;
    ObservableList<RequestDTO> modelRequestsReceived = FXCollections.observableArrayList();
    ObservableList<RequestDTO> modelRequestsSent = FXCollections.observableArrayList();
    private int pageReceived;
    private int pageSent;

    @FXML
    private AnchorPane rootAnchorPane;
    @FXML
    private TableColumn<RequestDTO,String> tableColumnUserReceived;
    @FXML
    private TableColumn<RequestDTO,String> tableColumnStatusReceived;
    @FXML
    private TableColumn<RequestDTO,String> tableColumnDateReceived;
    @FXML
    private TableView<RequestDTO> tableViewRequestsReceived;
    @FXML
    private TableColumn<RequestDTO,String> tableColumnReceiverSent;
    @FXML
    private TableColumn<RequestDTO,String> tableColumnStatusSent;
    @FXML
    private TableColumn<RequestDTO,String> tableColumnDateSent;
    @FXML
    private TableView<RequestDTO> tableViewRequestsSent;
    @FXML
    private Pagination paginationTableViewSent;
    @FXML
    private Pagination paginationTableViewReceived;

    @FXML
    public void initialize(){
        tableColumnUserReceived.setCellValueFactory(new PropertyValueFactory<>("user"));
        tableColumnStatusReceived.setCellValueFactory(new PropertyValueFactory<>("status"));
        tableColumnDateReceived.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableViewRequestsReceived.setItems(modelRequestsReceived);

        tableColumnReceiverSent.setCellValueFactory(new PropertyValueFactory<>("user"));
        tableColumnStatusSent.setCellValueFactory(new PropertyValueFactory<>("status"));
        tableColumnDateSent.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableViewRequestsSent.setItems(modelRequestsSent);

        paginationTableViewReceived.currentPageIndexProperty().addListener(x -> {
            this.pageReceived=paginationTableViewReceived.getCurrentPageIndex();
            initModelReceived();
        });
        paginationTableViewSent.currentPageIndexProperty().addListener(x -> {
            this.pageSent=paginationTableViewSent.getCurrentPageIndex();
            initModelSent();
        });
    }

    @FXML
    public void handleAcceptRequest(){
        RequestDTO aux = tableViewRequestsReceived.getSelectionModel().getSelectedItem();
        if(aux==null){
            MessageAlert.showErrorMessage(null,"Trebuie selectata o cerere");
        }
        else{
            Request r = new Request(aux.getUser().getId(),currentUser.getId(),aux.getStatus(),aux.getDate());
            for(Request i : service.getNetworkService().getAllRequests()){
                if(i.getId1().equals(aux.getUser().getId()) && i.getId2().equals(currentUser.getId())){
                    r.setId(i.getId());
                    break;
                }
            }
            Request rez = service.acceptRequest(currentUser.getId(), r);
            if(rez!=null)
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Answer","Raspuns salvat");
            else
                MessageAlert.showErrorMessage(null,"Hopa, a aparut o eroare!");
            initModelReceived();
        }
    }

    @FXML
    public void handleRejectRequest(){
        RequestDTO aux = tableViewRequestsReceived.getSelectionModel().getSelectedItem();
        if(aux==null){
            MessageAlert.showErrorMessage(null,"Trebuie selectata o cerere");
        }
        else {
            Request r = new Request(aux.getUser().getId(), currentUser.getId(), aux.getStatus(), aux.getDate());
            for (Request i : service.getNetworkService().getAllRequests()){
                if (i.getId1().equals(aux.getUser().getId()) && i.getId2().equals(currentUser.getId())) {
                    r.setId(i.getId());
                    break;
                }
            }
            Request rez = service.rejectRequest(currentUser.getId(), r);
            if(rez!=null)
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Answer","Raspuns salvat");
            else
                MessageAlert.showErrorMessage(null,"Hopa, a aparut o eroare!");
            initModelReceived();
        }
    }

    @FXML
    public void handleDeleteRequest(){
        RequestDTO aux = tableViewRequestsSent.getSelectionModel().getSelectedItem();
        if(aux==null){
            MessageAlert.showErrorMessage(null,"Trebuie selectata o cerere");
        }
        else {
            Request r = new Request(currentUser.getId(),aux.getUser().getId(), aux.getStatus(), aux.getDate());
            for (Request i : service.getNetworkService().getAllRequests()) {
                if (i.getId2().equals(aux.getUser().getId()) && i.getId1().equals(currentUser.getId())) {
                    r.setId(i.getId());
                    break;
                }
            }
            Request rez = service.deleteRequest(r);
            if(rez!=null)
                MessageAlert.showErrorMessage(null,"Nu se mai poate anula cererea");
            else
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Delete","Cerere stearsa");
            initModelSent();
        }
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

    public void setService(Service srv){
        this.service=srv;
        this.currentUser = srv.getCurrentUser();
        service.addObserver(this);
        initModelSent();
        initModelReceived();
        this.pageReceived=1;
        this.pageReceived=1;
        service.setPageSizeReceived(3);
        service.setPageSizeSent(3);
        paginationTableViewSent.setPageCount(service.sizeFindAllSent()/3+1);
        paginationTableViewReceived.setPageCount(service.sizeFindAllReceived()/3+1);
    }

    private void initModelReceived(){
        List<RequestDTO> aux = new ArrayList<>();
        //for(Request r : service.getNetworkService().getAllRequests()){
        for(Request r : service.getReceivedOnPage(this.pageReceived)){
            //if(r.getId2().equals(currentUser.getId())){
                User user = service.getUserService().getUser(r.getId1());
                RequestDTO rq = new RequestDTO(r.getId1(),user,r.getStatus(),r.getDate());
                aux.add(rq);
            //}
        }
        modelRequestsReceived.setAll(aux.stream().collect(Collectors.toList()));
    }

    private void initModelSent(){
        List<RequestDTO> aux = new ArrayList<>();
        //for(Request r : service.getNetworkService().getAllRequests()){
        for(Request r : service.getSentOnPage(this.pageSent)){
            //if(r.getId1().equals(currentUser.getId())){
                User user = service.getUserService().getUser(r.getId2());
                RequestDTO rq = new RequestDTO(r.getId1(),user,r.getStatus(),r.getDate());
                aux.add(rq);
            //}
        }
        modelRequestsSent.setAll(aux.stream()
                .collect(Collectors.toList()));
    }

    @Override
    public void update(UserChangeEvent event) {
        initModelReceived();
        initModelSent();
    }
}
