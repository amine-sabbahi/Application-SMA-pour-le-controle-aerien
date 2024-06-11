package CompagnieAerienne;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.sql.*;

public class CompagnieUI extends Application {
    protected CompagnieAgent compagnieAgent;

    ObservableList<String> observableList;
    @Override
    public void start(Stage primaryStage) throws Exception {

        StartContainer();
        Image iconstage = new Image("compagnie-aerienne.png");


        // Set the icon for the stage
        primaryStage.getIcons().add(iconstage);
        // Create form components
        TextField nomCompagnieField = new TextField();
        TextField piloteField = new TextField();
        TextField avionField = new TextField();
        DatePicker datePicker = new DatePicker();
        Button confirmButton = new Button("Réserver");
        // Create Spinner for hours
        Spinner<Integer> hourSpinner = new Spinner<>(0, 23, 0);
        hourSpinner.setEditable(true);

        // Create Spinner for minutes
        Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 0);
        minuteSpinner.setEditable(true);
        // Create a form layout
        GridPane formPane = new GridPane();
        formPane.setPadding(new Insets(30));
        formPane.setHgap(30);
        formPane.setVgap(30);
        formPane.setAlignment(Pos.CENTER);
        // Add labels with padding to the left
        Label nomCompagnieLabel = new Label("Nom Compagnie :");
        Label piloteLabel = new Label("Pilote :");
        Label avionLabel = new Label("Avion :");
        Label dateLabel = new Label("Date :");
        Label heureLabel = new Label("Heure :");
        Label minutesLabel = new Label("Minutes :");


        formPane.addRow(0,nomCompagnieLabel, nomCompagnieField);
        formPane.addRow(1,avionLabel, avionField);
        formPane.addRow(2,piloteLabel, piloteField);
        formPane.addRow(3,dateLabel, datePicker);
        formPane.addRow(4,heureLabel, hourSpinner);
        formPane.addRow(5,minutesLabel, minuteSpinner);
        formPane.add(confirmButton, 0, 6, 4, 1);



        // Create an image view for displaying the picture
        ImageView imageView = new ImageView();
        imageView.setFitWidth(828); // Adjust the width of the image view as needed
        imageView.setPreserveRatio(true);

        // Load an example image (replace the path with your image file)
        Image defaultImage = new Image("decollage-avion.jpg"); // Provide the path to your image file
        imageView.setImage(defaultImage);

        // Create a pane for the image view
        StackPane imagePane = new StackPane();
        imagePane.getChildren().add(imageView);
        imagePane.setPadding(new Insets(10));

        // Create a list view for the conversation
        observableList = FXCollections.observableArrayList();

        ListView<String> conversationList = new ListView<>(observableList);
        conversationList.setPrefHeight(200); // Adjust the height as needed


        // Create main layout
        BorderPane mainPane = new BorderPane();
        mainPane.setLeft(formPane);
        mainPane.setRight(imageView);
        mainPane.setBottom(conversationList);


        confirmButton.setOnAction(e -> {
            // Handle form confirmation here
            String nomCompagnie = nomCompagnieField.getText();
            String pilote = piloteField.getText();
            String avion = avionField.getText();
            String date = datePicker.getValue().toString();
            int heure = hourSpinner.getValue();
            int minutes = minuteSpinner.getValue();

            // Check if there is already data with the same date, hour, and minute
            boolean alreadyExists = checkIfExists(date, heure, minutes);

            if (!alreadyExists) {
                // Check if there is at least a 20-minute difference from the latest entry for the same date
                boolean validTimeDifference = checkTimeDifference(date, heure, minutes);

                if (validTimeDifference) {
                    // Save data to the database
                    saveToDatabase(nomCompagnie, pilote, avion, date, heure, minutes);
                    String Message = String.format("%s: Je voudrais réserver une piste le %s à %d:%d s'il vous plaît !", nomCompagnie, date, heure, minutes);

                    // observableList.add(livre);
                    GuiEvent event =new GuiEvent(this,1);
                    event.addParameter(Message);
                    compagnieAgent.onGuiEvent(event);

                } else {
                    System.out.println("There must be at least a 20-minute difference from the latest entry for the same date.");
                    String Message = String.format("%s: Je voudrais réserver une piste le %s à %d:%d s'il vous plaît !", nomCompagnie ,date, heure, minutes);

                    // observableList.add(livre);
                    GuiEvent event =new GuiEvent(this,2);
                    event.addParameter(Message);
                    compagnieAgent.onGuiEvent(event);
                }
            } else {
                System.out.println("Data with the same date, hour, and minute already exists.");
                String Message = String.format("%s: Je voudrais réserver une piste le %s à %d:%d s'il vous plaît !",nomCompagnie, date, heure, minutes);

                // observableList.add(livre);
                GuiEvent event =new GuiEvent(this,2);
                event.addParameter(Message);
                compagnieAgent.onGuiEvent(event);
            }
        });
        // Create and show the scene
        Scene scene = new Scene(mainPane, 800, 600); // Adjust the scene width and height as needed
        primaryStage.setScene(scene);
        primaryStage.setTitle("CompagnieAerienne");
        primaryStage.show();
        // Button Styling
        confirmButton.setStyle("-fx-background-color: #E67E30; -fx-text-fill: white; -fx-font-size: 14px;");
        DropShadow shadow = new DropShadow();
        confirmButton.setOnMouseEntered(e -> {
            confirmButton.setCursor(Cursor.HAND);
            confirmButton.setEffect(shadow);
        });

        confirmButton.setOnMouseExited(e -> {
            confirmButton.setCursor(Cursor.DEFAULT);
            confirmButton.setEffect(null);
        });
// Adjusting Padding
        formPane.setPadding(new Insets(50));
        mainPane.setPadding(new Insets(20));

// Font Styling
        nomCompagnieLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        piloteLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        dateLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        heureLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        minutesLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        avionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");

// Color Scheme
        mainPane.setStyle("-fx-background-color: #f2f2f2;");
        formPane.setStyle("-fx-background-color: white; -fx-border-color: #cccccc;");

    }

    public void StartContainer() throws StaleProxyException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profileImpl = new ProfileImpl();
        profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
        // creation d'un container
        AgentContainer container = runtime.createAgentContainer(profileImpl);
        //creation d'un agent (deploiement de l'agent dans le container)
        AgentController agentController = container.createNewAgent("CompagnieAerienne", "CompagnieAerienne.CompagnieAgent", new Object[]{this});
        agentController.start();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private boolean checkIfExists(String date, int heure, int minutes) {
        try {
            // Establish database connection
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Allocation", "root", "");

            // Create SQL statement
            Statement statement = connection.createStatement();

            // Execute query to check if data with the same date, heure, and minutes exists
            String query = String.format("SELECT COUNT(*) FROM allocation WHERE date = '%s' AND heure = %d AND minute = %d", date, heure, minutes);
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();
            int count = resultSet.getInt(1);

            // Close resources
            resultSet.close();
            statement.close();
            connection.close();

            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void saveToDatabase(String nomCompagnie, String pilote, String avion, String date, int heure, int minutes) {
        try {
            // Establish database connection
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Allocation", "root", "");

            // Create SQL statement
            Statement statement = connection.createStatement();

            // Execute query to insert data into the database
            String query = String.format("INSERT INTO Allocation (compagnie, avion, pilote, date, heure, minute, piste) " +
                            "VALUES ('%s', '%s', '%s', '%s', %d, %d, NULL)",
                    nomCompagnie, avion, pilote, date, heure, minutes);
            int rowsAffected = statement.executeUpdate(query);

            // Close resources
            statement.close();
            connection.close();

            if (rowsAffected > 0) {
                System.out.println("Data saved to the database.");
            } else {
                System.out.println("Failed to save data to the database.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    private boolean checkTimeDifference(String date, int heure, int minutes) {
        try {
            // Establish database connection
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Allocation", "root", "");

            // Create SQL statement to fetch all entries for the same date
            String query = String.format("SELECT heure, minute FROM allocation WHERE date = '%s'", date);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Calculate the total number of minutes for the current entry
            int currentMinutes = heure * 60 + minutes;

            // Iterate through all entries and check for a 20-minute difference
            while (resultSet.next()) {
                int existingHeure = resultSet.getInt("heure");
                int existingMinutes = resultSet.getInt("minute");

                // Calculate the total number of minutes for the existing entry
                int existingTotalMinutes = existingHeure * 60 + existingMinutes;

                // Calculate the time difference in minutes
                int timeDifference = Math.abs(currentMinutes - existingTotalMinutes);

                // Check if the time difference is less than 20 minutes
                if (timeDifference < 20) {
                    return false; // Time difference less than 20 minutes found
                }
            }

            // If no time difference less than 20 minutes is found, return true
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }
    public void setCompagnieAgent(CompagnieAgent compagnieAgent) {
        this.compagnieAgent = compagnieAgent;
    }
    public void logMessage(ACLMessage aclMessage) {
        Platform.runLater(() -> {
            observableList.add(aclMessage.getContent());
        });
    }
}