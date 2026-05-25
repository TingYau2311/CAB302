package com.tasktopia.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton that manages the application's single SQLite database connection.
 * <p>
 * On first access, the class loads the SQLite JDBC driver, opens a connection
 * to {@code Cab302db.db} in the working directory, and creates the required
 * tables ({@code contacts}, {@code user_categories}, {@code tasks}) if they do
 * not already exist.
 * </p>
 *
 * <p>Use {@link #getInstance()} to obtain the shared {@link Connection} object.</p>
 *
 * <p><strong>Thread safety:</strong> This implementation is not thread-safe.
 * For a multi-threaded environment, additional synchronisation would be required.</p>
 */
public class SqliteConnection {

    /** The single shared database connection. */
    private static Connection instance = null;

    /**
     * Private constructor that initialises the SQLite driver, opens the
     * connection, and bootstraps the database schema.
     */
    private SqliteConnection() {
        try {
            // Force the SQLite driver to load
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:Cab302db.db";
            instance = DriverManager.getConnection(url);
            createTables();
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite driver not found: " + e);
        } catch (SQLException sqlEx) {
            System.err.println(sqlEx);
        }
    }

    /**
     * Creates the application's database tables if they do not already exist.
     * <p>
     * Tables created:
     * <ul>
     *   <li>{@code contacts} — stores registered user accounts</li>
     *   <li>{@code user_categories} — stores user-defined task categories</li>
     *   <li>{@code tasks} — stores task records linked to a contact</li>
     * </ul>
     * </p>
     */
    private void createTables() {
        try {
            Statement statement = instance.createStatement();

            // Contacts table
            statement.execute("CREATE TABLE IF NOT EXISTS contacts ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "firstName VARCHAR NOT NULL,"
                    + "lastName VARCHAR NOT NULL,"
                    + "email VARCHAR NOT NULL,"
                    + "password VARCHAR NOT NULL"
                    + ")");

            // User-defined categories table
            statement.execute("CREATE TABLE IF NOT EXISTS user_categories ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "userId INTEGER NOT NULL,"
                    + "name VARCHAR NOT NULL,"
                    + "colour VARCHAR NOT NULL,"
                    + "FOREIGN KEY (userId) REFERENCES contacts(id)"
                    + ")");

            // Tasks table
            statement.execute("CREATE TABLE IF NOT EXISTS tasks ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "title VARCHAR NOT NULL,"
                    + "startDate VARCHAR,"
                    + "endDate VARCHAR,"
                    + "description VARCHAR,"
                    + "tags VARCHAR,"
                    + "priority VARCHAR,"
                    + "userId INTEGER,"
                    + "FOREIGN KEY (userId) REFERENCES contacts(id)"
                    + ")");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the shared {@link Connection} instance, creating it on first call.
     *
     * @return the application's single SQLite {@link Connection}; never {@code null}
     *         after successful initialisation
     */
    public static Connection getInstance() {
        if (instance == null) {
            new SqliteConnection();
        }
        return instance;
    }
}