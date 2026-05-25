package com.tasktopia.model;

/**
 * Represents a user (contact) in the Tasktopia application.
 * <p>
 * A {@code Contact} holds the personal and authentication details of a registered user,
 * including their name, email address, and password.
 * </p>
 */
public class Contact {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    /**
     * Constructs a new {@code Contact} with the specified details.
     * The {@code id} field is not set here; it is assigned by the database upon insertion.
     *
     * @param firstName the contact's first name
     * @param lastName  the contact's last name
     * @param email     the contact's email address, used as a login identifier
     * @param password  the contact's password (should be stored securely)
     */
    public Contact(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    /**
     * Returns the unique database ID of this contact.
     *
     * @return the contact's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique database ID of this contact.
     * Typically called by the DAO after a successful insert.
     *
     * @param id the ID to assign
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the contact's first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the contact's first name.
     *
     * @param firstName the new first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the contact's last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the contact's last name.
     *
     * @param lastName the new last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the contact's email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the contact's email address.
     *
     * @param email the new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the contact's password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the contact's password.
     *
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the contact's full name by concatenating first and last name.
     *
     * @return a string in the format {@code "firstName lastName"}
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}