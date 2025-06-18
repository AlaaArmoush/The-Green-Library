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

public class AddCustomerController {

    @FXML
    private TextField CustIDTF;

    @FXML
    private TextField CustFnameTF;

    @FXML
    private TextField CustLnameTF;

    @FXML
    private TextField LibraryCardTF;

    @FXML
    private TextField CustContactTF;

    @FXML
    private Button addCustomerBTTN;

    @FXML
    private Button cancelAddCustomerBTTN;

    @FXML
    void addCustomer(ActionEvent event) throws IOException {
        int custID = Integer.parseInt(CustIDTF.getText());
        String custFname = CustFnameTF.getText();
        String custLname = CustLnameTF.getText();
        boolean libraryCard = Boolean.parseBoolean(LibraryCardTF.getText());
        String custContact = CustContactTF.getText();

        // Database connection details
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "alaa";
        String password = "2342004";

        // SQL statement to insert a new customer
        String sql = "INSERT INTO Customer (CustID, CustFname, CustLname, LibraryCard, CustContact) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, custID);
            pstmt.setString(2, custFname);
            pstmt.setString(3, custLname);
            pstmt.setBoolean(4, libraryCard);
            pstmt.setString(5, custContact);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Customer added successfully!");

                // Close the window after adding the customer
                Stage stage = (Stage) addCustomerBTTN.getScene().getWindow();
                stage.close();

                // Refresh the TableView in the CustomerController
                App.setRoot("Customer");

            } else {
                System.out.println("Failed to add customer!");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding customer: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input format. Please enter a valid integer for CustID.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    @FXML
    void cancelAddCustomer(ActionEvent event) {
        Stage stage = (Stage) cancelAddCustomerBTTN.getScene().getWindow();
        stage.close();
    }

}
