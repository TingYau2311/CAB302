package com.tasktopia.model;

import java.util.ArrayList;
import java.util.List;

public class MockContactDAO implements IContactDAO {

    private final List<Contact> contacts = new ArrayList<>();

    @Override
    public void addContact(Contact contact) {
        contacts.add(contact);
    }

    @Override
    public List<Contact> getAllContacts() {
        return contacts;
    }

    @Override
    public Contact getContact(int id) {
        return contacts.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void deleteContact(Contact contact) {
        contacts.remove(contact);
    }

    @Override
    public void updateContact(Contact contact) {
        // Simple mock behaviour:
        // Remove old version, add updated version
        deleteContact(contact);
        contacts.add(contact);
    }
}
