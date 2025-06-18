# 📚 The Green Library

**The Green Library** is a streamlined database system designed to enhance and modernize library operations. It efficiently manages members, employees, books, authors, and publishers — all within a cohesive, role-based environment.

---

## 🖼️ Preview
![Admin](https://github.com/user-attachments/assets/28ddff8d-bd9a-4fd5-b45d-f7053f4e15d7)
![Profile(1)](https://github.com/user-attachments/assets/46ad3464-c05d-492a-81a1-9f642129c0f6)
![User search](https://github.com/user-attachments/assets/c0b6a863-41a5-457a-8db8-f84411c7c6be)



---

## 🚀 Features

* 💘 **Complete Library Management**
  Centralized handling of members, staff, books, authors, and publishers.

* 🔐 **User Roles & Permissions**

  * **Admins**: Oversee daily operations, log books, and manage staff.
  * **Members**: Have personal profiles, can borrow books, leave feedback, and view activity history.
  * **Non-members**: Can browse and purchase books, but cannot borrow.

* 🧠 **Smart Search & Filtering**
  Search for books with filters based on availability and user preferences for a faster, more intuitive experience.

* 📊 **JasperReports Integration**
  Generate professional reports for all key tables — including members, books, employees, publishers, and more.

* 💳 **Membership Support**
  Distinction between **Free** and **Paid** memberships, enabling different access levels and borrowing capabilities.

---

## ⚙️ Backend Highlights

* Designed a robust **PostgreSQL schema** with proper entity relationships and constraints.
* Developed backend logic to manage borrowing, returning, purchasing, and feedback functionality.
* Improved performance and user experience by optimizing queries and using filtering mechanisms.

---

## 🗃️ Database Setup

1. Make sure you have **PostgreSQL** installed and running.
2. Create a new database (e.g., `green_library`).
3. Import the provided schema:

   ```bash
   psql -U your_username -d green_library -f green_library_schema.sql
   ```
4. Update the JDBC config in the project source with your DB credentials.

> 📀 You can find the schema file in the root of this repository: [`green_library_schema.sql`](./green_library_schema.sql)

---

## 🛠️ Technologies Used

* **Java**
* **PostgreSQL**
* **JasperReports**
* **JDBC**

---

## 📷 Additional Screenshots

<!-- Add any extra screenshots here (optional) -->

* Member Profile
  ![Member Profile](screenshots/member_profile.png)

* Admin Panel
  ![Admin Panel](screenshots/admin_panel.png)

* Generated Report Example
  ![Report Example](screenshots/report_example.png)

---

## 📦 Installation & Running the Project

1. Clone the repository:

   ```bash
   git clone https://github.com/AlaaArmoush/The-Green-Library.git
   ```
2. Set up the PostgreSQL database (see [Database Setup](#️-database-setup)).
3. Open the project in your preferred Java IDE.
4. Make sure JasperReports libraries and PostgreSQL JDBC driver are included.
5. Run the application.

---

## 📌 Future Improvements

* Add graphical user interface (GUI) layer for broader usability.
* Expand membership plan flexibility.
* Implement overdue tracking and automated email reminders.

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).
