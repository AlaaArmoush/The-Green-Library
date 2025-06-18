package com.mycompany.mavenproject1;

import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.sql.ResultSet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

public class BookController {

    @FXML
    private TableColumn<Book, Integer> id;

    @FXML
    private TableColumn<Book, String> title; //bookname

    @FXML
    private TableColumn<Book, Boolean> available;

    @FXML
    private TableColumn<Book, Author> author;

    @FXML
    private TableColumn<Book, Publisher> publisher;

    @FXML
    private TableColumn<Book, Integer> count;

    @FXML
    private TableColumn<Book, Employee> employee;

    @FXML
    private TableView<Book> table;

    @FXML
    private ComboBox<String> bookCombo;

    @FXML
    private TextField searchAttribute;

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
            bookCombo.setItems(FXCollections.observableArrayList("None", "BookId", "BookName", "BookAvailable", "EmpId",
                    "PubId", "AuthId", "BookCount"));

            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");

            // Establish the connection
            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "alaa";
            String password = "2342004";
            Connection conn = DriverManager.getConnection(url, user, password);
            String query;

            if ("None".equals(bookCombo.getValue())) {
                query = "SELECT b.*, e.*, p.*, a.* FROM Book b "
                        + "LEFT JOIN Employee e ON b.EmpID = e.EmpID "
                        + "LEFT JOIN Publisher p ON b.PubID = p.PubID "
                        + "LEFT JOIN Author a ON b.AuthID = a.AuthID ";
            } else if ("BookName".equals(bookCombo.getValue())) {
                query = "SELECT b.*, e.*, p.*, a.* FROM Book b "
                        + "LEFT JOIN Employee e ON b.EmpID = e.EmpID "
                        + "LEFT JOIN Publisher p ON b.PubID = p.PubID "
                        + "LEFT JOIN Author a ON b.AuthID = a.AuthID "
                        + "WHERE b.BookName ILIKE ?";
            } else if ("BookAvailable".equals(bookCombo.getValue())) {
                query = "SELECT b.*, e.*, p.*, a.* FROM Book b "
                        + "LEFT JOIN Employee e ON b.EmpID = e.EmpID "
                        + "LEFT JOIN Publisher p ON b.PubID = p.PubID "
                        + "LEFT JOIN Author a ON b.AuthID = a.AuthID "
                        + "WHERE b.BookAvailable = ?";
            } else {
                query = "SELECT b.*, e.*, p.*, a.* FROM Book b "
                        + "LEFT JOIN Employee e ON b.EmpID = e.EmpID "
                        + "LEFT JOIN Publisher p ON b.PubID = p.PubID "
                        + "LEFT JOIN Author a ON b.AuthID = a.AuthID "
                        + "WHERE b." + bookCombo.getValue() + " = ?";
            }

            // Create a PreparedStatement
            PreparedStatement stmt = conn.prepareStatement(query);

            if (!"None".equals(bookCombo.getValue())) {
                try {
                    switch (bookCombo.getValue()) {
                        case "BookName":
                            stmt.setString(1, "%" + searchAttribute.getText() + "%"); // Use "%" for pattern matching
                            break;
                        case "BookAvailable":
                            stmt.setBoolean(1, Boolean.parseBoolean(searchAttribute.getText()));
                            break;
                        default:
                            stmt.setInt(1, Integer.parseInt(searchAttribute.getText()));
                            break;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid input for " + bookCombo.getValue() + ". Please enter a valid value.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            ResultSet rs = stmt.executeQuery();

            // Create an ObservableList to store Book objects
            ObservableList<Book> bookList = FXCollections.observableArrayList();

            // Iterate through the ResultSet and populate the ObservableList
            while (rs.next()) {
                // Retrieve book data
                int bookID = rs.getInt("BookID");
                String bookName = rs.getString("BookName");
                boolean bookAvailable = rs.getBoolean("bookavailable");
                int bookCount = rs.getInt("BookCount");

                // Retrieve employee data
                Employee employee = new Employee();
                employee.setEmpID(rs.getInt("EmpID"));
                employee.setEmpFname(rs.getString("EmpFname"));
                employee.setEmpLname(rs.getString("EmpLname"));

                // Retrieve publisher data
                Publisher publisher = new Publisher();
                publisher.setPubID(rs.getInt("PubID"));
                publisher.setPubName(rs.getString("PubName"));
                publisher.setPubCountry(rs.getString("PubCountry"));

                // Retrieve author data
                Author author = new Author();
                author.setAuthID(rs.getInt("AuthID"));
                author.setAuthFname(rs.getString("AuthFname"));
                author.setAuthLname(rs.getString("AuthLname"));

                // Create a new Book object with associated entities and add it to the list
                Book book = new Book(bookID, bookName, bookAvailable, bookCount, employee, publisher, author);
                bookList.add(book);
            }

            // Close the ResultSet, statement, and connection
            rs.close();
            stmt.close();
            conn.close();

            // Set the items to the table
            table.setItems(bookList);

            // Bind columns to model properties
            id.setCellValueFactory(new PropertyValueFactory<>("bookID"));
            title.setCellValueFactory(new PropertyValueFactory<>("bookName"));
            available.setCellValueFactory(new PropertyValueFactory<>("bookAvailable"));
            count.setCellValueFactory(new PropertyValueFactory<>("bookCount"));
            employee.setCellValueFactory(new PropertyValueFactory<>("employee"));
            publisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
            author.setCellValueFactory(new PropertyValueFactory<>("author"));

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
        }
    }

    @FXML
    void addBookWindowCall(ActionEvent event) throws IOException {
        // Load AddBook.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AddBook.fxml"));
        Parent addBookRoot = loader.load();

        //set a new stage
        Stage addBookStage = new Stage();
        addBookStage.setScene(new Scene(addBookRoot));
        addBookStage.show();
    }

    @FXML
    void removeBook(ActionEvent event) {
        Book selectedBook = table.getSelectionModel().getSelectedItem();

        if (selectedBook != null) {
            int bookid = selectedBook.getBookID(); // Retrieve bookID from selected book

            try {
                // Load the PostgreSQL JDBC driver
                Class.forName("org.postgresql.Driver");

                // Establish the connection
                String url = "jdbc:postgresql://localhost:5432/postgres";
                String user = "alaa";
                String password = "2342004";
                Connection conn = DriverManager.getConnection(url, user, password);

                // Create a statement
                Statement stmt = conn.createStatement();

                // Execute the delete query
                int rowsAffected = stmt.executeUpdate("DELETE FROM book WHERE bookid = " + bookid);

                // Check if any rows were deleted
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Row with book id " + bookid + " deleted successfully.");
                    initialize();
                } else {
                    JOptionPane.showMessageDialog(null, "No rows found with book id " + bookid + ".");
                }

                // Close statement and connection
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "An error occurred while deleting the row. Please try again later.", "Database Error", JOptionPane.ERROR_MESSAGE);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Database driver not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a book from the table.");
        }
    }

    @FXML
    public void initialize() {
        try {
            searchAttribute.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    searchAction(new ActionEvent()); // Call the searchAction method when Enter key is pressed
                }
            });
            //set teh combo list items
            bookCombo.setItems(FXCollections.observableArrayList("None", "BookId", "BookName", "BookAvailable", "EmpId",
                    "PubId", "AuthId", "BookCount"));

            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");

            // Establish the connection
            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "alaa";
            String password = "2342004";
            Connection conn = DriverManager.getConnection(url, user, password);

            // Create a statement
            Statement stmt = conn.createStatement();

            // Execute the query to retrieve book data with associated entities
            // Execute the query to retrieve book data with associated entities
            ResultSet rs = stmt.executeQuery("SELECT b.*, e.*, p.*, a.* FROM Book b "
                    + "LEFT JOIN Employee e ON b.EmpID = e.EmpID "
                    + "LEFT JOIN Publisher p ON b.PubID = p.PubID "
                    + "LEFT JOIN Author a ON b.AuthID = a.AuthID"
            );

            // Create an ObservableList to store Book objects
            ObservableList<Book> bookList = FXCollections.observableArrayList();

            // Iterate through the ResultSet and populate the ObservableList
            while (rs.next()) {
                // Retrieve book data
                int bookID = rs.getInt("BookID");
                String bookName = rs.getString("BookName");
                boolean bookAvailable = rs.getBoolean("bookavailable");
                int bookCount = rs.getInt("BookCount");

                // Retrieve employee data
                Employee employee = new Employee();
                employee.setEmpID(rs.getInt("EmpID")); // Use the correct column name
                employee.setEmpFname(rs.getString("EmpFname"));
                employee.setEmpLname(rs.getString("EmpLname"));
                // Similarly, retrieve and set other employee fields

                // Retrieve publisher data
                Publisher publisher = new Publisher();
                publisher.setPubID(rs.getInt("PubID")); // Use the correct column name
                publisher.setPubName(rs.getString("PubName"));
                publisher.setPubCountry(rs.getString("PubCountry"));
                // Similarly, retrieve and set other publisher fields

                // Retrieve author data
                Author author = new Author();
                author.setAuthID(rs.getInt("AuthID")); // Use the correct column name
                author.setAuthFname(rs.getString("AuthFname"));
                author.setAuthLname(rs.getString("AuthLname"));
                // Similarly, retrieve and set other author fields

                // Create a new Book object with associated entities and add it to the list
                Book book = new Book(bookID, bookName, bookAvailable, bookCount, employee, publisher, author);
                bookList.add(book);
            }

            // Close the ResultSet, statement, and connection
            rs.close();
            stmt.close();
            conn.close();

            // Set the items to the table
            table.setItems(bookList);

            // Bind columns to model properties
            id.setCellValueFactory(new PropertyValueFactory<>("bookID"));
            title.setCellValueFactory(new PropertyValueFactory<>("bookName"));
            available.setCellValueFactory(new PropertyValueFactory<>("bookAvailable"));
            count.setCellValueFactory(new PropertyValueFactory<>("bookCount"));
            employee.setCellValueFactory(new PropertyValueFactory<>("employee"));
            publisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
            author.setCellValueFactory(new PropertyValueFactory<>("author"));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
        }
    }
}
