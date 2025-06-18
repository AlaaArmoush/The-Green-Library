package com.mycompany.mavenproject1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javax.swing.JOptionPane;

public class FeedbackController {

    @FXML
    private TableColumn<Feedback, String> Description;

    @FXML
    private ComboBox<String> FeedbackCombo;

    @FXML
    private TableColumn<Feedback, Integer> customerid;

    @FXML
    private TableColumn<Feedback, Integer> ratingoutof10;

    @FXML
    private TextField searchAttribute;

    @FXML
    private TableView<Feedback> table;

    //--------------------METHODS-----------------------------
    @FXML
    void LogOutAction(ActionEvent event) throws IOException {
        App.setRoot("LogIn");
    }

    @FXML
    void EmployeeTable(ActionEvent event) throws IOException {
        App.setRoot("Employee");
    }

    @FXML
    void CustomerTable(ActionEvent event) throws IOException {
        App.setRoot("Customer");
    }

    @FXML
    void BookTable(ActionEvent event) throws IOException {
        App.setRoot("Book");
    }

    @FXML
    void PublisherTable(ActionEvent event) throws IOException {
        App.setRoot("Publisher");
    }

    @FXML
    void AuthorTable(ActionEvent event) throws IOException {
        App.setRoot("Author");
    }

    @FXML
    void BorrowsTable(ActionEvent event) throws IOException {
        App.setRoot("Borrows");
    }

    @FXML
    void PurchasesTable(ActionEvent event) throws IOException {
        App.setRoot("Purchases");
    }

    @FXML
    void FeedbackTable(ActionEvent event) throws IOException {
        App.setRoot("Feedback");
    }

    @FXML
    void searchAction(ActionEvent event) {
        try {
            FeedbackCombo.setItems(FXCollections.observableArrayList("None", "Description", "rating_10", "CustID"));

            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "alaa";
            String password = "2342004";
            Connection conn = DriverManager.getConnection(url, user, password);

            String selectedColumn = FeedbackCombo.getValue();

            String query;
            if ("None".equals(selectedColumn)) {
                query = "SELECT * FROM Feedback";
            } else if ("Description".equals(selectedColumn)) {
                query = "SELECT * FROM Feedback WHERE Description ILIKE ?";
            } else {
                query = "SELECT * FROM Feedback WHERE " + selectedColumn + " = ?";
            }

            java.sql.PreparedStatement stmt = conn.prepareStatement(query);

            if ("Description".equals(selectedColumn)) {
                String searchPattern = "%" + searchAttribute.getText().trim().replaceAll("\\s+", "%") + "%";
                stmt.setString(1, searchPattern);
            } else if (!"None".equals(selectedColumn)) {
                try {
                    stmt.setInt(1, Integer.parseInt(searchAttribute.getText()));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid input for " + selectedColumn + ". Please enter a valid value.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            ResultSet rs = stmt.executeQuery();

            ObservableList<Feedback> feedbackList = FXCollections.observableArrayList();

            while (rs.next()) {
                String description = rs.getString("Description");
                int rating = rs.getInt("rating_10");
                int custID = rs.getInt("CustID");

                Feedback feedback = new Feedback(description, rating, custID);
                feedbackList.add(feedback);
            }

            rs.close();
            stmt.close();
            conn.close();

            this.Description.setCellValueFactory(new PropertyValueFactory<>("description"));
            this.ratingoutof10.setCellValueFactory(new PropertyValueFactory<>("rating_10"));
            this.customerid.setCellValueFactory(new PropertyValueFactory<>("custID"));

            this.table.setItems(feedbackList);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while searching. Please try again later.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @FXML
    public void initialize() {
        try {
            FeedbackCombo.setItems(FXCollections.observableArrayList("None", "Description", "rating_10", "CustID"));

            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "alaa";
            String password = "2342004";
            Connection conn = DriverManager.getConnection(url, user, password);

            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Feedback");
            ResultSet rs = stmt.executeQuery();

            ObservableList<Feedback> feedbackList = FXCollections.observableArrayList();

            while (rs.next()) {
                String description = rs.getString("Description");
                int rating = rs.getInt("rating_10");
                int custID = rs.getInt("CustID");

                Feedback feedback = new Feedback(description, rating, custID);
                feedbackList.add(feedback);
            }

            rs.close();
            stmt.close();
            conn.close();

            Description.setCellValueFactory(new PropertyValueFactory<>("Description"));
            ratingoutof10.setCellValueFactory(new PropertyValueFactory<>("rating_10"));
            customerid.setCellValueFactory(new PropertyValueFactory<>("custID"));

            table.setItems(feedbackList);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while initializing. Please try again later.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
