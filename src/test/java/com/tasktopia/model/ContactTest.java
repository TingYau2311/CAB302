package com.tasktopia.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ContactTest {

    @Test
    void constructorSetsAllFieldsCorrectly() {
        Contact c = new Contact("John", "Doe", "john@test.com", "password123");

        assertEquals("John", c.getFirstName());
        assertEquals("Doe", c.getLastName());
        assertEquals("john@test.com", c.getEmail());
        assertEquals("password123", c.getPassword());
    }

    @Test
    void settersUpdateFieldsCorrectly() {
        Contact c = new Contact("A", "B", "a@b.com", "pass");

        c.setFirstName("Updated");
        c.setLastName("Name");
        c.setEmail("new@email.com");
        c.setPassword("newpass");

        assertEquals("Updated", c.getFirstName());
        assertEquals("Name", c.getLastName());
        assertEquals("new@email.com", c.getEmail());
        assertEquals("newpass", c.getPassword());
    }

    @Test
    void getFullNameCombinesFirstAndLast() {
        Contact c = new Contact("Jane", "Smith", "jane@test.com", "pass");
        String fullName = c.getFirstName() + " " + c.getLastName();
        assertEquals("Jane Smith", fullName);
    }
}