package com.mycompany.mavenproject1;

import java.io.File;
import java.io.FileInputStream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javafx.scene.input.KeyCode;
import javax.swing.JFrame;

import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.swing.JRViewer;

public class EmployeeController {

    @FXML
    private ComboBox<String> employeeCombo;

    @FXML
    private TableColumn<Employee, Integer> id;

    @FXML
    private TableColumn<Employee, String> firstname;

    @FXML
    private TableColumn<Employee, String> lastname;

    @FXML
    private TableColumn<Employee, Float> salary;

    @FXML
    private TextField searchAttribute;

    @FXML
    private TableView<Employee> table;

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

    //show jasper report method
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
            input = new FileInputStream(new File("EmployeeReport.jrxml"));
            jd = JRXmlLoader.load(input);
            jr = JasperCompileManager.compileReport(jd);
            jp = JasperFillManager.fillReport(jr, null, con);
            JFrame frame = new JFrame("Employees Report");
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
    void searchAction(ActionEvent event) {
        try {
            employeeCombo.setItems(FXCollections.observableArrayList("None", "EmpID", "EmpFname", "EmpLname", "EmpSalary"));

            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "alaa";
            String password = "2342004";
            Connection conn = DriverManager.getConnection(url, user, password);

            String selectedColumn = employeeCombo.getValue();

            String query;
            if ("None".equals(selectedColumn)) {
                query = "SELECT EmpID, EmpFname, EmpLname, EmpSalary FROM Employee";
            } else if ("EmpID".equals(selectedColumn)) {
                query = "SELECT * FROM Employee WHERE EmpID = ?";
            } else if ("EmpSalary".equals(selectedColumn)) {
                query = "SELECT * FROM Employee WHERE EmpSalary = ?";
            } else {
                query = "SELECT EmpID, EmpFname, EmpLname, EmpSalary FROM Employee WHERE " + selectedColumn + " ILIKE ?";
            }

            java.sql.PreparedStatement stmt = conn.prepareStatement(query);

            if (!"None".equals(selectedColumn)) {
                try {
                    if ("EmpID".equals(selectedColumn)) {
                        stmt.setInt(1, Integer.parseInt(searchAttribute.getText()));
                    } else if ("EmpSalary".equals(selectedColumn)) {
                        stmt.setFloat(1, Float.parseFloat(searchAttribute.getText()));
                    } else {
                        stmt.setString(1, "%" + searchAttribute.getText() + "%"); // Use "%" for pattern matching
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid input for " + selectedColumn + ". Please enter a valid input.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            ResultSet rs = stmt.executeQuery();

            ObservableList<Employee> employeeList = FXCollections.observableArrayList();

            while (rs.next()) {
                int empID = rs.getInt("EmpID");
                String empFname = rs.getString("EmpFname");
                String empLname = rs.getString("EmpLname");
                float empSalary = rs.getFloat("EmpSalary");

                Employee employee = new Employee(empID, empFname, empLname, empSalary);
                employeeList.add(employee);
            }

            rs.close();
            stmt.close();
            conn.close();

            table.setItems(employeeList);

            id.setCellValueFactory(new PropertyValueFactory<>("empID"));
            firstname.setCellValueFactory(new PropertyValueFactory<>("empFname"));
            lastname.setCellValueFactory(new PropertyValueFactory<>("empLname"));
            salary.setCellValueFactory(new PropertyValueFactory<>("empSalary"));

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void addEmployee(ActionEvent event) {
        try {
            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");

            // Establish the connection
            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "alaa";
            String password = "2342004";
            Connection conn = DriverManager.getConnection(url, user, password);

            // Prompt the user to enter employee details
            String empFname = JOptionPane.showInputDialog(null, "Enter the first name of the employee:");
            String empLname = JOptionPane.showInputDialog(null, "Enter the last name of the employee:");
            String empSalaryString = JOptionPane.showInputDialog(null, "Enter the salary of the employee:");

            if (empFname != null && empLname != null && empSalaryString != null) {
                float empSalary = Float.parseFloat(empSalaryString);

                // Create a PreparedStatement
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO Employee(EmpFname, EmpLname, EmpSalary) VALUES (?, ?, ?)");
                stmt.setString(1, empFname);
                stmt.setString(2, empLname);
                stmt.setFloat(3, empSalary);

                // Execute the insert query
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Employee added successfully.");
                    searchAction(new ActionEvent());  // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to add employee.");
                }

                // Close statement and connection
                stmt.close();
                conn.close();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid salary format. Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while adding the employee. Please try again later.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @FXML
    void removeEmployee(ActionEvent event) {
        try {
            Employee selectedEmployee = table.getSelectionModel().getSelectedItem();

            if (selectedEmployee != null) {
                int empid = selectedEmployee.getEmpID();

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
                int rowsAffected = stmt.executeUpdate("DELETE FROM Employee WHERE EmpID = " + empid);

                // Check if any rows were deleted
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Row with Employee id " + empid + " deleted successfully.");
                    searchAction(new ActionEvent());  // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(null, "No rows found with Employee id " + empid + ".");
                }

                // Close statement and connection
                stmt.close();
                conn.close();
            } else {
                JOptionPane.showMessageDialog(null, "Please select an employee from the table.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid Employee ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while deleting the employee. Please try again later.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @FXML
    void addEmployeeWindowCall(ActionEvent event) {
        try {
            // Load AddEmployee.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddEmployee.fxml"));
            Parent addEmployeeRoot = loader.load();

            // Set a new stage
            Stage addEmployeeStage = new Stage();
            addEmployeeStage.setScene(new Scene(addEmployeeRoot));
            addEmployeeStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        try {
            searchAttribute.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    searchAction(new ActionEvent()); // Call the searchAction method when Enter key is pressed
                }
            });
            employeeCombo.setItems(FXCollections.observableArrayList("None", "EmpID", "EmpFname", "EmpLname", "EmpSalary"));

            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");

            // Establish the connection
            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "alaa";
            String password = "2342004";
            Connection conn = DriverManager.getConnection(url, user, password);

            // Create a statement
            Statement stmt = conn.createStatement();

            // Execute the SQL query
            ResultSet rs = stmt.executeQuery("SELECT EmpID, EmpFname, EmpLname, EmpSalary FROM Employee");

            // Create an ObservableList to store Employee objects
            ObservableList<Employee> employeeList = FXCollections.observableArrayList();

            // Iterate through the ResultSet and populate the ObservableList
            while (rs.next()) {
                // Retrieve employee data
                int empID = rs.getInt("EmpID");
                String empFname = rs.getString("EmpFname");
                String empLname = rs.getString("EmpLname");
                float empSalary = rs.getFloat("EmpSalary");

                // Create a new Employee object and add it to the list
                Employee employee = new Employee(empID, empFname, empLname, empSalary);
                employeeList.add(employee);
            }

            // Close the ResultSet, statement, and connection
            rs.close();
            stmt.close();
            conn.close();

            // Set the items to the table
            table.setItems(employeeList);

            // Bind columns to model properties
            id.setCellValueFactory(new PropertyValueFactory<>("empID"));
            firstname.setCellValueFactory(new PropertyValueFactory<>("empFname"));
            lastname.setCellValueFactory(new PropertyValueFactory<>("empLname"));
            salary.setCellValueFactory(new PropertyValueFactory<>("empSalary"));

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
        }
    }

}
