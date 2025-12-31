package com.scd.project.test;

import com.scd.project.model.Student;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit Tests for Student Model Class
 * 
 * Team Members:
 * - Muhammad Kashan Tariq (212145)
 * - Adeel Hussain (221829)
 * - Syed Abdain (221855)
 * - Muhammad Tauseef (221789)
 */
public class StudentDAOTest {

    private Student testStudent;

    @Before
    public void setUp() {
        testStudent = new Student(1, "Test Student", "test@example.com", "Computer Science");
    }

    @Test
    public void testStudentDefaultConstructor() {
        Student student = new Student();
        assertEquals(0, student.getId());
        assertNull(student.getName());
        assertNull(student.getEmail());
        assertNull(student.getCourse());
        assertNull(student.getPhone());
        assertNull(student.getAddress());
    }

    @Test
    public void testStudentFourParamConstructor() {
        Student student = new Student(5, "Jane Smith", "jane@example.com", "Data Science");

        assertEquals(5, student.getId());
        assertEquals("Jane Smith", student.getName());
        assertEquals("jane@example.com", student.getEmail());
        assertEquals("Data Science", student.getCourse());
    }

    @Test
    public void testStudentThreeParamConstructor() {
        Student student = new Student("New Student", "new@example.com", "IT");

        assertEquals(0, student.getId());
        assertEquals("New Student", student.getName());
        assertEquals("new@example.com", student.getEmail());
        assertEquals("IT", student.getCourse());
    }

    @Test
    public void testStudentSettersAndGetters() {
        Student student = new Student();
        student.setId(100);
        student.setName("John Doe");
        student.setEmail("john@example.com");
        student.setCourse("Software Engineering");
        student.setPhone("123-456-7890");
        student.setAddress("123 Main St");

        assertEquals(100, student.getId());
        assertEquals("John Doe", student.getName());
        assertEquals("john@example.com", student.getEmail());
        assertEquals("Software Engineering", student.getCourse());
        assertEquals("123-456-7890", student.getPhone());
        assertEquals("123 Main St", student.getAddress());
    }

    @Test
    public void testStudentToString() {
        String result = testStudent.toString();

        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("name=Test Student"));
        assertTrue(result.contains("email=test@example.com"));
        assertTrue(result.contains("course=Computer Science"));
    }

    @Test
    public void testStudentEqualsById() {
        Student student1 = new Student(1, "Test", "test@test.com", "Course");
        Student student2 = new Student(1, "Test", "test@test.com", "Course");

        assertEquals(student1.getId(), student2.getId());
        assertEquals(student1.getName(), student2.getName());
    }

    @Test
    public void testStudentModelValidation() {
        Student student = new Student();

        assertEquals(0, student.getId());
        assertNull(student.getName());
        assertNull(student.getEmail());
        assertNull(student.getCourse());

        student.setName("Valid Name");
        assertNotNull(student.getName());
        assertEquals("Valid Name", student.getName());
    }

    @Test
    public void testStudentEdgeCases() {
        Student student = new Student();
        student.setName("");
        student.setEmail("");
        student.setCourse("");

        assertEquals("", student.getName());
        assertEquals("", student.getEmail());
        assertEquals("", student.getCourse());
    }

    @Test
    public void testStudentNullHandling() {
        Student student = new Student();
        student.setPhone(null);
        student.setAddress(null);

        assertNull(student.getPhone());
        assertNull(student.getAddress());
    }

    @Test
    public void testStudentAllFields() {
        Student student = new Student();
        student.setId(1);
        student.setName("Test Name");
        student.setEmail("test@test.com");
        student.setCourse("Computer Science");
        student.setPhone("03001234567");
        student.setAddress("Test Address");

        assertNotNull(student.getId());
        assertNotNull(student.getName());
        assertNotNull(student.getEmail());
        assertNotNull(student.getCourse());
        assertNotNull(student.getPhone());
        assertNotNull(student.getAddress());
    }

    @Test
    public void testStudentIdRange() {
        Student student = new Student();
        student.setId(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, student.getId());

        student.setId(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, student.getId());
    }

    @Test
    public void testStudentNameLength() {
        Student student = new Student();
        String longName = "A";
        for (int i = 0; i < 50; i++) {
            longName += "A";
        }
        student.setName(longName);
        assertEquals(51, student.getName().length());
    }

    @Test
    public void testStudentEmailFormat() {
        Student student = new Student();
        student.setEmail("student@university.edu");

        assertTrue(student.getEmail().contains("@"));
        assertTrue(student.getEmail().contains("."));
    }
}