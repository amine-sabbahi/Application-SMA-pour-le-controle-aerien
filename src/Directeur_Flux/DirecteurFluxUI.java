package Directeur_Flux;

import DataBase.Allocation;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
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

public class DirecteurFluxUI extends Application {

    ObservableList<String> observableList;
    protected DirecteurFluxAgent directeurFluxAgent;

    public void setControleurAgent(DirecteurFluxAgent directeurFluxAgent) {
        this.directeurFluxAgent = directeurFluxAgent;
    }



    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        StartContainer();
        Image iconstage = new Image("directeur.png");


        // Set the icon for the stage
        stage.getIcons().add(iconstage);

        stage.setTitle("DirecteurFlux");
        stage.setWidth(600);
        stage.setHeight(400);

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

        TableColumn<Allocation, String> DateColumn = new TableColumn<>("Date Atterissage");
        DateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Allocation, String> HeureColumn = new TableColumn<>("Heure Atterissage");
        HeureColumn.setCellValueFactory(cellData -> {
            String heureMinute = cellData.getValue().getHeureAtterissage(); // Get the hour and minute value from the Allocation object
            String[] parts = heureMinute.split(":"); // Split the hourMinute string into hour and minute parts
            String formattedHeureMinute =  parts[0] + " : " + parts[1]; // Format the hour and minute
            return new SimpleStringProperty(formattedHeureMinute); // Return a SimpleStringProperty with the formatted value
        });


        TableColumn<Allocation, String> PisteColumn = new TableColumn<>("Piste");
        PisteColumn.setCellValueFactory(new PropertyValueFactory<>("piste"));


        // Add more columns for Date, Heure, Minute, and Arrivé if needed

        tableView.getColumns().addAll(compagnieColumn, avionColumn, piloteColumn,DateColumn ,HeureColumn ,PisteColumn );

        compagnieColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.20)); // 20%
        avionColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15)); // 15%
        piloteColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.20)); // 20%
        DateColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.20)); // 20%
        HeureColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15)); // 15%
        PisteColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.10)); // 10%


        // Create bottom part (list of conversation messages)
        ListView<String> listView = new ListView<>();
        observableList = FXCollections.observableArrayList();
        listView.setItems(observableList);
        listView.setPrefHeight(250); // Adjust the height as needed
        // Create a BorderPane to hold both parts
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(tableView);
        borderPane.setBottom(listView);
        // Create a Text node for the "HELLO" message
        Text helloText = new Text("Communication avec Avion et GestionnaireTrafic");
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
    private void loadData(TableView<Allocation> tableView) {
        try {
            // Establish database connection
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Allocation", "root", "");

            // Create SQL statement
            Statement statement = connection.createStatement();

            // Execute query to fetch data from Allocation table
            ResultSet resultSet = statement.executeQuery("SELECT * FROM allocation WHERE arrivé = 'Oui'");

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



    public void StartContainer() throws ControllerException {
        // rendre les pistes dipos a chaque demarrage du container

        //DisplyData();


        Runtime runtime=Runtime.instance();
        ProfileImpl profileImpl =new ProfileImpl();
        profileImpl.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        // creation d'un container
        AgentContainer container = runtime.createAgentContainer(profileImpl);
        //creation d'un agent (deploiement de l'agent dans le container)
        AgentController agentController= container
                .createNewAgent("DirecteurFlux","Directeur_Flux.DirecteurFluxAgent",new Object[]{this});
        agentController.start();
    }

    public void logMessage(String aclMessage) {
        Platform.runLater(() -> {
            observableList.add(aclMessage);
        });

    }


    }
