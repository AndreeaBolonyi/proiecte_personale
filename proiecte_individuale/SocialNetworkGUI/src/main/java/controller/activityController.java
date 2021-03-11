package socialnetwork.controller;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.property.TextAlignment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.Message;
import socialnetwork.domain.User;
import socialnetwork.domain.UserView;
import socialnetwork.events.UserChangeEvent;
import socialnetwork.observer.Observer;
import socialnetwork.service.Service;

import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;


public class activityController implements Observer<UserChangeEvent> {
    private Service service;
    private User currentUser;
    ObservableList<UserView> modelUsers = FXCollections.observableArrayList();
    private final List<UserView> friends = new ArrayList<>();
    private final List<LocalDateTime> dates = new ArrayList<>();
    private final List<Message> messages = new ArrayList<>();
    private final List<Message> messages2 = new ArrayList<>();
    private final List<LocalDateTime> dates2 = new ArrayList<>();

    @FXML
    private AnchorPane rootAnchorPane;
    @FXML
    private AnchorPane miniAnchorPane;
    @FXML
    private DatePicker datePickerStart1;
    @FXML
    private DatePicker datePickerEnd1;
    @FXML
    private DatePicker datePickerStart2;
    @FXML
    private DatePicker datePickerEnd2;
    @FXML
    private TableColumn<UserView,String> tableColumnFirstName;
    @FXML
    private TableColumn<UserView,String> tableColumnLastName;
    @FXML
    private TableColumn<UserView,String> tableColumnEmail;
    @FXML
    private TableView<UserView> tableViewUsers;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab tab1;
    @FXML
    private Tab tab2;
    @FXML
    private Chart chart;

    @FXML
    public void initialize(){
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableViewUsers.setItems(modelUsers);

        datePickerEnd1.valueProperty().addListener(x -> handleChart());
        datePickerStart1.valueProperty().addListener(x->{
            miniAnchorPane.getChildren().clear();
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

    @FXML
    public void handleChart(){
        initFriendsDatesMessages();
        chart = createChart();
        miniAnchorPane.getChildren().add(chart);
    }

    private Chart createChart(){
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        XYChart.Series<String,Number> series1 = new XYChart.Series<>();
        series1.setName("Friends");

        Map<LocalDate,Integer> map = new HashMap<>();
        for(LocalDateTime aux : dates){
            LocalDate a = aux.toLocalDate();
            int prev = 0;
            if(map.get(a) != null)
                prev = map.get(a);
            map.put(a,prev+1);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE ;
        for(Map.Entry<LocalDate,Integer> entry : map.entrySet()){
            String formattedDate = formatter.format(entry.getKey());
            int value = entry.getValue();
            series1.getData().add(new XYChart.Data<>(formattedDate,value));
        }

        XYChart.Series<String,Number> series2 = new XYChart.Series<>();
        series2.setName("Messages");
        Map<LocalDate,Integer> map2 = new HashMap<>();
        for(LocalDateTime aux : dates2){
            LocalDate a = aux.toLocalDate();
            int prev = 0;
            if(map2.get(a) != null)
                prev = map2.get(a);
            map2.put(a,prev+1);
        }

        for(Map.Entry<LocalDate,Integer> entry : map2.entrySet()){
            String formattedDate = formatter.format(entry.getKey());
            int value = entry.getValue();
            series2.getData().add(new XYChart.Data<>(formattedDate,value));
        }

        chart.setAnimated(true);
        chart.setTitle("Statistical analysis");
        chart.getData().addAll(series1,series2);
        chart.setPrefHeight(300);
        return  chart;
    }

    private void initFriendsDatesMessages(){
        LocalDate start1 = datePickerStart1.getValue();
        LocalDate end1 = datePickerEnd1.getValue();
        //cautam prietenii noi adaugati in intervalul [start1,end1]
        for(Friendship f : service.getFriendshipService().getAll()){
            if(f.getId().getLeft().equals(currentUser.getId())){
                if(f.getDate().toLocalDate().isAfter(start1) && f.getDate().toLocalDate().isBefore(end1)){
                    User aux = service.getUserService().getUser(f.getId().getRight());
                    friends.add(new UserView(aux.getFirstName(),aux.getLastName(),aux.getEmail()));
                    dates.add(f.getDate());
                }
            }
            if(f.getId().getRight().equals(currentUser.getId())){
                if(f.getDate().toLocalDate().isAfter(start1) && f.getDate().toLocalDate().isBefore(end1)){
                    User aux = service.getUserService().getUser(f.getId().getLeft());
                    friends.add(new UserView(aux.getFirstName(),aux.getLastName(),aux.getEmail()));
                    dates.add(f.getDate());
                }
            }
        }

        //cautam mesajele primite in intervalul [start1,end1]
        for(Message m : service.getNetworkService().getAllMessages()){
            if(m.getTo().contains(currentUser) && m.getDate().toLocalDate().isAfter(start1) && m.getDate().toLocalDate().isBefore(end1))
            {
                messages.add(m);
                dates2.add(m.getDate());
            }
        }
    }
    @FXML
    public void handleGeneratePDF(){
        try{
            Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();

            if(selectedTab.equals(tab1)){
                Group root = new Group(chart);
                Scene scene = new Scene(root,600,300);
                WritableImage image = scene.snapshot(null);
                File chartFile = new File("C:\\Users\\lenovo\\Desktop\\SocialNetworkGUI\\src\\main\\resources\\images\\chart.png");

                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", chartFile);
                } catch (Exception e) {
                    MessageAlert.showErrorMessage(null,"Nu se poate salva imaginea");
                }

                JFrame jFrame = new JFrame();
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setDialogTitle("Specify a file to save");

                int userSelection = jFileChooser.showSaveDialog(jFrame);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File fileToSave = jFileChooser.getSelectedFile();

                        //PdfWriter writer = new PdfWriter("C:\\Users\\lenovo\\Desktop\\" + "raport_prieteni_mesaje_" + currentUser.getLastName() + "_" + currentUser.getFirstName() +".pdf");
                        PdfWriter writer = new PdfWriter(fileToSave.toString());
                        PdfDocument pdfDoc = new PdfDocument(writer);
                        pdfDoc.addNewPage();
                        Document document = new Document(pdfDoc);

                        Paragraph title = new Paragraph();
                        title.add("New friends and received messages");
                        title.setTextAlignment(TextAlignment.CENTER);
                        document.add(title);

                        Paragraph subtitle = new Paragraph();
                        subtitle.add("Time period:" + datePickerStart1.getValue().toString() + " -> " + datePickerEnd1.getValue().toString());
                        subtitle.setTextAlignment(TextAlignment.CENTER);
                        document.add(subtitle);
                        document.add(new Paragraph(" "));

                        Paragraph paragraph3 = new Paragraph();
                        paragraph3.add("User: " + currentUser.getFirstName() + " " + currentUser.getLastName());
                        document.add(paragraph3);

                        Paragraph paragraph4 = new Paragraph();
                        paragraph4.add("Date: " + LocalDateTime.now().toString().replace("T"," "));
                        document.add(paragraph4);

                        document.add(new Paragraph(" "));


                        Paragraph paragraphFriends = new Paragraph("Friends added in that time");
                        paragraphFriends.setTextAlignment(TextAlignment.CENTER);
                        float [] pointColumnWidths = {150F, 150F, 150F,150F};
                        Table tableFriends = new Table(pointColumnWidths);
                        tableFriends.addCell(new Cell().add("First name"));
                        tableFriends.addCell(new Cell().add("Last name"));
                        tableFriends.addCell(new Cell().add("Email"));
                        tableFriends.addCell(new Cell().add("Date"));
                        int i=0;
                        for(UserView aux : friends) {
                            tableFriends.addCell(new Cell().add(aux.getFirstName()));
                            tableFriends.addCell(new Cell().add(aux.getLastName()));
                            tableFriends.addCell(new Cell().add(aux.getEmail()));
                            tableFriends.addCell(new Cell().add(dates.get(i).toString().replace("T"," ")));
                            i++;
                        }

                        Paragraph paragraphMessages = new Paragraph("Received messages in that time");
                        paragraphMessages.setTextAlignment(TextAlignment.CENTER);
                        Table tableMessages = new Table(pointColumnWidths);
                        tableMessages.addCell(new Cell().add("From"));
                        tableMessages.addCell(new Cell().add("Message"));
                        tableMessages.addCell(new Cell().add("Date"));
                        tableMessages.addCell(new Cell().add("Reply"));
                        for(Message m : messages){
                            tableMessages.addCell(new Cell().add(m.getFrom().getFirstName() + " " + m.getFrom().getLastName()));
                            tableMessages.addCell(new Cell().add(m.getMessage()));
                            tableMessages.addCell(new Cell().add(m.getDate().toString().replace("T"," ")));
                            if(m.getReply()!=null)
                                tableMessages.addCell(new Cell().add(m.getReply().toString()));
                            else
                                tableMessages.addCell(new Cell().add(""));
                        }

                        document.add(paragraphFriends);
                        document.add(tableFriends);
                        document.add(new Paragraph(" "));
                        document.add(new Paragraph(" "));
                        document.add(new Paragraph(" "));
                        document.add(paragraphMessages);
                        document.add(tableMessages);
                        document.add(new Paragraph(" "));
                        document.add(new Paragraph(" "));

                        String imageFile = "C:\\Users\\lenovo\\Desktop\\SocialNetworkGUI\\src\\main\\resources\\images\\chart.png";
                        ImageData data = ImageDataFactory.create(imageFile);
                        Image img = new Image(data);
                        document.add(img);

                        document.close();

                        JOptionPane.showMessageDialog(null, "PDF Saved");
                        datePickerStart1.getEditor().clear();
                        datePickerEnd1.getEditor().clear();
                    Desktop.getDesktop().open(new File(fileToSave.toString()));
                }
            }
            else {
                if (selectedTab.equals(tab2)) {
                    //cautam mesajele de la un anumit user in intervalul [start2,end2]
                    LocalDate start2 = datePickerStart2.getValue();
                    LocalDate end2 = datePickerEnd2.getValue();
                    User from = null;
                    UserView aux = tableViewUsers.getSelectionModel().getSelectedItem();
                    if (aux == null)
                        MessageAlert.showErrorMessage(null, "Selectati un prieten din tabel");
                    else {
                        from = service.findUser(aux.getFirstName(), aux.getLastName(), aux.getEmail());
                        for (Message m : service.getNetworkService().getAllMessages()) {
                            if (m.getFrom().equals(from) && m.getTo().contains(currentUser) && m.getDate().toLocalDate().isAfter(start2) && m.getDate().toLocalDate().isBefore(end2))
                                messages2.add(m);
                        }
                    }

                    //scriem datele in raport si cream fisierul pdf
                    JFrame jFrame = new JFrame();
                    JFileChooser jFileChooser = new JFileChooser();
                    jFileChooser.setDialogTitle("Specify a file to save");

                    int userSelection = jFileChooser.showSaveDialog(jFrame);
                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File fileToSave = jFileChooser.getSelectedFile();
                        PdfWriter writer = new PdfWriter(fileToSave.toString());
                        PdfDocument pdfDoc = new PdfDocument(writer);
                        pdfDoc.addNewPage();
                        Document document = new Document(pdfDoc);

                        Paragraph title = new Paragraph();
                        title.add("Received messages in that time from your friend");
                        title.setTextAlignment(TextAlignment.CENTER);
                        document.add(title);

                        Paragraph subtitle = new Paragraph();
                        subtitle.add("Period: " + datePickerStart2.getValue().toString() + " -> " + datePickerEnd2.getValue().toString());
                        subtitle.setTextAlignment(TextAlignment.CENTER);
                        document.add(subtitle);
                        document.add(new Paragraph(" "));

                        Paragraph paragraph3 = new Paragraph();
                        paragraph3.add("User: " + currentUser.getFirstName() + " " + currentUser.getLastName());
                        document.add(paragraph3);

                        Paragraph paragraph5 = new Paragraph();
                        paragraph5.add("Friend: " + from.getFirstName() + " " + from.getLastName());
                        document.add(paragraph5);

                        Paragraph paragraph4 = new Paragraph();
                        paragraph4.add("Date: " + LocalDateTime.now().toString().replace("T"," "));
                        document.add(paragraph4);

                        document.add(new Paragraph(" "));

                        float [] pointColumnWidths = {150F, 150F,150F};
                        Paragraph paragraphMessages = new Paragraph("Received messages in that time from your friend " + from.getFirstName() + " " + from.getLastName());
                        Table tableMessages = new Table(pointColumnWidths);
                        tableMessages.addCell(new Cell().add("Message"));
                        tableMessages.addCell(new Cell().add("Date"));
                        tableMessages.addCell(new Cell().add("Reply"));
                        for(Message m : messages2){
                            tableMessages.addCell(new Cell().add(m.getMessage()));
                            tableMessages.addCell(new Cell().add(m.getDate().toString().replace("T"," ")));
                            if(m.getReply()!=null){
                                String string = m.getReply().getFrom().getFirstName() + " " + m.getReply().getFrom().getLastName() + " " + " sent the message " + m.getReply().getMessage() + " on " + m.getReply().getDate().toString().replace("T", " ");
                                tableMessages.addCell(new Cell().add(string));
                            }
                            else
                                tableMessages.addCell(new Cell().add(""));
                        }

                        document.add(paragraphMessages);
                        document.add(tableMessages);
                        document.close();

                        datePickerStart2.getEditor().clear();
                        datePickerEnd2.getEditor().clear();
                        JOptionPane.showMessageDialog(null, "PDF Saved");
                        Desktop.getDesktop().open(new File(fileToSave.toString()));
                    }
                } else {
                    MessageAlert.showErrorMessage(null, "Selecteaza un tab");
                }
            }
        }
        catch(IOException e){
            MessageAlert.showErrorMessage(null,"Nu se poate genera pdf");
        }
    }

    @Override
    public void update(UserChangeEvent event) {
        initModel();
    }

    public void setService(Service service) {
        this.service = service;
        this.currentUser= service.getCurrentUser();
        initModel();
        service.addObserver(this);
    }

    private void initModel(){
        modelUsers.setAll(service.findUserFriends());
    }
}



