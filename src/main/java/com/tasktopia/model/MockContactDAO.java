package com.tasktopia.model;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory implementation of {@link IContactDAO} for use in unit tests
 * and development scenarios where a real database is not available.
 * <p>
 * Contacts are stored in a plain {@link ArrayList} and IDs are assigned
 * via a simple auto-incrementing counter, mirroring the behaviour of a
 * database {@code AUTOINCREMENT} primary key.
 * </p>
 */
public class MockContactDAO implements IContactDAO {

    /** Internal list holding all contacts for the lifetime of this instance. */
    private final ArrayList<Contact> contacts = new ArrayList<>();

    /** Counter used to simulate auto-incremented primary keys. */
    private int autoIncrementedId = 1;

    /**
     * Adds the given contact to the in-memory store and assigns it the
     * next available auto-incremented ID.
     *
     * @param contact the {@link Contact} to add; must not be {@code null}
     */
    @Override
    public void addContact(Contact contact) {
        contact.setId(autoIncrementedId++);
        contacts.add(contact);
    }

    /**
     * Replaces the existing contact entry whose ID matches the given contact.
     * If no matching contact is found, the method returns without making changes.
     *
     * @param contact the {@link Contact} with updated values; must not be {@code null}
     */
    @Override
    public void updateContact(Contact contact) {
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getId() == contact.getId()) {
                contacts.set(i, contact);
                return;
            }
        }
    }

    /**
     * Removes the contact with the same ID as the given contact from the store.
     * If no matching contact is found, the method returns without making changes.
     *
     * @param contact the {@link Contact} to delete; must not be {@code null}
     */
    @Override
    public void deleteContact(Contact contact) {
        contacts.removeIf(c -> c.getId() == contact.getId());
    }

    /**
     * Finds and returns the contact with the specified ID.
     *
     * @param id the ID of the contact to retrieve
     * @return the matching {@link Contact}, or {@code null} if none is found
     */
    @Override
    public Contact getContact(int id) {
        return contacts.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns a copy of all contacts currently held in the in-memory store.
     *
     * @return a new {@link List} containing all stored {@link Contact} objects;
     *         never {@code null}, but may be empty
     */
    @Override
    public List<Contact> getAllContacts() {
        return new ArrayList<>(contacts);
    }
}