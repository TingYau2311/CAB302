package com.tasktopia.model;

import java.util.ArrayList;
import java.util.List;

public class MockContactDAO implements IContactDAO {

    private final ArrayList<Contact> contacts = new ArrayList<>();
    private int autoIncrementedId = 1;

    @Override
    public void addContact(Contact contact) {
        contact.setId(autoIncrementedId++);
        contacts.add(contact);
    }

    @Override
    public void updateContact(Contact contact) {
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getId() == contact.getId()) {
                contacts.set(i, contact);
                return;
            }
        }
    }

    @Override
    public void deleteContact(Contact contact) {
        contacts.removeIf(c -> c.getId() == contact.getId());
    }

    @Override
    public Contact getContact(int id) {
        return contacts.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Contact> getAllContacts() {
        return new ArrayList<>(contacts);
    }
}