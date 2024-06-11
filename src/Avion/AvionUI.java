package Avion;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.*;

public class AvionUI extends Application{

    protected AvionAgent avionAgent;

    ObservableList<String> observableList;
    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Allocation";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";




    private Connection connection;

    //private int PisteAlloué =0 ; // initialisation par hasard

    /*public int getPisteAlloué() {
        return PisteAlloué;
    }

    public void setPisteAlloué(int pisteAlloué) {
        PisteAlloué = pisteAlloué;
    }*/
    // Database connection details


    // Method to fetch avion options based on compagnie selection

    public static void main(String[] args){
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {


        StartContainer();
        // Establish database connection
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Image iconstage = new Image("avion.png");


        // Set the icon for the stage
        primaryStage.getIcons().add(iconstage);
        // Create form components
        ChoiceBox<String> select1 = new ChoiceBox<>();


        ChoiceBox<String> select2 = new ChoiceBox<>();



        Button decollageButton = new Button("Décollage");
        Button atterrissageButton = new Button("Atterrissage");
        // Set both buttons to be initially disabled
        decollageButton.setDisable(true);
        atterrissageButton.setDisable(true);
        // Create a form layout
        GridPane formPane = new GridPane();
        formPane.setPadding(new Insets(30));
        formPane.setHgap(30);
        formPane.setVgap(30);
        formPane.setAlignment(Pos.CENTER);

        // Add labels with padding to the left
        Label selectLabel1 = new Label("Compagnie :");
        Label selectLabel2 = new Label("Avion :");

        formPane.addRow(0, selectLabel1, select1);
        formPane.addRow(1, selectLabel2, select2);
        formPane.add(decollageButton, 0, 2);
        formPane.add(atterrissageButton, 1, 2);

        // Create image views for left and right sides
        ImageView leftImageView = new ImageView();
        leftImageView.setFitWidth(500); // Adjust the width of the image view as needed

        leftImageView.setPreserveRatio(true);
        ImageView rightImageView = new ImageView();
        rightImageView.setFitWidth(430); // Adjust the width of the image view as needed

        rightImageView.setPreserveRatio(true);

        // Load images
        Image leftImage = new Image("pistesoleil.jpg"); // Provide the path to your image file
        Image rightImage = new Image("avionpiste.png"); // Provide the path to your image file
        leftImageView.setImage(leftImage);
        rightImageView.setImage(rightImage);

        // Create a pane for the image views
        StackPane leftImagePane = new StackPane(leftImageView);
        StackPane rightImagePane = new StackPane(rightImageView);

        // Create a list view for the conversation
        observableList = FXCollections.observableArrayList();
        ListView<String> conversationList = new ListView<>(observableList);


        // Create main layout
        BorderPane mainPane = new BorderPane();
        mainPane.setLeft(leftImageView);
        mainPane.setCenter(formPane);
        mainPane.setRight(rightImageView);
        mainPane.setBottom(conversationList);

        // Set action for decollage button
        decollageButton.setOnAction(e -> {
            // Handle decollage action here
            System.out.println("Décollage button clicked");
        });

        // Set action for atterrissage button
        atterrissageButton.setOnAction(e -> {
            // Handle atterrissage action here
            System.out.println("Atterrissage button clicked");
        });

        // Create and show the scene
        Scene scene = new Scene(mainPane, 800, 600); // Adjust the scene width and height as needed
        primaryStage.setScene(scene);
        primaryStage.setTitle("Avion");
        primaryStage.show();

        // Button Styling
        decollageButton.setStyle("-fx-background-color: #E67E30; -fx-text-fill: white; -fx-font-size: 14px;");
        atterrissageButton.setStyle("-fx-background-color: #E67E30; -fx-text-fill: white; -fx-font-size: 14px;");
        DropShadow shadow = new DropShadow();
        decollageButton.setOnMouseEntered(e -> {
            decollageButton.setCursor(Cursor.HAND);
            decollageButton.setEffect(shadow);
        });

        atterrissageButton.setOnMouseEntered(e -> {
            atterrissageButton.setCursor(Cursor.HAND);
            atterrissageButton.setEffect(shadow);
        });

        decollageButton.setOnMouseExited(e -> {
            decollageButton.setCursor(Cursor.DEFAULT);
            decollageButton.setEffect(null);
        });

        atterrissageButton.setOnMouseExited(e -> {
            atterrissageButton.setCursor(Cursor.DEFAULT);
            atterrissageButton.setEffect(null);
        });

        // Adjusting Padding
        formPane.setPadding(new Insets(50));
        mainPane.setPadding(new Insets(20));

        // Font Styling
        selectLabel1.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        selectLabel2.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        // Color Scheme
        mainPane.setStyle("-fx-background-color: #f2f2f2;");
        formPane.setStyle("-fx-background-color: white; -fx-border-color: #cccccc;");

        // Populate select1 with compagnie values from database

        // Populate select1 with compagnie values from database
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT DISTINCT compagnie FROM allocation");
            ResultSet resultSet = preparedStatement.executeQuery();
            ObservableList<String> compagnies = FXCollections.observableArrayList();
            while (resultSet.next()) {
                compagnies.add(resultSet.getString("compagnie"));
            }
            select1.setItems(compagnies);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Handle select1 selection event to fetch avion options
        select1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    String selectedCompagnie = newValue;
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT avion FROM allocation WHERE compagnie = ?");
                    preparedStatement.setString(1, selectedCompagnie);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    ObservableList<String> avions = FXCollections.observableArrayList();
                    while (resultSet.next()) {
                        avions.add(resultSet.getString("avion"));
                    }
                    select2.setItems(avions);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else {        // If nothing is selected in select1, disable both buttons
                decollageButton.setDisable(true);
                atterrissageButton.setDisable(true);}
        });
// Handle select2 selection event to enable/disable buttons based on 'arrivé' status
        select2.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    String selectedAvion = newValue;
                    String selectedCompagnie = select1.getValue();
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT arrivé FROM allocation WHERE compagnie = ? AND avion = ?");
                    preparedStatement.setString(1, selectedCompagnie);
                    preparedStatement.setString(2, selectedAvion);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        String arriveStatus = resultSet.getString("arrivé");
                        if (arriveStatus.equals("Oui")) {
                            decollageButton.setDisable(false);
                            atterrissageButton.setDisable(true);
                        } else if (arriveStatus.equals("Non")) {
                            decollageButton.setDisable(true);
                            atterrissageButton.setDisable(false);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else {        // If nothing is selected in select1, disable both buttons
                decollageButton.setDisable(true);
                atterrissageButton.setDisable(true);}
        });
        atterrissageButton.setOnAction(e -> {
            String selectedAvion = select2.getValue();
            String selectedCompagnie = select1.getValue();
            if (selectedAvion != null && selectedCompagnie != null) {
                try {
                    // Find the next available piste number
                    int nextPisteNumber = 1;
                    boolean pisteFound = false;
                    while (nextPisteNumber <= 60) {
                        // Check if the piste number is already taken
                        PreparedStatement checkStatement = connection.prepareStatement("SELECT * FROM allocation WHERE piste = ? AND arrivé = 'Oui'");
                        checkStatement.setInt(1, nextPisteNumber);
                        ResultSet resultSet = checkStatement.executeQuery();
                        if (!resultSet.next()) {
                            // If the piste number is available, assign it to the compagnie and avion
                            PreparedStatement updateStatement = connection.prepareStatement("UPDATE allocation SET arrivé = 'Oui', piste = ? WHERE compagnie = ? AND avion = ?");
                            updateStatement.setInt(1, nextPisteNumber);
                            updateStatement.setString(2, selectedCompagnie);
                            updateStatement.setString(3, selectedAvion);
                            updateStatement.executeUpdate();
                            pisteFound = true;
                            break;
                        }
                        nextPisteNumber++;
                    }

                    if (pisteFound) {
                        // Disable atterrissageButton and enable decollageButton
                        atterrissageButton.setDisable(true);
                        decollageButton.setDisable(false);
                        String Message = String.format("Pilote : Ici %s  de %s , je vais attérir veuillez m'accorder une piste !", selectedAvion, selectedCompagnie);

                        // observableList.add(livre);
                        GuiEvent event =new GuiEvent(this,1);
                        event.addParameter(Message);
                        event.addParameter(nextPisteNumber);
                        event.addParameter(selectedCompagnie);
                        event.addParameter(selectedAvion);
                        avionAgent.onGuiEvent(event);
                    } else {
                        // Handle case where no available piste was found (could prompt a message to the user)
                        System.out.println("All pistes are currently occupied.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        // Set action for decollage button
        decollageButton.setOnAction(e -> {
            String selectedAvion = select2.getValue();
            String selectedCompagnie = select1.getValue();
            if (selectedAvion != null && selectedCompagnie != null) {
                try {
                    // Delete row from the database
                    PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM allocation WHERE compagnie = ? AND avion = ?");
                    deleteStatement.setString(1, selectedCompagnie);
                    deleteStatement.setString(2, selectedAvion);
                    deleteStatement.executeUpdate();
                    String Message = String.format("Pilote : Ici %s  de %s , je vais Décoller veuillez me confirmer ma piste !", selectedAvion, selectedCompagnie);

                    // observableList.add(livre);
                    GuiEvent event =new GuiEvent(this,2);
                    event.addParameter(Message);
                    event.addParameter(selectedCompagnie);
                    event.addParameter(selectedAvion);

                    avionAgent.onGuiEvent(event);

                    // Clear select boxes and refresh UI

                    select1.setValue(null);
                    select2.setValue(null);
                    refreshUI(select1,select2);

                    // Enable atterrissageButton and disable decollageButton
                    atterrissageButton.setDisable(true);
                    decollageButton.setDisable(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        // Set up a Timeline to refresh UI every 5 seconds
        // Create a ScheduledExecutorService to refresh UI every 5 seconds
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            refreshUI(select1,select2); // Load fresh data
        }));
        timeline.setCycleCount(Timeline.INDEFINITE); // Repeat indefinitely
        timeline.play();

    }



    private void refreshUI(ChoiceBox<String> select1,ChoiceBox<String> select2) {

        try {

            // Preserve selected values

            String selectedCompagnie = select1.getValue();
            String selectedAvion = select2.getValue();

            // Refresh select1 with compagnie values
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT DISTINCT compagnie FROM allocation");
            ResultSet resultSet = preparedStatement.executeQuery();
            ObservableList<String> compagnies = FXCollections.observableArrayList();
            while (resultSet.next()) {
                compagnies.add(resultSet.getString("compagnie"));
            }
            // Check if there are any remaining Avions for each compagnie
            for (String compagnie : compagnies) {
                preparedStatement = connection.prepareStatement("SELECT COUNT(*) as count FROM allocation WHERE compagnie = ?");
                preparedStatement.setString(1, compagnie);
                resultSet = preparedStatement.executeQuery();
                resultSet.next();
                int count = resultSet.getInt("count");
                if (count == 0) {
                    // Remove compagnie from select options if no Avions are left
                    compagnies.remove(compagnie);
                }
            }
            select1.setItems(compagnies);

            // Set selected value for select1 if still present
            if (selectedCompagnie != null && compagnies.contains(selectedCompagnie)) {
                select1.setValue(selectedCompagnie);
            }

            // Refresh select2 with avion values based on selected compagnie
            if (selectedCompagnie != null) {
                preparedStatement = connection.prepareStatement("SELECT avion FROM allocation WHERE compagnie = ?");
                preparedStatement.setString(1, selectedCompagnie);
                resultSet = preparedStatement.executeQuery();
                ObservableList<String> avions = FXCollections.observableArrayList();
                while (resultSet.next()) {
                    avions.add(resultSet.getString("avion"));
                }
                select2.setItems(avions);

                // Set selected value for select2 if still present
                if (selectedAvion != null && avions.contains(selectedAvion)) {
                    select2.setValue(selectedAvion);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }



   public void StartContainer() throws StaleProxyException {

        Runtime runtime=Runtime.instance();
        ProfileImpl profileImpl =new ProfileImpl();
        profileImpl.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        // creation d'un container
        AgentContainer container = runtime.createAgentContainer(profileImpl);
        //creation d'un agent (deploiement de l'agent dans le container)
        AgentController agentController= container
                .createNewAgent("Avion","Avion.AvionAgent",new Object[]{this});
        agentController.start();
    }

    public void setAvionAgent(AvionAgent avionAgent) {
        this.avionAgent = avionAgent;
    }


    public void logMessage(String aclMessage) {
        Platform.runLater(()->{
            observableList.add(aclMessage);
        });
    }
}
