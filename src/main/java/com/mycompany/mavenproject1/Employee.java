package com.mycompany.mavenproject1;

public class Employee {

    private int empID;
    private String empFname;
    private String empLname;
    private float empSalary;

    // Constructors
    public Employee() {
    }

    public Employee(int empID, String empFname, String empLname, float empSalary) {
        this.empID = empID;
        this.empFname = empFname;
        this.empLname = empLname;
        this.empSalary = empSalary;

    }

    // Getters and setters
    public int getEmpID() {
        return empID;
    }

    public void setEmpID(int empID) {
        this.empID = empID;
    }

    public String getEmpFname() {
        return empFname;
    }

    public void setEmpFname(String empFname) {
        this.empFname = empFname;
    }

    public String getEmpLname() {
        return empLname;
    }

    public void setEmpLname(String empLname) {
        this.empLname = empLname;
    }

    public float getEmpSalary() {
        return empSalary;
    }

    public void setEmpSalary(float empSalary) {
        this.empSalary = empSalary;
    }

    public String toString() {
        return "" + empID;
    }

}
