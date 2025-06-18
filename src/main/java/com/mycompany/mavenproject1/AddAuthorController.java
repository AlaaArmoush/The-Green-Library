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

public class AddAuthorController {

    @FXML
    private TextField AuthIDTF;

    @FXML
    private TextField AuthFnameTF;

    @FXML
    private TextField AuthLnameTF;

    @FXML
    private TextField AuthCountryTF;

    @FXML
    private Button addAuthorBTTN;

    @FXML
    private Button cancelAddAuthorBTTN;

    @FXML
    void addAuthor(ActionEvent event) throws IOException {
        int authID = Integer.parseInt(AuthIDTF.getText());
        String authFname = AuthFnameTF.getText();
        String authLname = AuthLnameTF.getText();
        String authCountry = AuthCountryTF.getText();

        // Database connection details
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "alaa";
        String password = "2342004";

        // SQL statement to insert a new author
        String sql = "INSERT INTO Author (AuthID, AuthFname, AuthLname, AuthCountry) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, authID);
            pstmt.setString(2, authFname);
            pstmt.setString(3, authLname);
            pstmt.setString(4, authCountry);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Author added successfully!");

                // Close the window after adding the author
                Stage stage = (Stage) addAuthorBTTN.getScene().getWindow();
                stage.close();

                // Refresh the TableView in the AuthorController
                App.setRoot("Author");

            } else {
                System.out.println("Failed to add author!");
            }

        } catch (SQLException e) {
            System.out.println("Error adding author: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input format. Please enter valid integers for AuthID.");
        }
    }

    @FXML
    void cancelAddAuthor(ActionEvent event) {
        Stage stage = (Stage) cancelAddAuthorBTTN.getScene().getWindow();
        stage.close();
    }
}
