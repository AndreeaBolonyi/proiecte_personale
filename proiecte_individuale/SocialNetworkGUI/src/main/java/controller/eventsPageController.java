package socialnetwork.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import socialnetwork.domain.Event;
import socialnetwork.domain.Message;
import socialnetwork.domain.Notification;
import socialnetwork.events.UserChangeEvent;
import socialnetwork.observer.Observer;
import socialnetwork.service.Service;
import socialnetwork.service.ServiceException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class eventsPageController implements Observer<UserChangeEvent> {
    private Service service;
    private final List<Event> events = new ArrayList<>();

    @FXML
    private AnchorPane rootAnchorPane;
    @FXML
    private AnchorPane calendar;
    @FXML
    private Label labelNextEvent;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label infoEvent;
    @FXML
    private AnchorPane miniAnchorPane;
    @FXML
    private Label spatiu;

    @FXML
    public void initialize(){
        this.datePicker = new DatePicker(LocalDate.now());
        datePicker.valueProperty().addListener(x -> {
            boolean exist=false;
            for(Event e : events){
                LocalDate aux = e.getDate().toLocalDate();
                if(aux.equals(datePicker.getValue())) {
                    infoEvent.setText(e.getDescription());
                    exist=true;
                }
            }
            if(!exist)
                infoEvent.setText("No events in that day");
            miniAnchorPane.getChildren().clear();
            spatiu.setText("\n");
            miniAnchorPane.getChildren().addAll(infoEvent);
        });
        this.infoEvent.setFont(new Font("Times New Roman",12));
        spatiu.setText("\n\n\n\n\n\n");
    }

    @FXML
    public void handleGo(){
        LocalDate date = datePicker.getValue();
        if(date.isBefore(LocalDate.now()))
            MessageAlert.showErrorMessage(null,"Eveniment incheiat\n");
        else{
           try{
               Event e = service.addEvent(date);
               if(e != null)
                   MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Info","Participare inregistrata");
           }
           catch(ServiceException e){
               MessageAlert.showErrorMessage(null,e.toString());
           }
        }
    }

    @FXML
    public void handleCancel(){
        LocalDate date = datePicker.getValue();
        if(infoEvent.getText().equals(""))
            MessageAlert.showErrorMessage(null,"Selectati o data");
        else{
            if(date.isBefore(LocalDate.now()))
                MessageAlert.showErrorMessage(null,"Eveniment incheiat\n");
            else{
                try{
                    Event e = service.deleteEvent(date);
                    if(e != null)
                        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Info","Participare anulata");

                }
                catch(ServiceException e){
                    MessageAlert.showErrorMessage(null,e.toString());
                }
            }
        }
    }

    @FXML
    public void handleNotifications(){
        miniAnchorPane.getChildren().clear();
        spatiu.setText("\n");
        miniAnchorPane.getChildren().add(setNotifications());
    }

    private TableView<Notification> setNotifications(){
        TableView<Notification> tableView = new TableView<>();
        TableColumn<Notification,String> column1 = new TableColumn<>();
        column1.setCellValueFactory(new PropertyValueFactory<>("message"));
        TableColumn<Notification,String> column2 = new TableColumn<>();
        column2.setCellValueFactory(new PropertyValueFactory<>("event"));
        tableView.getColumns().addAll(column1,column2);

        for(Event e : events){
            if(e.getParticipants().contains(service.getCurrentUser())){
                LocalDateTime now = LocalDateTime.now();
                if(e.getDate().isBefore(now))
                    tableView.getItems().add(new Notification("The event is over", e));
                else
                    tableView.getItems().add(new Notification("Don't forget about it", e));
            }
        }

        tableView.setPrefHeight(200);
        column1.setPrefWidth(135);
        column2.setPrefWidth(230);

        tableView.getStylesheets().add("/css/userPage.css");
        return tableView;
    }

    @FXML
    public void handleSoon(){
        String info="";
        for(Notification n : service.findAllNotifications())
            if(n.getEvent().getParticipants().contains(service.getCurrentUser()))
                info+=n.getEvent().getDescription() + " is soon\n";
        this.infoEvent.setText(info);
        miniAnchorPane.getChildren().clear();
        miniAnchorPane.getChildren().setAll(this.infoEvent);
    }

    @FXML
    public void handleAddEvent(){
        try {
            //setup
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/addEventView.fxml"));
            AnchorPane root = (AnchorPane) loader.load();

            //stage
            Stage dialogStage=new Stage();
            dialogStage.setTitle("Add new event");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene=new Scene(root);
            dialogStage.setScene(scene);

            addEventController editMessage=loader.getController();
            editMessage.setService(service,dialogStage);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteEvent(){
        boolean deleted=false;
        for(Event e : events){
            if(e.getDate().toLocalDate().equals(datePicker.getValue()) && e.getOwner().equals(service.getCurrentUser())){
                service.deleteEventOwner(e);
                deleted=true;
            }
        }
        if(!deleted)
            MessageAlert.showErrorMessage(null,"Nu puteti sterge evenimentul daca nu l-ati organizat");
        else
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Delete","Eveniment sters cu succes");
        colorCalendar();
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
        for(Event e : service.findAllEvents())
            events.add(e);
        labelNextEvent.setText(nextEvent());
        service.addObserver(this);
        colorCalendar();
        DatePickerSkin datePickerSkin = new DatePickerSkin(datePicker);
        Node popupContent = datePickerSkin.getPopupContent();
        calendar.getChildren().addAll(popupContent);
        service.tomorrow();
    }

    private String nextEvent(){
        events.sort(Comparator.comparing(Event::getDate));
        for(Event e : events){
            if(e.getDate().isAfter(LocalDateTime.now()) && e.getParticipants().contains(service.getCurrentUser()))
                return "Next event is: " + e.getDescription() + "\n" + e.getDate().toString().replace("T", " ");
        }
        return "No future events for you";
    }

    private void colorCalendar(){
        final Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty && item != null) {
                            for(Event e : events){
                                LocalDate aux = e.getDate().toLocalDate();
                                if(aux.equals(item)){
                                    this.setStyle("-fx-background-color: pink");
                                }
                            }
                        }
                    }
                };
            }
        };
        datePicker.setDayCellFactory(dayCellFactory);
    }

    @Override
    public void update(UserChangeEvent event) {
            colorCalendar();
    }
}
