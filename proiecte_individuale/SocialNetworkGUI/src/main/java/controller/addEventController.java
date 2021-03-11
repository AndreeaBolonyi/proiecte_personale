package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;
import socialnetwork.domain.Event;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.events.UserChangeEvent;
import socialnetwork.observer.Observer;
import socialnetwork.repository.RepositoryException;
import socialnetwork.service.Service;
import socialnetwork.service.ServiceException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class addEventController implements Observer<UserChangeEvent> {
    private Service service;
    private Stage dialogStage;

    @FXML
    private TextField textFieldDescription;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField textFieldHour;
    @FXML
    private Label labelTime;
    @FXML
    private Label labelDescription;
    @FXML
    private Label labelDate;

    public void setService(Service srv, Stage dialogStage){
        this.service=srv;
        this.dialogStage=dialogStage;
        this.service.addObserver(this);
    }

    @FXML
    public void initialize(){
        labelTime.setText("Time\n(the format is hh:mm)");labelTime.setTextAlignment(TextAlignment.CENTER);
        labelDescription.setText("Description\n(write some words about your event)");
        labelDate.setText("Date\n(select a date from the calendar)");
    }

    @FXML
    public void handleAdd(){
        if(textFieldHour.getText().equals("") || textFieldDescription.getText().equals(""))
            MessageAlert.showErrorMessage(null,"Toate campurile trebuie completate");
        else{
            String description = textFieldDescription.getText();
            LocalDate date = datePicker.getValue();
            String time = textFieldHour.getText();
            String str = date.toString() + " " + time;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(str, formatter);

            try{
                List<User> list = new ArrayList<>(); list.add(service.getCurrentUser());
                Event newEvent = new Event(dateTime,description,list,service.getCurrentUser());
                Event e = service.addNewEvent(newEvent);
                if(e != null)
                    MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Info","Eveniment creat");
                else
                    MessageAlert.showErrorMessage(null,"Eroare la adaugare");
                clearFields();
            }
            catch(RepositoryException e){
                MessageAlert.showErrorMessage(null,e.toString());
            }
            catch(ServiceException e){
                MessageAlert.showErrorMessage(null,e.toString());
            }
            catch(ValidationException e){
                MessageAlert.showErrorMessage(null,e.toString());
            }
        }
    }

    @FXML
    public void handleCancel(){
        dialogStage.close();
    }

    private void clearFields() {
        textFieldDescription.setText("");
        textFieldHour.setText("");
        datePicker.getEditor().clear();
    }

    @Override
    public void update(UserChangeEvent event) {

    }
}
