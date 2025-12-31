# University Student Management System (USMS)

## 🎓 Software Construction and Development - Term Project

A complete Spring MVC web application for managing student records, demonstrating Object-Oriented Programming principles, MVC architecture, database integration, and CI/CD practices.

---

## 👥 Team Members

| Name | Registration Number |
|------|---------------------|
| Muhammad Kashan Tariq | 212145 |
| Adeel Hussain | 221829 |
| Syed Abdain | 221855 |
| Muhammad Tauseef | 221789 |

---

## 📋 Project Overview

**Project Name:** University Student Management System (USMS)  
**Application Type:** Spring MVC Web Application  
**Database:** MySQL  
**Server:** Apache Tomcat 10  
**Framework:** Spring Framework 6.x  
**Build Tool:** Apache Maven / Apache Ant  
**Testing:** JUnit 5  

---

## 🚀 Quick Start Guide

### Prerequisites

Before running the application, ensure you have:

- [x] Java JDK 17 or higher installed
- [x] Apache Tomcat 10 installed and running
- [x] MySQL (via XAMPP) installed and running
- [x] Apache Maven or Ant installed

### Installation Steps

#### 1. Setup Database

1. Open phpMyAdmin (http://localhost/phpmyadmin)
2. Click the "SQL" tab
3. Copy and paste the contents of `database_setup.sql`
4. Click "Go" to execute

Or run from command line:
```bash
mysql -u root -p < database_setup.sql
```

#### 2. Configure Database Connection

Edit file: `src/main/java/com/scd/project/config/MvcConfig.java`

Update the database password:
```java
dataSource.setPassword("YOUR_MYSQL_PASSWORD");
```

#### 3. Import Project in NetBeans

1. Open NetBeans IDE
2. File → Open Project
3. Navigate to `USMS_Project` folder
4. Select the project and click "Open"

#### 4. Configure Tomcat in NetBeans

1. Go to Tools → Servers
2. Click "Add Server"
3. Select "Apache Tomcat"
4. Browse to Tomcat installation directory (e.g., `C:\apache-tomcat-10.1`)
5. Set username and password (use admin/admin123)
6. Click "Finish"

#### 5. Run the Application

1. Right-click on the project in NetBeans
2. Select "Run" (or press F6)
3. Application will be deployed to Tomcat
4. Open browser and navigate to: http://localhost:8080/USMS

---

## 📁 Project Structure

```
USMS_Project/
├── pom.xml                          # Maven configuration and dependencies
├── build.xml                        # Ant build configuration
├── Jenkinsfile                      # Jenkins CI/CD pipeline
├── database_setup.sql               # Database setup script
├── README.md                        # This file
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/scd/project/
│   │   │       ├── config/
│   │   │       │   └── MvcConfig.java           # Spring MVC configuration
│   │   │       ├── controller/
│   │   │       │   └── StudentController.java   # HTTP request handlers
│   │   │       ├── dao/
│   │   │       │   ├── StudentDAO.java          # DAO interface
│   │   │       │   └── StudentDAOImpl.java      # DAO implementation
│   │   │       └── model/
│   │   │           └── Student.java             # Student entity class
│   │   │
│   │   ├── resources/
│   │   │   └── (configuration files)
│   │   │
│   │   └── webapp/
│   │       ├── index.jsp                        # Landing page
│   │       └── WEB-INF/
│   │           ├── web.xml                      # Deployment descriptor
│   │           └── views/
│   │               ├── header.jsp               # Common header
│   │               ├── footer.jsp               # Common footer
│   │               ├── list-students.jsp        # List all students
│   │               ├── student-form.jsp         # Add/Edit form
│   │               └── student-view.jsp         # View student details
│   │
│   └── test/
│       └── java/
│           └── com/scd/project/test/
│               └── StudentDAOTest.java          # Unit tests
│
└── lib/                                   # Library files (if needed)
```

---

## 🔧 Features

### Core Functionality

- ✅ **Add Students** - Register new students with name, email, course, phone, and address
- ✅ **View Students** - Display all students in a responsive table
- ✅ **Edit Students** - Update existing student information
- ✅ **Delete Students** - Remove student records with confirmation
- ✅ **Search Students** - Find students by name
- ✅ **View Details** - View complete student information

### Technical Features

- ✅ **MVC Architecture** - Clean separation of Model, View, and Controller
- ✅ **Database Integration** - MySQL database with JDBC
- ✅ **Responsive Design** - Bootstrap 5 for mobile-friendly UI
- ✅ **Form Validation** - Client-side and server-side validation
- ✅ **Unit Testing** - JUnit 5 test cases
- ✅ **CI/CD Ready** - Jenkins pipeline configuration

---

## 🛠️ Technologies Used

| Technology | Purpose |
|------------|---------|
| Java 17+ | Programming language |
| Spring MVC 6 | Web framework |
| MySQL | Database |
| Bootstrap 5 | CSS framework |
| JUnit 5 | Unit testing |
| Apache Tomcat 10 | Application server |
| Apache Maven | Build tool |
| Apache Ant | Alternative build tool |
| Jenkins | CI/CD automation |

---

## 📝 Database Schema

### Students Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT | Unique student ID |
| name | VARCHAR(100) | NOT NULL | Full name |
| email | VARCHAR(100) | NOT NULL, UNIQUE | Email address |
| course | VARCHAR(50) | NOT NULL | Course name |
| phone | VARCHAR(20) | NULL | Phone number |
| address | TEXT | NULL | Physical address |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Creation time |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE | Last update |

---

## 🧪 Running Tests

### Unit Tests

Run JUnit tests using Maven:
```bash
mvn test
```

Run tests using Ant:
```bash
ant test
```

### Test Coverage

The project includes unit tests for:
- Student model (getters, setters, constructors)
- StudentDAO methods (CRUD operations)
- Validation scenarios

---

## 🚢 Deployment

### Local Deployment (Tomcat)

1. Build the WAR file:
   ```bash
   mvn package
   ```
   
   Or with Ant:
   ```bash
   ant build
   ```

2. Deploy WAR file:
   - Copy `target/USMS.war` to `Tomcat/webapps/` directory
   - Or use Tomcat Manager application

3. Access deployed application:
   ```
   http://localhost:8080/USMS
   ```

### CI/CD Deployment with Jenkins

1. Configure Jenkins with required plugins
2. Create a new Pipeline job
3. Use the provided `Jenkinsfile`
4. Configure credentials for GitHub, Tomcat, and MySQL
5. Run the pipeline

---

## 📅 Submission Requirements

As per course requirements, this project includes:

- [x] Title Page with team information
- [x] SRS Document and UML Diagrams
- [x] Generated Java Code
- [x] Project Implementation Details
- [x] Build and Unit Testing
- [x] CI/CD Pipeline Configuration
- [x] Application Release and Deployment

---

## 📚 Learning Outcomes

This project demonstrates:

1. **Object-Oriented Programming** - Classes, objects, encapsulation, inheritance
2. **MVC Architecture** - Model-View-Controller design pattern
3. **Database Operations** - JDBC, CRUD operations, DAO pattern
4. **Web Development** - JSP, JSTL, Bootstrap, form handling
5. **Testing** - Unit testing with JUnit 5
6. **Version Control** - Git and GitHub collaboration
7. **CI/CD** - Jenkins pipeline automation
8. **Build Tools** - Maven and Ant configuration

---

## 📞 Support

For issues or questions:

1. Check the troubleshooting section below
2. Review course lab manuals
3. Contact team members

---

## 🔍 Troubleshooting

### Common Issues

**Issue:** MySQL Connection Error
```
Solution: Check database name, username, and password in MvcConfig.java
```

**Issue:** Tomcat Not Starting
```
Solution: Ensure Tomcat is properly configured and no other process uses port 8080
```

**Issue:** JSP Pages Not Rendering
```
Solution: Check that views are in /WEB-INF/views/ directory
```

**Issue:** JUnit Tests Failing
```
Solution: Ensure JUnit 5 dependencies are in pom.xml
```

---

## 📄 License

This project is for educational purposes as part of the Software Construction and Development course.

---

## 🙏 Acknowledgments

- Course Instructor: Mr. Sharif Hussain
- Based on Lab Manuals 1-13
- Uses Bootstrap 5 (MIT License)

---

**© 2025 Software Construction and Development - All Rights Reserved**
