package com.mycompany.mavenproject1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

public class AddBookController {

    @FXML
    private TextField AuthIDTF;

    @FXML
    private TextField BookAvTF;

    @FXML
    private TextField BookIDTF;

    @FXML
    private TextField BookTitleTF;

    @FXML
    private TextField CountIDTF;

    @FXML
    private TextField PubIDTF;

    @FXML
    private Button addBookBTTN;

    @FXML
    private Button cancelAddBookBTTN;

    @FXML
    void addBook(ActionEvent event) throws IOException {
        int bookID = Integer.parseInt(BookIDTF.getText());
        String bookTitle = BookTitleTF.getText();
        boolean availability = Boolean.parseBoolean(BookAvTF.getText());
        int publisherID = Integer.parseInt(PubIDTF.getText());
        int authorID = Integer.parseInt(AuthIDTF.getText());
        int count = Integer.parseInt(CountIDTF.getText());

        // database connection
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "alaa";
        String password = "2342004";

        // quer to insert a new book
        String sql = "INSERT INTO Book (BookID, BookName, BookAvailable, BookCount, PubID, AuthID, empID) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookID);
            pstmt.setString(2, bookTitle);
            pstmt.setBoolean(3, availability);
            pstmt.setInt(4, count);
            pstmt.setInt(5, publisherID);
            pstmt.setInt(6, authorID);
            pstmt.setInt(7, LogInController.loggedInEmpID);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Book added successfully!");

                // Close the window after adding the book
                Stage stage = (Stage) addBookBTTN.getScene().getWindow();
                stage.close();

                // Refresh the TableView in the BookController
                App.setRoot("Book");

            } else {
                System.out.println("Failed to add book!");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input format. Please enter valid integers for BookID and Count.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    @FXML
    void cancelAddBook(ActionEvent event) {
        Stage stage = (Stage) addBookBTTN.getScene().getWindow();
        stage.close();
    }

}
