package com.mycompany.mavenproject1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.scene.control.Button;
import javax.swing.JOptionPane;

public class AddEmployeeController {

    @FXML
    private TextField EmployeeFNTF;

    @FXML
    private TextField EmployeeIDTF;

    @FXML
    private TextField EmployeeLNTF;

    @FXML
    private TextField EmployeeSalaryTF;

    @FXML
    private Button addEmployeeBTTN;

    @FXML
    private Button cancelAddEmployeeBTTN;

    @FXML
    void addEmployee(ActionEvent event) throws IOException {
        int empID = Integer.parseInt(EmployeeIDTF.getText());
        String empFname = EmployeeFNTF.getText();
        String empLname = EmployeeLNTF.getText();
        float empSalary = Float.parseFloat(EmployeeSalaryTF.getText());

        // Database connection details
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "alaa";
        String password = "2342004";

        // SQL statement to insert a new employee
        String sql = "INSERT INTO Employee (EmpID, EmpFname, EmpLname, EmpSalary) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, empID);
            pstmt.setString(2, empFname);
            pstmt.setString(3, empLname);
            pstmt.setFloat(4, empSalary);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Employee added successfully!");

                // Close the window after adding the employee
                Stage stage = (Stage) addEmployeeBTTN.getScene().getWindow();
                stage.close();

                // Refresh the TableView in the EmployeeController
                App.setRoot("Employee");

            } else {
                System.out.println("Failed to add employee!");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding employee: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input format. Please enter valid values.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    @FXML
    void cancelAddEmployee(ActionEvent event) {
        Stage stage = (Stage) cancelAddEmployeeBTTN.getScene().getWindow();
        stage.close();
    }

}
