package com.mycompany.mavenproject1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class AddPublisherController {

    @FXML
    private TextField PubIDTF;

    @FXML
    private TextField PubNameTF;

    @FXML
    private TextField PubCountryTF;

    @FXML
    private Button addPublisherBTTN;

    @FXML
    private Button cancelAddPublisherBTTN;

    @FXML
    void addPublisher(ActionEvent event) throws IOException {
        int pubID = Integer.parseInt(PubIDTF.getText());
        String pubName = PubNameTF.getText();
        String pubCountry = PubCountryTF.getText();

        // Database connection details
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "alaa";
        String password = "2342004";

        // SQL statement to insert a new publisher
        String sql = "INSERT INTO Publisher (PubID, PubName, PubCountry) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pubID);
            pstmt.setString(2, pubName);
            pstmt.setString(3, pubCountry);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Publisher added successfully!");

                // Close the window after adding the publisher
                Stage stage = (Stage) addPublisherBTTN.getScene().getWindow();
                stage.close();

                // Refresh the TableView in the PublisherController
                App.setRoot("Publisher");

            } else {
                System.out.println("Failed to add publisher!");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding publisher: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input format. Please enter a valid integer for PubID.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    @FXML
    void cancelAddPublisher(ActionEvent event) {
        Stage stage = (Stage) cancelAddPublisherBTTN.getScene().getWindow();
        stage.close();
    }
}
