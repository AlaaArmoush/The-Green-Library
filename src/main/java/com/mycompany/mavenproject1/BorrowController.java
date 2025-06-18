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

public class BorrowController {

    @FXML
    private TableColumn<Borrow, Integer> bookid;

    @FXML
    private ComboBox<String> borrowsCombo;

    @FXML
    private TableColumn<Borrow, Integer> customerid;

    @FXML
    private TableColumn<Borrow, String> enddate;

    @FXML
    private TextField searchAttribute;

    @FXML
    private TableColumn<Borrow, String> startdate;

    @FXML
    private TableView<Borrow> table;

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
            input = new FileInputStream(new File("BorrowReport.jrxml"));
            jd = JRXmlLoader.load(input);
            jr = JasperCompileManager.compileReport(jd);
            jp = JasperFillManager.fillReport(jr, null, con);
            JFrame frame = new JFrame("BorrowsReport");
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
    void addBorrowWindowCall(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AddBorrow.fxml"));
        Parent addPublisherRoot = loader.load();
        Stage addPublisherStage = new Stage();
        addPublisherStage.setScene(new Scene(addPublisherRoot));
        addPublisherStage.show();
    }

    @FXML
    void removeBorrow(ActionEvent event) {
        try {
            Borrow selectedBorrow = table.getSelectionModel().getSelectedItem();

            if (selectedBorrow != null) {
                int custid = selectedBorrow.getCustID();
                int bookid = selectedBorrow.getBookID();

                Class.forName("org.postgresql.Driver");
                String url = "jdbc:postgresql://localhost:5432/postgres";
                String user = "alaa";
                String password = "2342004";
                Connection conn = DriverManager.getConnection(url, user, password);

                Statement stmt = conn.createStatement();
                int rowsAffected = stmt.executeUpdate("DELETE FROM Borrows WHERE CustID = " + custid + " AND BookID = " + bookid);

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Row with customer id " + custid + " and book id " + bookid + " deleted successfully.");
                    initialize();
                } else {
                    JOptionPane.showMessageDialog(null, "No rows found with customer id " + custid + " and book id " + bookid + ".");
                }

                stmt.close();
                conn.close();
            } else {
                JOptionPane.showMessageDialog(null, "Please select a borrow from the table.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input format. Please enter valid integers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while deleting the row. Please try again later.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @FXML
    void searchAction(ActionEvent event) {
        try {
            borrowsCombo.setItems(FXCollections.observableArrayList("None", "CustID", "BookID", "StartDate", "EndDate"));

            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "alaa";
            String password = "2342004";
            Connection conn = DriverManager.getConnection(url, user, password);

            String selectedColumn = borrowsCombo.getValue();

            String query;
            if ("None".equals(selectedColumn)) {
                query = "SELECT * FROM Borrows";
            } else if ("CustID".equals(selectedColumn)) {
                query = "SELECT * FROM Borrows WHERE CustID = ?";
            } else if ("BookID".equals(selectedColumn)) {
                query = "SELECT * FROM Borrows WHERE BookID = ?";
            } else {
                query = "SELECT * FROM Borrows WHERE " + selectedColumn + " = ?";
            }

            java.sql.PreparedStatement stmt = conn.prepareStatement(query);

            if (!"None".equals(selectedColumn)) {
                try {
                    switch (selectedColumn) {
                        case "CustID":
                        case "BookID":
                            stmt.setInt(1, Integer.parseInt(searchAttribute.getText()));
                            break;
                        case "StartDate":
                        case "EndDate":
                            stmt.setDate(1, java.sql.Date.valueOf(searchAttribute.getText()));
                            break;
                        default:
                            stmt.setString(1, searchAttribute.getText());
                            break;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid input for " + selectedColumn + ". Please enter a valid value.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            ResultSet rs = stmt.executeQuery();

            ObservableList<Borrow> borrowList = FXCollections.observableArrayList();

            while (rs.next()) {
                int custID = rs.getInt("CustID");
                int bookID = rs.getInt("BookID");
                String startDate = rs.getString("StartDate");
                String endDate = rs.getString("EndDate");

                Borrow borrow = new Borrow(custID, bookID, startDate, endDate);
                borrowList.add(borrow);
            }

            rs.close();
            stmt.close();
            conn.close();

            table.setItems(borrowList);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while searching. Please try again later.", "Database Error", JOptionPane.ERROR_MESSAGE);
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
            borrowsCombo.setItems(FXCollections.observableArrayList("None", "CustID", "BookID", "StartDate", "EndDate"));

            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "alaa";
            String password = "2342004";
            Connection conn = DriverManager.getConnection(url, user, password);

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Borrows");

            ObservableList<Borrow> borrowList = FXCollections.observableArrayList();

            while (rs.next()) {
                int custID = rs.getInt("CustID");
                int bookID = rs.getInt("BookID");
                String startDate = rs.getString("StartDate");
                String endDate = rs.getString("EndDate");

                Borrow borrow = new Borrow(custID, bookID, startDate, endDate);
                borrowList.add(borrow);
            }

            rs.close();
            stmt.close();
            conn.close();

            bookid.setCellValueFactory(new PropertyValueFactory<>("bookID"));
            customerid.setCellValueFactory(new PropertyValueFactory<>("custID"));
            startdate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
            enddate.setCellValueFactory(new PropertyValueFactory<>("endDate"));

            table.setItems(borrowList);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while initializing. Please try again later.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
