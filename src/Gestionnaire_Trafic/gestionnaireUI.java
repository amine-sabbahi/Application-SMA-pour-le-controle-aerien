package Gestionnaire_Trafic;

import DataBase.Allocation;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.*;

public class gestionnaireUI extends Application {

    protected gestionnaireAgent gestionnaireAgent;
    ObservableList<String> observableList;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        StartContainer();
        Image iconstage = new Image("gestionnaire.png");


        // Set the icon for the stage
        stage.getIcons().add(iconstage);

        stage.setTitle("Gestionnaire_Trafic");

        // Create top part (table)
        TableView<Allocation> tableView = new TableView<>();
        tableView.setPrefHeight(400); // Adjust the height as needed

        // Create table columns
        TableColumn<Allocation, String> compagnieColumn = new TableColumn<>("Compagnie");
        compagnieColumn.setCellValueFactory(new PropertyValueFactory<>("compagnie"));

        TableColumn<Allocation, String> avionColumn = new TableColumn<>("Avion");
        avionColumn.setCellValueFactory(new PropertyValueFactory<>("avion"));

        TableColumn<Allocation, String> piloteColumn = new TableColumn<>("Pilote");
        piloteColumn.setCellValueFactory(new PropertyValueFactory<>("pilote"));

        TableColumn<Allocation, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Allocation, Integer> heureColumn = new TableColumn<>("Heure");
        heureColumn.setCellValueFactory(new PropertyValueFactory<>("heure"));

        TableColumn<Allocation, Integer> minuteColumn = new TableColumn<>("Minute");
        minuteColumn.setCellValueFactory(new PropertyValueFactory<>("minute"));

        TableColumn<Allocation, String> arriveColumn = new TableColumn<>("Arrivé");
        arriveColumn.setCellValueFactory(new PropertyValueFactory<>("arrive"));

        tableView.getColumns().addAll(compagnieColumn, avionColumn, piloteColumn, dateColumn, heureColumn, minuteColumn, arriveColumn);
        // Set the preferred width of each column as a percentage of the total width
        compagnieColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15)); // 15%
        avionColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15)); // 15%
        piloteColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15)); // 15%
        dateColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15)); // 15%
        heureColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.10)); // 10%
        minuteColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.10)); // 10%
        arriveColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.20)); // 20%

        // Create bottom part (list of conversation messages)

        observableList = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<String>(observableList);

        listView.setPrefHeight(250); // Adjust the height as needed

        // Create a BorderPane to hold both parts
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(tableView);
        borderPane.setBottom(listView);
        // Create a Text node for the "HELLO" message
        Text helloText = new Text("Communication avec Compagnie et DirecteurFlux");
        helloText.setFont(Font.font("Calibri", FontWeight.BOLD, 22)); // Larger font size and different font family

        // Create a StackPane to center the text
        StackPane helloPane = new StackPane(helloText);

        // Set the center of the BorderPane to the StackPane containing the text
        borderPane.setCenter(helloPane);
        BorderPane.setAlignment(helloPane, Pos.CENTER); // Align the StackPane to the center

        // Create the scene
        Scene scene = new Scene(borderPane, 800, 600); // Adjust width and height as needed

        stage.setScene(scene);
        stage.show();
        // Load data into the TableView initially
        loadData(tableView);

        // Setup a timeline to periodically refresh the TableView

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            tableView.getItems().clear(); // Clear existing data
            loadData(tableView); // Load fresh data
        }));
        timeline.setCycleCount(Timeline.INDEFINITE); // Repeat indefinitely
        timeline.play();
    }
    public void StartContainer() throws StaleProxyException {

        Runtime runtime=Runtime.instance();
        ProfileImpl profileImpl =new ProfileImpl();
        profileImpl.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        // creation d'un container
        AgentContainer container = runtime.createAgentContainer(profileImpl);
        //creation d'un agent (deploiement de l'agent dans le container)
        AgentController agentController= container
                .createNewAgent("gestionnaire","Gestionnaire_Trafic.gestionnaireAgent",new Object[]{this});
        agentController.start();
    }


    public void logMessage(ACLMessage aclMessage) {
        Platform.runLater(() -> {
            observableList.add(aclMessage.getContent());
        });

    }
    private void loadData(TableView<Allocation> tableView) {
        try {
            // Establish database connection
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Allocation", "root", "");

            // Create SQL statement
            Statement statement = connection.createStatement();

            // Execute query to fetch data from Allocation table
            ResultSet resultSet = statement.executeQuery("SELECT * FROM allocation");

            // Populate the TableView with data from the ResultSet
            while (resultSet.next()) {
                Allocation allocation = new Allocation(
                        resultSet.getString("compagnie"),
                        resultSet.getString("avion"),
                        resultSet.getString("pilote"),
                        resultSet.getString("date"),
                        resultSet.getInt("heure"),
                        resultSet.getInt("minute"),
                        resultSet.getString("arrivé"),
                        resultSet.getInt("piste")
                );
                tableView.getItems().add(allocation);
            }

            // Close resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
