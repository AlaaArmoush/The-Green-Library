package com.mycompany.mavenproject1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import javax.swing.JOptionPane;

public class AddBorrowController {

    @FXML
    private TextField BookIDTF;

    @FXML
    private TextField CustIDTF;

    @FXML
    private DatePicker StartDateTF;

    @FXML
    private Button addBorrowBTTN;

    @FXML
    private Button cancelAddBorrowBTTN;

    @FXML
    void addBorrow(ActionEvent event) throws IOException {
        int bookID = Integer.parseInt(BookIDTF.getText());
        int custID = Integer.parseInt(CustIDTF.getText());
        Date startDate = Date.valueOf(StartDateTF.getValue());

        // Database connection details
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "alaa";
        String password = "2342004";

        // SQL statement to insert a new borrow
        String sql = "INSERT INTO Borrows (BookID, CustID, StartDate) VALUES (?, ?, ?)";
        //the question marks are place holders

        try (Connection conn = DriverManager.getConnection(url, user, password); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookID);
            pstmt.setInt(2, custID);
            pstmt.setDate(3, startDate);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Borrow added successfully!");

                // Close the window after adding the borrow
                Stage stage = (Stage) addBorrowBTTN.getScene().getWindow();
                stage.close();

                // Refresh the TableView in the BorrowController
                App.setRoot("Borrows");

            } else {
                System.out.println("Failed to add borrow!");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding borrow: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input format. Please enter valid integers for BookID and CustID.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    @FXML
    void cancelAddBorrow(ActionEvent event) {
        Stage stage = (Stage) cancelAddBorrowBTTN.getScene().getWindow();
        stage.close();
    }
}
