package com.mycompany.mavenproject1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

public class LogInController {

    @FXML
    private Button loginB;

    @FXML
    private TextField passTF;

    @FXML
    private TextField userTF;

    public static int loggedInEmpID;
    public static int loggedInCustID;

    @FXML
    void LogInButtonAction(ActionEvent event) throws IOException {
        try {
            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            // Handle the exception
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database driver not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Stop further execution if driver is not found
        }

        // Establish the connection
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "alaa";
        String password = "2342004";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // Create a statement
            try (Statement stmt = conn.createStatement()) {
                // Query the Employee table to check if the entered credentials are valid
                String empQuery = "SELECT COUNT(*) FROM Employee WHERE empID = " + userTF.getText() + " AND password = '" + passTF.getText() + "'";
                try (ResultSet empRs = stmt.executeQuery(empQuery)) {
                    if (empRs.next() && empRs.getInt(1) > 0) {
                        loggedInEmpID = Integer.parseInt(userTF.getText());
                        // Navigate to the AdminDashboard
                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        stage.setMaximized(true);  // Set the stage to maximized
                        App.setRoot("Book");
                        return;  // Exit the method after successful login
                    }
                }

                // Query the Customer table to check if the entered credentials are valid
                String custQuery = "SELECT COUNT(*) FROM Customer WHERE custID = " + userTF.getText() + " AND password = '" + passTF.getText() + "'";
                try (ResultSet custRs = stmt.executeQuery(custQuery)) {
                    if (custRs.next() && custRs.getInt(1) > 0) {
                        loggedInCustID = Integer.parseInt(userTF.getText());
                        // Navigate to the CustomerDashboard
                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        stage.setMaximized(true);  // Set the stage to maximized
                        App.setRoot("CustomerDashboard");
                        return;  // Exit the method after successful login
                    }
                }

                // If no result is returned, it means the credentials are invalid
                // Display an error message to the user
                JOptionPane.showMessageDialog(null, "Invalid credentials. Please try again.", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            // Handle parsing errors for user ID
            JOptionPane.showMessageDialog(null, "Invalid user ID format. Please enter a valid integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            // Handle any SQL errors
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while connecting to the database. Please try again later.", "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            // Handle any other exceptions
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An unexpected error occurred. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
