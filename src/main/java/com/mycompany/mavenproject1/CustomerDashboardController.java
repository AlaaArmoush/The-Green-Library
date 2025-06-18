package com.mycompany.mavenproject1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class CustomerDashboardController {

    @FXML
    private TextField searchnameTF;

    @FXML
    private TableColumn<String, CustomerBook> purchasedbookname;

    @FXML
    private TableColumn<String, CustomerBook> purchasedbookauthor;

    @FXML
    private TableView<CustomerBook> purchasedtable;

    @FXML
    private TableColumn<String, CustomerBook> borrowedbookname;

    @FXML
    private TableColumn<String, CustomerBook> borrowedbookenddate;

    @FXML
    private TableView<CustomerBook> borrowedtable;

    @FXML
    private Label customercontact;

    @FXML
    private Label firstandlastname;

    @FXML
    private Label firstname;

    @FXML
    private Label id;

    @FXML
    private TextField searchTextField;

    @FXML
    private TableColumn<String, Feedback> feedbackdescriptioncolumn;

    @FXML
    private TableColumn<Integer, Feedback> feedbackratingcolumn;

    @FXML
    private TableColumn<String, Feedback> feedbackusernamecolumn;

    @FXML
    private TableView<Feedback> feedbacktable;

    private ObservableList<Feedback> feedbackData = FXCollections.observableArrayList();

    //i propably could have just made a new constructor instead of creating the CustomerBook.java now that i think about it
    private ObservableList<CustomerBook> purchasedBooksData = FXCollections.observableArrayList();
    private ObservableList<CustomerBook> borrowedBooksData = FXCollections.observableArrayList();

    @FXML
    private void LogOutAction(ActionEvent event) throws IOException {
        App.setRoot("LogIn");
    }

    @FXML
    private void callWindowFeedback(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomerFeedback.fxml"));
        Parent addFeedbackRoot = loader.load();
        Stage addFeedbackStage = new Stage();
        addFeedbackStage.setScene(new Scene(addFeedbackRoot));
        addFeedbackStage.show();
    }

    @FXML
    private void initialize() {

        // Add an event handler to the TextField
        searchTextField.setOnMouseClicked(event -> {
            try {
                App.setRoot("CustomerBookSearch");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //same for the search borrowed and purchased
        searchnameTF.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                String searchText = searchnameTF.getText();

                searchPurchasedBooks(searchText);
                searchBorrowedBooks(searchText);
            }
        });

        // Set customer details
        try {
            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");

            // Establish the connection
            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "alaa";
            String password = "2342004";

            try (Connection conn = DriverManager.getConnection(url, user, password); Statement stmt = conn.createStatement()) {

                // Fetch customer details
                String queryCustomer = "SELECT CustFname, CustLname, CustContact FROM Customer WHERE CustID = " + LogInController.loggedInCustID;
                ResultSet rsCustomer = stmt.executeQuery(queryCustomer);

                if (rsCustomer.next()) {
                    String firstName = rsCustomer.getString("CustFname");
                    String lastName = rsCustomer.getString("CustLname");
                    String fullName = firstName + " " + lastName;
                    String contact = rsCustomer.getString("CustContact");

                    firstname.setText(firstName);
                    firstandlastname.setText(fullName);
                    customercontact.setText(contact);
                    id.setText(String.valueOf(LogInController.loggedInCustID));
                }

                // Load purchased books
                purchasedBooksData.clear();
                String queryPurchasedBooks = "SELECT b.BookName, a.AuthFname, a.AuthLname FROM Book b "
                        + "JOIN Author a ON b.AuthID = a.AuthID "
                        + "JOIN Purchases p ON b.BookID = p.BookID "
                        + "WHERE p.CustID = " + LogInController.loggedInCustID;
                ResultSet rsPurchased = stmt.executeQuery(queryPurchasedBooks);

                while (rsPurchased.next()) {
                    String bookName = rsPurchased.getString("BookName");
                    String authorFirstName = rsPurchased.getString("AuthFname");
                    String authorLastName = rsPurchased.getString("AuthLname");
                    purchasedBooksData.add(new CustomerBook(bookName, authorFirstName + " " + authorLastName, ""));
                }

                purchasedbookname.setCellValueFactory(new PropertyValueFactory<>("name"));
                purchasedbookauthor.setCellValueFactory(new PropertyValueFactory<>("author"));
                purchasedtable.setItems(purchasedBooksData);

                // Load borrowed books
                borrowedBooksData.clear();
                String queryBorrowedBooks = "SELECT b.BookName, bb.EndDate FROM Book b "
                        + "JOIN Borrows bb ON b.BookID = bb.BookID "
                        + "WHERE bb.CustID = " + LogInController.loggedInCustID;
                ResultSet rsBorrowed = stmt.executeQuery(queryBorrowedBooks);

                while (rsBorrowed.next()) {
                    String bookName = rsBorrowed.getString("BookName");
                    String endDate = rsBorrowed.getString("EndDate");
                    borrowedBooksData.add(new CustomerBook(bookName, "", endDate));
                }

                borrowedbookname.setCellValueFactory(new PropertyValueFactory<>("name"));
                borrowedbookenddate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
                borrowedtable.setItems(borrowedBooksData);

                // Load feedback
                feedbackData.clear();
                String queryFeedback = "SELECT f.Description, f.rating_10, c.CustFname, c.CustLname "
                        + "FROM Feedback f "
                        + "JOIN Customer c ON f.CustID = c.CustID";
                ResultSet rsFeedback = stmt.executeQuery(queryFeedback);

                while (rsFeedback.next()) {
                    String description = rsFeedback.getString("Description");
                    int rating = rsFeedback.getInt("rating_10");
                    String userName = rsFeedback.getString("CustFname") + " " + rsFeedback.getString("CustLname");
                    feedbackData.add(new Feedback(description, rating, userName)); // Using the new constructor
                }

                // some debugging i did ignore it
                for (Feedback feedback : feedbackData) {
                    System.out.println("Description: " + feedback.getDescription() + ", Rating: " + feedback.getRating_10() + ", Username: " + feedback.getUserName());
                }

                feedbackdescriptioncolumn.setCellValueFactory(new PropertyValueFactory<>("description"));
                feedbackratingcolumn.setCellValueFactory(new PropertyValueFactory<>("rating_10"));
                feedbackusernamecolumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
                feedbacktable.setItems(feedbackData);

            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    //----------search methods----------
    private void searchPurchasedBooks(String searchText) {
        purchasedBooksData.clear();

        try {
            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");

            // Establish the connection
            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "alaa";
            String password = "2342004";

            try (Connection conn = DriverManager.getConnection(url, user, password); Statement stmt = conn.createStatement()) {

                String query = "SELECT b.BookName, a.AuthFname, a.AuthLname FROM Book b "
                        + "JOIN Author a ON b.AuthID = a.AuthID "
                        + "JOIN Purchases p ON b.BookID = p.BookID "
                        + "WHERE p.CustID = " + LogInController.loggedInCustID + " "
                        + "AND b.BookName LIKE '%" + searchText + "%'";

                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {
                    String bookName = rs.getString("BookName");
                    String authorFirstName = rs.getString("AuthFname");
                    String authorLastName = rs.getString("AuthLname");
                    purchasedBooksData.add(new CustomerBook(bookName, authorFirstName + " " + authorLastName, ""));
                }

                purchasedbookname.setCellValueFactory(new PropertyValueFactory<>("name"));
                purchasedbookauthor.setCellValueFactory(new PropertyValueFactory<>("author"));
                purchasedtable.setItems(purchasedBooksData);

            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchBorrowedBooks(String searchText) {
        borrowedBooksData.clear();

        try {
            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");

            // Establish the connection
            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "alaa";
            String password = "2342004";

            try (Connection conn = DriverManager.getConnection(url, user, password); Statement stmt = conn.createStatement()) {

                String query = "SELECT b.BookName, bb.EndDate FROM Book b "
                        + "JOIN Borrows bb ON b.BookID = bb.BookID "
                        + "WHERE bb.CustID = " + LogInController.loggedInCustID + " "
                        + "AND b.BookName LIKE '%" + searchText + "%'";

                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {
                    String bookName = rs.getString("BookName");
                    String endDate = rs.getString("EndDate");
                    borrowedBooksData.add(new CustomerBook(bookName, "", endDate));
                }

                borrowedbookname.setCellValueFactory(new PropertyValueFactory<>("name"));
                borrowedbookenddate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
                borrowedtable.setItems(borrowedBooksData);

            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
