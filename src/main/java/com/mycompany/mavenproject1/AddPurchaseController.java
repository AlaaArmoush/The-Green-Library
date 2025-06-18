package com.mycompany.mavenproject1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import javax.swing.JOptionPane;

public class AddPurchaseController {

    @FXML
    private TextField BookIDTF;

    @FXML
    private TextField CustIDTF;

    @FXML
    private Button addPurchaseBTTN;

    @FXML
    private Button cancelAddPurchaseBTTN;

    @FXML
    void addPurchase(ActionEvent event) throws IOException {
        int bookID = Integer.parseInt(BookIDTF.getText());
        int custID = Integer.parseInt(CustIDTF.getText());

        // Database connection details
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "alaa";
        String password = "2342004";

        // SQL statement to insert a new purchase
        String sql = "INSERT INTO Purchases (BookID, CustID) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookID);
            pstmt.setInt(2, custID);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Purchase added successfully!");

                // Close the window after adding the purchase
                Stage stage = (Stage) addPurchaseBTTN.getScene().getWindow();
                stage.close();

                // Refresh the TableView in the PurchasesController
                App.setRoot("Purchases");

            } else {
                System.out.println("Failed to add purchase!");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding purchase: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input format. Please enter valid integers for BookID and CustID.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    @FXML
    void cancelAddPurchase(ActionEvent event) {
        Stage stage = (Stage) cancelAddPurchaseBTTN.getScene().getWindow();
        stage.close();
    }
}
