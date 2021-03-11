package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.password.Crypt;
import socialnetwork.service.Service;

import java.io.IOException;

public class signInPageController {
    private Service service;

    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldLastName;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private PasswordField textFieldPassword;
    @FXML
    private PasswordField textFieldPassword2;
    @FXML
    private AnchorPane rootAnchorPane;

    @FXML
    public void initialize(){

    }

    @FXML
    public void handleButtonAction(){
        if(!textFieldFirstName.getText().equals("") && !textFieldLastName.getText().equals("") && !textFieldEmail.getText().equals("") && !textFieldPassword.getText().equals("") && !textFieldPassword2.getText().equals("")){
            try{
                if(!textFieldPassword.getText().equals(textFieldPassword2.getText()))
                    MessageAlert.showErrorMessage(null,"Parolele nu coincid");
                else{
                    //adaugam userul nou creat
                    String password = Crypt.hashPassword(textFieldPassword.getText());
                    service.getUserService().addUser(textFieldFirstName.getText(), textFieldLastName.getText(),textFieldEmail.getText(), password);

                    //setup
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/views/userPage.fxml"));
                    AnchorPane pane = loader.load();

                    userPageController ctrl = loader.getController();
                    for(User u : service.getAllUsers()){
                        if(u.getEmail().equals(textFieldEmail.getText())){
                            service.setCurrentUser(u);
                            break;
                        }
                    }
                    try{
                        ctrl.setService(service);
                    }
                    catch(Exception e){

                    }

                    rootAnchorPane.getChildren().setAll(pane);
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
            catch(ValidationException e){
                MessageAlert.showErrorMessage(null,e.toString());
            }
        }
        else{
            MessageAlert.showErrorMessage(null,"Toate campurile trebuie completate");
        }
    }

    @FXML
    public void handleCancelButtonAction(){
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

    public void setService(Service service){
        this.service=service;
        User u=null;
        for(User user : service.getAllUsers()){
            if(user.getEmail().equals(textFieldEmail.getText()) && Crypt.checkPassword(textFieldPassword.getText(),user.getPassword())){
                u=user;
                break;
            }
        }
        service.setCurrentUser(u);
    }
}
