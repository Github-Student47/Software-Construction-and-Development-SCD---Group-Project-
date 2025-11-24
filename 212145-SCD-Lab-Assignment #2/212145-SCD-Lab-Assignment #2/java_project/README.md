# Student Registration System - Setup and Deployment Guide

## Student Information
- **Name:** Muhammad Kashan Tariq
- **Registration Number:** 212145
- **Subject:** Software Construction and Development
- **Date:** October 27, 2025

## Project Overview

This is a comprehensive **Student Course Registration System** built using **Model-Driven Engineering (MDE)** principles. The system implements a complete **MVC (Model-View-Controller)** architecture with a new **Grade Management Feature** that was added as part of the homework assignment.

## System Architecture

### Model-View-Controller (MVC) Structure

1. **Model Layer (M):**
   - `Student.java` - Student entity with course relationships
   - `Course.java` - Course entity with enrollment management
   - `Registration.java` - Many-to-Many relationship management
   - `Grade.java` - NEW FEATURE: Academic performance tracking
   - `User.java` - Authentication and authorization

2. **View Layer (V):**
   - JSP pages for user interface
   - Responsive web design
   - AJAX integration for dynamic updates

3. **Controller Layer (C):**
   - `StudentController.java` - Student management operations
   - `CourseController.java` - Course management operations
   - `GradeController.java` - Grade management operations
   - `UserController.java` - Authentication operations

4. **Service Layer (Business Logic):**
   - `StudentService.java` - Student business operations
   - `CourseService.java` - Course business operations
   - `GradeService.java` - Grade business operations
   - `UserService.java` - User business operations

5. **Data Access Layer (DAO):**
   - `StudentDAO.java` - Database operations for students
   - `CourseDAO.java` - Database operations for courses
   - `GradeDAO.java` - Database operations for grades
   - `RegistrationDAO.java` - Database operations for registrations
   - `UserDAO.java` - Database operations for users

## Prerequisites

Before setting up the system, ensure you have the following installed:

1. **Java Development Kit (JDK) 17 or higher**
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or use OpenJDK

2. **NetBeans IDE** (Recommended)
   - Download from [Apache NetBeans](https://netbeans.apache.org/)

3. **MySQL Server 8.0 or higher**
   - Download from [MySQL](https://dev.mysql.com/downloads/mysql/)

4. **Apache Tomcat 10.1 or higher**
   - Download from [Apache Tomcat](https://tomcat.apache.org/)

5. **StarUML or Visual Paradigm** (For UML modeling)
   - StarUML: [StarUML](https://staruml.io/)
   - Visual Paradigm: [Visual Paradigm](https://www.visual-paradigm.com/)

## Installation Steps

### 1. Database Setup

1. **Start MySQL Server:**
   ```bash
   # On Windows
   net start mysql

   # On Linux/Mac
   sudo systemctl start mysql
   ```

2. **Create Database:**
   ```sql
   -- Open MySQL command line and run:
   SOURCE src/main/resources/sql/create_database.sql;
   ```

3. **Verify Database Creation:**
   ```sql
   USE student_registration;
   SHOW TABLES;
   ```

### 2. Project Setup

1. **Clone/Download Project:**
   - Extract the project to your desired directory

2. **Configure Database Connection:**
   - Edit database connection settings in DAO classes:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/student_registration";
   private static final String USERNAME = "root";
   private static final String PASSWORD = "your_mysql_password";
   ```

3. **Build Project with Maven:**
   ```bash
   # In project root directory
   mvn clean compile
   mvn package
   ```

### 3. NetBeans Configuration

1. **Open Project in NetBeans:**
   - File → Open Project
   - Navigate to project directory
   - Select the `student-registration-system` folder

2. **Configure Tomcat Server:**
   - Tools → Servers
   - Add Tomcat Server
   - Set installation directory to your Tomcat installation

3. **Configure Database Driver:**
   - In NetBeans, go to Services → Databases
   - Right-click on Drivers → Add Driver
   - Add MySQL JDBC Driver (mysql-connector-java.jar)

### 4. Deploy Application

1. **Run on Tomcat:**
   - Right-click on project
   - Run → Run on Server
   - Select configured Tomcat server

2. **Access Application:**
   - Open browser and go to: `http://localhost:8080/student-registration-system`

## Usage Guide

### Student Management

1. **Create Student:**
   ```java
   StudentController controller = new StudentController();
   StudentController.OperationResult result = controller.createStudent(
       "John Doe", "john@email.com", "12345", "Computer Science", 3
   );
   ```

2. **Register for Course:**
   ```java
   boolean success = controller.registerStudentForCourse(
       studentId, "CS101", "Fall", 2025
   );
   ```

3. **View Student Courses:**
   ```java
   OperationResult result = controller.getStudentCourses(studentId);
   List<Course> courses = (List<Course>) result.getData();
   ```

### Grade Management (NEW FEATURE)

1. **Assign Grade:**
   ```java
   GradeService gradeService = new GradeService();
   boolean success = gradeService.assignGrade(
       studentId, "CS101", 85.5, "Final", "Fall", 2025, "Dr. Smith", true
   );
   ```

2. **Calculate GPA:**
   ```java
   double gpa = gradeService.calculateGPA(studentId);
   System.out.println("Student GPA: " + gpa);
   ```

3. **Submit Grade Appeal:**
   ```java
   boolean success = gradeService.submitGradeAppeal(
       gradeId, "I believe there was an error in my final exam grading"
   );
   ```

## Testing

### Run Test Suite

```bash
# Run unit tests
mvn test

# Run specific test class
mvn test -Dtest=StudentRegistrationSystemTest
```

### Manual Testing Steps

1. **Test Student Creation:**
   - Create multiple students with different programs
   - Verify data validation

2. **Test Course Registration:**
   - Register students for courses
   - Test capacity constraints
   - Test duplicate registration prevention

3. **Test Grade Management:**
   - Assign various types of grades
   - Calculate GPA
   - Test grade appeals process

4. **Test User Authentication:**
   - Create user accounts
   - Test login/logout functionality
   - Test permission system

## UML Diagrams

The system was designed using UML Class Diagrams, which were then used to generate the Java code. The key relationships include:

- **Student** → **Registration** (1:N)
- **Course** → **Registration** (1:N)
- **Student** → **Grade** (1:N)
- **Course** → **Grade** (1:N)
- **User** → Permissions (1:N)

## File Structure

```
student-registration-system/
├── src/
│   ├── main/
│   │   ├── java/com/studentregistrationsystem/
│   │   │   ├── model/           # Entity classes
│   │   │   ├── controller/      # Controller classes
│   │   │   ├── service/         # Business logic
│   │   │   ├── dao/            # Data access objects
│   │   │   └── util/           # Utility classes
│   │   ├── webapp/
│   │   │   ├── WEB-INF/
│   │   │   │   └── jsp/        # JSP pages
│   │   │   ├── css/            # Stylesheets
│   │   │   └── js/             # JavaScript files
│   │   └── resources/
│   │       ├── sql/            # Database scripts
│   │       └── config/         # Configuration files
│   └── test/
│       └── java/com/studentregistrationsystem/
│           └── test/           # Test classes
├── pom.xml                     # Maven configuration
└── README.md                   # This file
```

## Key Features Implemented

### Core Functionality
- ✅ Student CRUD operations
- ✅ Course CRUD operations
- ✅ Student-course registration management
- ✅ User authentication and authorization
- ✅ Database integration with MySQL
- ✅ MVC architecture implementation

### NEW FEATURE (Homework Requirement)
- ✅ **Grade Management System:**
  - Grade assignment and tracking
  - Letter grade calculation
  - Quality points and GPA calculation
  - Grade type management (MidTerm, Final, Assignment, Quiz, Project)
  - Grade publishing system
  - Grade appeals process
  - Academic performance statistics

### Advanced Features
- ✅ Prerequisites validation
- ✅ Course capacity management
- ✅ Enrollment tracking
- ✅ Academic progress monitoring
- ✅ Multi-semester support
- ✅ Search and filtering capabilities
- ✅ Comprehensive error handling
- ✅ Data validation
- ✅ Pagination support

## Assignment Requirements Fulfillment

### Theory Assignment 01 Requirements ✅
1. ✅ **Designed software using UML Class Diagram**
   - Complete UML class diagram created
   - All relationships and attributes defined
   - Method signatures specified

2. ✅ **Generated Java MVC code from UML diagram**
   - Automatic code generation from UML
   - Manual completion of business logic
   - Full MVC implementation

3. ✅ **Integrated MVC code with MySQL database**
   - Complete DAO layer implementation
   - Database schema created
   - JDBC connectivity established

4. ✅ **Implemented Tomcat server configuration**
   - Maven pom.xml with Tomcat plugin
   - NetBeans integration
   - Deployment configuration

### Homework Task Requirements ✅
5. ✅ **Added small new feature**
   - Grade Management System implemented
   - Grade assignment, tracking, and appeals

6. ✅ **Updated UML diagram**
   - Grade class added to UML diagram
   - Relationships updated
   - New attributes and methods defined

7. ✅ **Modified code to include new feature**
   - Grade model, DAO, service, and controller
   - Grade-related business logic
   - Database integration for grades

## Troubleshooting

### Common Issues

1. **Database Connection Error:**
   - Check MySQL server is running
   - Verify connection credentials
   - Ensure database exists

2. **Tomcat Deployment Error:**
   - Check Tomcat installation path
   - Verify server configuration in NetBeans
   - Check port availability (8080)

3. **Build Errors:**
   - Run `mvn clean compile`
   - Check Java version (requires JDK 17+)
   - Verify Maven installation

4. **JDBC Driver Issues:**
   - Ensure MySQL JDBC driver is in classpath
   - Check driver version compatibility

### Debug Steps

1. **Enable Logging:**
   - Check log files for detailed error messages
   - Enable debug level logging in configuration

2. **Database Debugging:**
   - Test database connectivity separately
   - Verify SQL scripts executed successfully
   - Check table creation and data insertion

## Performance Optimization

### Implemented Optimizations
- Connection pooling for database connections
- Prepared statements to prevent SQL injection
- Indexing on frequently queried columns
- Pagination for large result sets
- Efficient DAO patterns

### Future Enhancements
- Caching layer implementation
- Asynchronous processing
- RESTful API development
- Microservices architecture
- Cloud deployment

## Security Considerations

### Implemented Security Features
- Input validation and sanitization
- SQL injection prevention
- Password handling (hashing recommended for production)
- Session management
- User role-based access control

### Production Security Recommendations
- Implement password hashing (bcrypt, scrypt)
- Add CSRF protection
- Implement rate limiting
- Use HTTPS for all communications
- Regular security audits
- Input validation on both client and server side

## Maintenance and Support

### Regular Maintenance Tasks
- Database backup procedures
- Log file rotation
- Performance monitoring
- Security updates
- Dependency updates

### Monitoring
- Application performance monitoring
- Database performance tracking
- User activity logging
- Error tracking and alerting

## Conclusion

This Student Registration System demonstrates a complete implementation of Model-Driven Engineering principles with a focus on practical software development. The system successfully integrates:

- UML-based design and code generation
- MVC architecture implementation
- Database integration with MySQL
- Web application deployment on Tomcat
- Advanced features like grade management and academic tracking

The implementation satisfies all requirements for the Software Construction and Development course while providing a robust, scalable solution for educational institution needs.

---

**Author:** Muhammad Kashan Tariq  
**Registration Number:** 212145  
**Date:** October 27, 2025  
**Subject:** Software Construction and Development