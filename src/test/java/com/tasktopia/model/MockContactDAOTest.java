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

    private Contact sample(String email) {
        return new Contact("Test", "User", email, "pass123");
    }

    @Test
    void addContactAssignsIdAndStores() {
        Contact c = sample("a@test.com");
        dao.addContact(c);

        List<Contact> all = dao.getAllContacts();
        assertEquals(1, all.size());
        assertEquals(1, all.get(0).getId());
    }

    @Test
    void updateContactReplacesExisting() {
        Contact c = sample("a@test.com");
        dao.addContact(c);

        c.setFirstName("Updated");
        dao.updateContact(c);

        Contact fetched = dao.getContact(c.getId());
        assertEquals("Updated", fetched.getFirstName());
    }

    @Test
    void deleteContactRemovesCorrectItem() {
        Contact c1 = sample("a@test.com");
        Contact c2 = sample("b@test.com");

        dao.addContact(c1);
        dao.addContact(c2);

        dao.deleteContact(c1);

        assertEquals(1, dao.getAllContacts().size());
        assertEquals("b@test.com", dao.getAllContacts().get(0).getEmail());
    }
}
