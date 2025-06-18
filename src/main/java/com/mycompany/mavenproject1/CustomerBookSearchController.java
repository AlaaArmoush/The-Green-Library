package com.mycompany.mavenproject1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javax.swing.JOptionPane;

public class CustomerBookSearchController {

    @FXML
    private TextField searchBar;

    @FXML
    private TableView<Book> bookTable;

    @FXML
    private TableColumn<Book, Integer> bookidColumn;

    @FXML
    private TableColumn<Book, String> booknameColumn;

    @FXML
    private TableColumn<Book, String> authornameColumn;

    @FXML
    private TableColumn<Book, String> publishernameColumn;

    @FXML
    private ComboBox<String> bookCombo;

    private ObservableList<Book> bookData = FXCollections.observableArrayList();
    private ObservableList<String> searchOptions = FXCollections.observableArrayList("None", "Book Name", "Book ID", "Author Name", "Publisher Name");

    @FXML
    void backToProfile(ActionEvent event) throws IOException {
        App.setRoot("CustomerDashboard");
    }

    @FXML
    void borrowAction(ActionEvent event) {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();

        if (selectedBook != null) {
            int bookID = selectedBook.getBookID();
            try {
                String url = "jdbc:postgresql://localhost:5432/postgres";
                String user = "alaa";
                String password = "2342004";

                try (Connection conn = DriverManager.getConnection(url, user, password)) {
                    String query = "INSERT INTO Borrows (CustID, BookID, StartDate) VALUES (?, ?, current_date)";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, LogInController.loggedInCustID);
                    pstmt.setInt(2, bookID);

                    int affectedRows = pstmt.executeUpdate();

                    if (affectedRows > 0) {
                        JOptionPane.showMessageDialog(null, "Book borrowed successfully!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to borrow the book.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "An error occurred while borrowing the book.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a book to borrow.");
        }
    }

    @FXML
    void purchaseAction(ActionEvent event) {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();

        if (selectedBook != null) {
            int bookID = selectedBook.getBookID();
            try {
                String url = "jdbc:postgresql://localhost:5432/postgres";
                String user = "alaa";
                String password = "2342004";

                try (Connection conn = DriverManager.getConnection(url, user, password)) {
                    String query = "INSERT INTO Purchases (CustID, BookID) VALUES (?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, LogInController.loggedInCustID);
                    pstmt.setInt(2, bookID);

                    int affectedRows = pstmt.executeUpdate();

                    if (affectedRows > 0) {
                        JOptionPane.showMessageDialog(null, "Book purchased successfully!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to purchase the book.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "An error occurred while purchasing the book.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a book to purchase.");
        }
    }

    @FXML
    void initialize() {
        // Populate bookCombo with search options
        bookCombo.setItems(searchOptions);

        bookData.clear();

        try {
            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");

            // Establish the connection
            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "alaa";
            String password = "2342004";

            try (Connection conn = DriverManager.getConnection(url, user, password); Statement stmt = conn.createStatement()) {

                String query = "SELECT b.BookID, b.BookName, a.AuthID, a.AuthFname, a.AuthLname, p.PubID, p.PubName FROM Book b "
                        + "JOIN Author a ON b.AuthID = a.AuthID "
                        + "JOIN Publisher p ON b.PubID = p.PubID "
                        + "WHERE b.bookavailable = 'true' ";

                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {
                    int bookID = rs.getInt("BookID");
                    String bookName = rs.getString("BookName");
                    int authID = rs.getInt("AuthID");
                    String authFname = rs.getString("AuthFname");
                    String authLname = rs.getString("AuthLname");
                    int pubID = rs.getInt("PubID");
                    String pubName = rs.getString("PubName");

                    Author author = new Author(authID, authFname, authLname);
                    Publisher publisher = new Publisher(pubID, pubName);

                    bookData.add(new Book(bookID, bookName, 0, null, publisher, author));
                }

            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        // Set up TableView columns
        bookidColumn.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        booknameColumn.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        authornameColumn.setCellValueFactory(cellData -> {
            String authorName = cellData.getValue().getAuthor().getAuthFname() + " " + cellData.getValue().getAuthor().getAuthLname();
            return new SimpleStringProperty(authorName);
        });
        publishernameColumn.setCellValueFactory(cellData -> {
            String publisherName = cellData.getValue().getPublisher().getPubName();
            return new SimpleStringProperty(publisherName);
        });

        if (bookTable != null) {
            bookTable.setItems(bookData);
        } else {
            System.out.println("bookTable is null");
        }

        // Bind the onKeyPressed event of searchBar to searchBooks method
        searchBar.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                searchBooks(event);
            }
        });

    }

    @FXML
    void searchBooks(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String selectedOption = bookCombo.getSelectionModel().getSelectedItem();
            String searchText = searchBar.getText().trim();

            ObservableList<Book> filteredList = FXCollections.observableArrayList();

            switch (selectedOption) {
                case "None":
                    filteredList.addAll(bookData);
                    break;
                case "Book Name":
                    filteredList = bookData.filtered(book -> book.getBookName().toLowerCase().contains(searchText.toLowerCase()));
                    break;
                case "Book ID":
                    try {
                        int bookID = Integer.parseInt(searchText);
                        filteredList = bookData.filtered(book -> book.getBookID() == bookID);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Book ID");
                    }
                    break;
                case "Author Name":
                    filteredList = bookData.filtered(book
                            -> (book.getAuthor().getAuthFname() + " " + book.getAuthor().getAuthLname()).toLowerCase().contains(searchText.toLowerCase()));
                    break;
                case "Publisher Name":
                    filteredList = bookData.filtered(book -> book.getPublisher().getPubName().toLowerCase().contains(searchText.toLowerCase()));
                    break;
                default:
                    filteredList.addAll(bookData);
                    break;
            }

            bookTable.setItems(filteredList);
        }
    }

}
