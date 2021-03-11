package socialnetwork;

import com.itextpdf.io.font.otf.GsubLookupType1;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.config.ApplicationContext;
import socialnetwork.controller.startPageController;
import socialnetwork.controller.userPageController;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.*;
import socialnetwork.repository.paging.PagingRepository;
import socialnetwork.service.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainFX extends Application {
    final String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.url");
    final String username= ApplicationContext.getPROPERTIES().getProperty("databse.socialnetwork.username");
    final String password= ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.pasword");
    PagingRepository<Long,User> repo = new UserDBRepository(url,username,password,new UserValidator());
    PagingRepository<Tuple<Long,Long>, Friendship> repo2 = new FriendshipDBRepository(url,username,password,new FriendshipValidator());
    PagingRepository<Long, Request> repo3 = new RequestDBRepository(url,username,password, new RequestValidator());
    PagingRepository<Long, Message> repo4 = new MessageDBRepository(url,username,password, new MessageValidator(),repo);
    PagingRepository<Long,Event> repo5 = new EventDBRepository(url,username,password,repo, new EventValidator());
    PagingRepository<Long,Notification> repo6 = new NotificationDBRepository(url,username,password,repo5);
    Repository<Long,User> repo7 = new UserLoggedDBRepository(url,username,password,new UserValidator(),repo);
    Service srv = new Service(repo2,repo3,repo4,repo,repo5,repo6,repo7);

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        User logged = srv.getUserLogged();
        if(logged == null){
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/startPage.fxml"));
            AnchorPane root=loader.load();

            startPageController ctrl = loader.getController();
            ctrl.setService(srv);

            Scene scene = new Scene(root, 700, 500);
            stage.getIcons().add(new Image("/images/icon.png"));
            stage.setTitle("Social App");

            scene.getStylesheets().add(getClass().getResource("/css/startPage.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        }
        else{
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/userPage.fxml"));
            AnchorPane root=loader.load();

            userPageController ctrl = loader.getController();
            srv.setCurrentUser(logged);
            ctrl.setService(srv);

            Scene scene = new Scene(root, 700, 500);
            stage.getIcons().add(new Image("/images/icon.png"));
            stage.setTitle("Social App");

            scene.getStylesheets().add(getClass().getResource("/css/startPage.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        }
    }

    public static void main(String[] args) {
        launch();
    }

    private Service initService(){
        return srv;
    }
}
