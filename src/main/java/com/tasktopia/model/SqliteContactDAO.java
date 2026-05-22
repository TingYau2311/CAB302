package com.tasktopia.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SQLite-backed implementation of {@link IContactDAO}.
 * <p>
 * Performs CRUD operations on the {@code contacts} table using the shared
 * connection provided by {@link SqliteConnection}. Generated primary keys are
 * written back to the {@link Contact} object after a successful insert.
 * </p>
 *
 * <p>All SQL exceptions are caught and printed to standard error rather than
 * propagated, so callers should verify return values where correctness matters.</p>
 */
public class SqliteContactDAO implements IContactDAO {

    /** Shared database connection obtained from the singleton {@link SqliteConnection}. */
    private Connection connection;

    /**
     * Constructs a new {@code SqliteContactDAO} using the application's
     * shared SQLite connection.
     */
    public SqliteContactDAO() {
        connection = SqliteConnection.getInstance();
    }

    /**
     * Inserts a new contact into the {@code contacts} table.
     * The generated database ID is assigned back to the {@code contact} object.
     *
     * @param contact the {@link Contact} to insert; must not be {@code null}
     */
    @Override
    public void addContact(Contact contact) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO contacts (firstName, lastName, email, password) VALUES (?, ?, ?, ?)");
            statement.setString(1, contact.getFirstName());
            statement.setString(2, contact.getLastName());
            statement.setString(3, contact.getEmail());
            statement.setString(4, contact.getPassword());
            statement.executeUpdate();
            // Set the id of the new contact from the generated key
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                contact.setId(generatedKeys.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates an existing contact record in the {@code contacts} table.
     * The contact is identified by its {@link Contact#getId() id}.
     *
     * @param contact the {@link Contact} containing updated field values; must not be {@code null}
     */
    @Override
    public void updateContact(Contact contact) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE contacts SET firstName = ?, lastName = ?, email = ?, password = ? WHERE id = ?");
            statement.setString(1, contact.getFirstName());
            statement.setString(2, contact.getLastName());
            statement.setString(3, contact.getEmail());
            statement.setString(4, contact.getPassword());
            statement.setInt(5, contact.getId());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a contact from the {@code contacts} table by its ID.
     *
     * @param contact the {@link Contact} to delete; must not be {@code null}
     */
    @Override
    public void deleteContact(Contact contact) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM contacts WHERE id = ?");
            statement.setInt(1, contact.getId());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a single contact from the {@code contacts} table by its ID.
     *
     * @param id the primary key of the contact to retrieve
     * @return the matching {@link Contact}, or {@code null} if no record exists with that ID
     */
    @Override
    public Contact getContact(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM contacts WHERE id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String firstName = resultSet.getString("firstName");
                String lastName  = resultSet.getString("lastName");
                String email     = resultSet.getString("email");
                String password  = resultSet.getString("password");
                Contact contact  = new Contact(firstName, lastName, email, password);
                contact.setId(id);
                return contact;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves all contacts stored in the {@code contacts} table.
     *
     * @return a {@link List} of all {@link Contact} objects; never {@code null},
     *         but may be empty if the table contains no rows
     */
    @Override
    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM contacts");
            while (resultSet.next()) {
                int    id        = resultSet.getInt("id");
                String firstName = resultSet.getString("firstName");
                String lastName  = resultSet.getString("lastName");
                String email     = resultSet.getString("email");
                String password  = resultSet.getString("password");
                Contact contact  = new Contact(firstName, lastName, email, password);
                contact.setId(id);
                contacts.add(contact);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contacts;
    }
}