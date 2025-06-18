package com.mycompany.mavenproject1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.swing.JRViewer;

public class AuthorController {

    @FXML
    private ComboBox<String> authorCombo;

    @FXML
    private TableColumn<Author, Integer> id;

    @FXML
    private TableColumn<Author, String> firstname;

    @FXML
    private TableColumn<Author, String> lastname;

    @FXML
    private TableColumn<Author, String> country;

    @FXML
    private TableView<Author> table;

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
    void report(ActionEvent event) {
        Connection con;
        InputStream input;
        JasperDesign jd;
        JasperReport jr;
        JasperPrint jp;
        OutputStream output;
        try {
            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "alaa";
            String password = "2342004";

            con = DriverManager.getConnection(url, user, password);
            input = new FileInputStream(new File("AuthorsReport.jrxml"));
            jd = JRXmlLoader.load(input);
            jr = JasperCompileManager.compileReport(jd);
            jp = JasperFillManager.fillReport(jr, null, con);
            JFrame frame = new JFrame("Authors Report");
            frame.getContentPane().add(new JRViewer(jp));
            frame.pack();
            frame.setVisible(true);
            input.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void addAuthorWindowCall(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AddAuthor.fxml"));
        Parent addAuthorRoot = loader.load();
        Stage addAuthorStage = new Stage();
        addAuthorStage.setScene(new Scene(addAuthorRoot));
        addAuthorStage.show();
    }

    @FXML
    void removeAuthor(ActionEvent event) {
        Author selectedAuthor = table.getSelectionModel().getSelectedItem();

        if (selectedAuthor != null) {
            int authid = selectedAuthor.getAuthID();
            try {
                Class.forName("org.postgresql.Driver");
                String url = "jdbc:postgresql://localhost:5432/postgres";
                String user = "alaa";
                String password = "2342004";
                Connection conn = DriverManager.getConnection(url, user, password);

                Statement stmt = conn.createStatement();
                int rowsAffected = stmt.executeUpdate("DELETE FROM author WHERE authid = " + authid);

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Row with author id " + authid + " deleted successfully.");
                    initialize();
                } else {
                    JOptionPane.showMessageDialog(null, "No rows found with author id " + authid + ".");
                }

                stmt.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "An error occurred while deleting the row. Please try again later.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select an author from the table.");
        }
    }

    @FXML
    void searchAction(ActionEvent event) {
        try {
            authorCombo.setItems(FXCollections.observableArrayList("None", "AuthID", "AuthFname", "AuthLname", "AuthCountry"));

            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "alaa";
            String password = "2342004";
            Connection conn = DriverManager.getConnection(url, user, password);

            String selectedColumn = authorCombo.getValue();

            String query;
            if ("None".equals(selectedColumn)) {
                query = "SELECT * FROM Author";
            } else if ("AuthID".equals(selectedColumn)) {
                query = "SELECT * FROM Author WHERE AuthID = ?";
            } else {
                query = "SELECT * FROM Author WHERE " + selectedColumn + " ILIKE ?";
            }

            java.sql.PreparedStatement stmt = conn.prepareStatement(query);

            if (!"None".equals(selectedColumn)) {
                try {
                    if ("AuthID".equals(selectedColumn)) {
                        stmt.setInt(1, Integer.parseInt(searchAttribute.getText()));
                    } else {
                        stmt.setString(1, "%" + searchAttribute.getText() + "%"); // Use "%" for pattern matching
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid input for " + selectedColumn + ". Please enter a valid input.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            ResultSet rs = stmt.executeQuery();

            ObservableList<Author> authorList = FXCollections.observableArrayList();

            while (rs.next()) {
                int authid = rs.getInt("AuthID");
                String firstname = rs.getString("AuthFname");
                String lastname = rs.getString("AuthLname");
                String country = rs.getString("AuthCountry");

                Author author = new Author(authid, firstname, lastname, country);
                authorList.add(author);
            }

            rs.close();
            stmt.close();
            conn.close();

            table.setItems(authorList);

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
            authorCombo.setItems(FXCollections.observableArrayList("None", "AuthID", "AuthFname", "AuthLname", "AuthCountry"));

            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "alaa";
            String password = "2342004";
            Connection conn = DriverManager.getConnection(url, user, password);

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Author");

            ObservableList<Author> authorList = FXCollections.observableArrayList();

            while (rs.next()) {
                int authid = rs.getInt("AuthID");
                String firstname = rs.getString("AuthFname");
                String lastname = rs.getString("AuthLname");
                String country = rs.getString("AuthCountry");

                Author author = new Author(authid, firstname, lastname, country);
                authorList.add(author);
            }

            rs.close();
            stmt.close();
            conn.close();

            table.setItems(authorList);

            id.setCellValueFactory(new PropertyValueFactory<>("authID"));
            firstname.setCellValueFactory(new PropertyValueFactory<>("authFname"));
            lastname.setCellValueFactory(new PropertyValueFactory<>("authLname"));
            country.setCellValueFactory(new PropertyValueFactory<>("authCountry"));

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
