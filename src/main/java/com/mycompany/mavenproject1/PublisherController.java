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

public class PublisherController {

    @FXML
    private ComboBox<String> publisherCombo;

    @FXML
    private TableColumn<Publisher, Integer> id;

    @FXML
    private TableColumn<Publisher, String> name;

    @FXML
    private TableColumn<Publisher, String> Country;

    @FXML
    private TextField searchAttribute;

    @FXML
    private TableView<Publisher> table;

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

    //jasper report call
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
            input = new FileInputStream(new File("PublishersReport.jrxml"));
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
    void addPublisherWindowCall(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AddPublisher.fxml"));
        Parent addPublisherRoot = loader.load();
        Stage addPublisherStage = new Stage();
        addPublisherStage.setScene(new Scene(addPublisherRoot));
        addPublisherStage.show();
    }

    @FXML
    void removePublisher(ActionEvent event) {
        try {
            Publisher selectedPublisher = table.getSelectionModel().getSelectedItem();

            if (selectedPublisher != null) {
                int pubid = selectedPublisher.getPubID();

                Class.forName("org.postgresql.Driver");
                String url = "jdbc:postgresql://localhost:5432/postgres";
                String user = "alaa";
                String password = "2342004";
                Connection conn = DriverManager.getConnection(url, user, password);

                Statement stmt = conn.createStatement();
                int rowsAffected = stmt.executeUpdate("DELETE FROM publisher WHERE pubid = " + pubid);

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Row with publisher id " + pubid + " deleted successfully.");
                    initialize();
                } else {
                    JOptionPane.showMessageDialog(null, "No rows found with publisher id " + pubid + ".");
                }

                stmt.close();
                conn.close();
            } else {
                JOptionPane.showMessageDialog(null, "Please select a publisher from the table.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid publisher id format. Please enter a valid integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while deleting the row. Please try again later.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @FXML
    void searchAction(ActionEvent event) {
        try {
            publisherCombo.setItems(FXCollections.observableArrayList("None", "PubID", "PubName", "PubCountry"));

            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "alaa";
            String password = "2342004";
            Connection conn = DriverManager.getConnection(url, user, password);

            String selectedColumn = publisherCombo.getValue();

            String query;
            if ("None".equals(selectedColumn)) {
                query = "SELECT * FROM Publisher";
            } else if ("PubID".equals(selectedColumn)) {
                query = "SELECT * FROM Publisher WHERE PubID = ?";
            } else {
                query = "SELECT * FROM Publisher WHERE " + selectedColumn + " ILIKE ?";
            }

            java.sql.PreparedStatement stmt = conn.prepareStatement(query);

            if (!"None".equals(selectedColumn)) {
                try {
                    if ("PubID".equals(selectedColumn)) {
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

            ObservableList<Publisher> publisherList = FXCollections.observableArrayList();

            while (rs.next()) {
                int pubid = rs.getInt("PubID");
                String pubname = rs.getString("PubName");
                String pubcountry = rs.getString("PubCountry");

                Publisher publisher = new Publisher(pubid, pubname, pubcountry);
                publisherList.add(publisher);
            }

            rs.close();
            stmt.close();
            conn.close();

            table.setItems(publisherList);

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
            publisherCombo.setItems(FXCollections.observableArrayList("None", "PubID", "PubName", "PubCountry"));

            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "alaa";
            String password = "2342004";
            Connection conn = DriverManager.getConnection(url, user, password);

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Publisher");

            ObservableList<Publisher> publisherList = FXCollections.observableArrayList();

            while (rs.next()) {
                int pubid = rs.getInt("PubID");
                String pubname = rs.getString("PubName");
                String pubcountry = rs.getString("PubCountry");

                Publisher publisher = new Publisher(pubid, pubname, pubcountry);
                publisherList.add(publisher);
            }

            rs.close();
            stmt.close();
            conn.close();

            table.setItems(publisherList);

            id.setCellValueFactory(new PropertyValueFactory<>("pubID"));
            name.setCellValueFactory(new PropertyValueFactory<>("pubName"));
            Country.setCellValueFactory(new PropertyValueFactory<>("pubCountry"));

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
