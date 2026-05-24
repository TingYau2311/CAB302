package com.tasktopia.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MockContactDAOTest {

    private MockContactDAO dao;

    @BeforeEach
    void setUp() {
        dao = new MockContactDAO();
    }

    @Test
    void addContactStoresContact() {
        Contact c = new Contact("John", "Doe", "john@test.com", "pass");
        dao.addContact(c);

        assertEquals(1, dao.getAllContacts().size());
    }

    @Test
    void getAllContactsReturnsAllAdded() {
        dao.addContact(new Contact("A", "B", "a@test.com", "pass"));
        dao.addContact(new Contact("C", "D", "c@test.com", "pass"));

        assertEquals(2, dao.getAllContacts().size());
    }

    @Test
    void getContactByIdFindsCorrectContact() {
        Contact c = new Contact("John", "Doe", "john@test.com", "pass");
        c.setId(1);
        dao.addContact(c);

        Contact found = dao.getContact(1);
        assertNotNull(found);
        assertEquals("John", found.getFirstName());
    }

    @Test
    void getContactByIdReturnsNullWhenNotFound() {
        Contact found = dao.getContact(999);
        assertNull(found);
    }

    @Test
    void updateContactModifiesExisting() {
        Contact c = new Contact("John", "Doe", "john@test.com", "pass");
        c.setId(1);
        dao.addContact(c);

        c.setFirstName("Jane");
        dao.updateContact(c);

        Contact found = dao.getContact(1);
        assertEquals("Jane", found.getFirstName());
    }

    @Test
    void deleteContactRemovesFromList() {
        Contact c = new Contact("John", "Doe", "john@test.com", "pass");
        dao.addContact(c);

        dao.deleteContact(c);

        assertEquals(0, dao.getAllContacts().size());
    }

    @Test
    void addMultipleContactsWithDifferentEmails() {
        dao.addContact(new Contact("A", "B", "a@test.com", "pass"));
        dao.addContact(new Contact("C", "D", "c@test.com", "pass"));
        dao.addContact(new Contact("E", "F", "e@test.com", "pass"));

        assertEquals(3, dao.getAllContacts().size());
    }

    @Test
    void deleteNonExistentContactDoesNothing() {
        dao.addContact(new Contact("A", "B", "a@test.com", "pass"));
        Contact fake = new Contact("Fake", "User", "fake@test.com", "pass");

        dao.deleteContact(fake);

        assertEquals(1, dao.getAllContacts().size());
    }

    @Test
    void getAllContactsReturnsEmptyListInitially() {
        assertEquals(0, dao.getAllContacts().size());
    }

    @Test
    void updateNonExistentContactAddsIt() {
        Contact c = new Contact("A", "B", "a@test.com", "pass");
        c.setId(1);

        dao.updateContact(c);

        assertEquals(1, dao.getAllContacts().size());
    }

    @Test
    void addContactWithSameEmailCreatesMultipleEntries() {
        dao.addContact(new Contact("A", "B", "same@test.com", "pass1"));
        dao.addContact(new Contact("C", "D", "same@test.com", "pass2"));

        assertEquals(2, dao.getAllContacts().size());
    }


}