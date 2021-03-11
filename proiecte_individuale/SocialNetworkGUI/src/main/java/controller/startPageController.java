package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import socialnetwork.domain.User;
import socialnetwork.password.Crypt;
import socialnetwork.service.Service;

public class startPageController{
    private Service service;

    @FXML
    private TextField textFieldEmail;
    @FXML
    private PasswordField textFieldPassword;
    @FXML
    private AnchorPane rootAnchorPane;
    @FXML
    private RadioButton rememberMeButton;
    @FXML
    private Label wrongPassword;

    @FXML
    public void handleButtonLoginAction() {
       try {
           if(textFieldEmail.getText().equals("") || textFieldPassword.getText().equals("")){
               MessageAlert.showErrorMessage(null,"Campurile trebuie completate neaparat!");
               return;
           }
           //setup
           FXMLLoader loader = new FXMLLoader();
           loader.setLocation(getClass().getResource("/views/userPage.fxml"));
           AnchorPane pane = loader.load();

           //controller
           userPageController ctrl = loader.getController();
           boolean ok=true;
           for(User user : service.getAllUsers()){
               if(user.getEmail().equals(textFieldEmail.getText()) && !Crypt.checkPassword(textFieldPassword.getText(),user.getPassword())){
                   //MessageAlert.showErrorMessage(null,"Reintroduceti parola, este gresita");
                   wrongPassword.setText("Reintroduceti parola, este gresita");
                   wrongPassword.setFont(new Font("Times New Roman",10));
                   ok=false;
               }
               else
                   if(user.getEmail().equals(textFieldEmail.getText()) && Crypt.checkPassword(textFieldPassword.getText(),user.getPassword())){
                   service.setCurrentUser(user);
                   break;
               }
           }
           if(ok) {
               ctrl.setService(service);
               rootAnchorPane.getChildren().setAll(pane);
           }
       }
       catch(Exception e){
           MessageAlert.showErrorMessage(null,"Nu se poate deschide fereastra");
       }
    }

    @FXML
    public void handleButtonSigninAction(){
        try{
            //setup
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/signInPage.fxml"));
            AnchorPane pane = loader.load();

            //controller
            signInPageController ctrl = loader.getController();
            ctrl.setService(service);

            rootAnchorPane.getChildren().setAll(pane);
        }
        catch(Exception e){
            MessageAlert.showErrorMessage(null,"Nu se poate deschide fereastra");
        }
    }

    @FXML
    public void handleRemember(){
        String email = textFieldEmail.getText();
        String password = textFieldPassword.getText();
        service.newUserToRemember(email,password);
    }

    @FXML
    public void initialize() {
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
        if(!rememberMeButton.isSelected()){
            service.deleteLoggedUser();
        }
    }
}