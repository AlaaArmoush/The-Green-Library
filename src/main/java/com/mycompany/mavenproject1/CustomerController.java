package com.mycompany.mavenproject1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

public class CustomerController {

    @FXML
    private TableColumn<Customer, Integer> id;

    @FXML
    private TableColumn<Customer, String> firstname;

    @FXML
    private TableColumn<Customer, String> lastname;

    @FXML
    private TableColumn<Customer, Boolean> librarycard;

    @FXML
    private TableColumn<Customer, String> contact;

    @FXML
    private TableView<Customer> table;

    @FXML
    private ComboBox<String> customerCombo;

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
    void addCustomerWindowCall(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AddCustomer.fxml"));
        Parent addBookRoot = loader.load();
        Stage addCustomerStage = new Stage();
        addCustomerStage.setScene(new Scene(addBookRoot));
        addCustomerStage.show();
    }

    @FXML
    void removeCustomer(ActionEvent event) {
        try {
            Customer selectedCustomer = table.getSelectionModel().getSelectedItem();

            if (selectedCustomer != null) {
                int custid = selectedCustomer.getCustID();

                Class.forName("org.postgresql.Driver");
                String url = "jdbc:postgresql://localhost:5432/postgres";
                String user = "alaa";
                String password = "2342004";
                Connection conn = DriverManager.getConnection(url, user, password);

                Statement stmt = conn.createStatement();
                int rowsAffected = stmt.executeUpdate("DELETE FROM customer WHERE custid = " + custid);

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Row with customer id " + custid + " deleted successfully.");
                    initialize();
                } else {
                    JOptionPane.showMessageDialog(null, "No rows found with customer id " + custid + ".");
                }

                stmt.close();
                conn.close();
            } else {
                JOptionPane.showMessageDialog(null, "Please select a customer from the table.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid customer id format. Please enter a valid integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while deleting the row. Please try again later.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @FXML
    void searchAction(ActionEvent event) {
        try {
            customerCombo.setItems(FXCollections.observableArrayList("None", "CustID", "CustFname", "CustLname", "LibraryCard", "CustContact"));

            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "alaa";
            String password = "2342004";
            Connection conn = DriverManager.getConnection(url, user, password);

            String selectedColumn = customerCombo.getValue();

            String query;
            if ("None".equals(selectedColumn)) {
                query = "SELECT * FROM Customer";
            } else if ("LibraryCard".equals(selectedColumn)) {
                query = "SELECT * FROM Customer WHERE LibraryCard = ?";
            } else if ("CustID".equals(selectedColumn)) {
                query = "SELECT * FROM Customer WHERE CustID = ?";
            } else {
                query = "SELECT * FROM Customer WHERE " + selectedColumn + " ILIKE ?";
            }

            java.sql.PreparedStatement stmt = conn.prepareStatement(query);

            if (!"None".equals(selectedColumn)) {
                try {
                    if ("CustID".equals(selectedColumn)) {
                        stmt.setInt(1, Integer.parseInt(searchAttribute.getText()));
                    } else if ("LibraryCard".equals(selectedColumn)) {
                        boolean value = Boolean.parseBoolean(searchAttribute.getText());
                        stmt.setBoolean(1, value);
                    } else {
                        stmt.setString(1, "%" + searchAttribute.getText() + "%"); // Use "%" for pattern matching
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid input for " + selectedColumn + ". Please enter a valid value.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            ResultSet rs = stmt.executeQuery();

            ObservableList<Customer> customerList = FXCollections.observableArrayList();

            while (rs.next()) {
                int custid = rs.getInt("CustID");
                String firstname = rs.getString("CustFname");
                String lastname = rs.getString("CustLname");
                boolean librarycard = rs.getBoolean("LibraryCard");
                String contact = rs.getString("CustContact");

                Customer customer = new Customer(custid, firstname, lastname, librarycard, contact);
                customerList.add(customer);
            }

            rs.close();
            stmt.close();
            conn.close();

            table.setItems(customerList);

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
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
            customerCombo.setItems(FXCollections.observableArrayList("None", "CustID", "CustFname", "CustLname", "LibraryCard", "CustContact"));

            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "alaa";
            String password = "2342004";
            Connection conn = DriverManager.getConnection(url, user, password);

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Customer");

            ObservableList<Customer> customerList = FXCollections.observableArrayList();

            while (rs.next()) {
                int custid = rs.getInt("CustID");
                String firstname = rs.getString("CustFname");
                String lastname = rs.getString("CustLname");
                boolean librarycard = rs.getBoolean("LibraryCard");
                String contact = rs.getString("CustContact");

                Customer customer = new Customer(custid, firstname, lastname, librarycard, contact);
                customerList.add(customer);
            }

            rs.close();
            stmt.close();
            conn.close();

            table.setItems(customerList);

            id.setCellValueFactory(new PropertyValueFactory<>("custID"));
            firstname.setCellValueFactory(new PropertyValueFactory<>("custFname"));
            lastname.setCellValueFactory(new PropertyValueFactory<>("custLname"));
            librarycard.setCellValueFactory(new PropertyValueFactory<>("libraryCard"));
            contact.setCellValueFactory(new PropertyValueFactory<>("custContact"));

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
