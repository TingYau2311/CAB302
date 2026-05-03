package com.tasktopia.model;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteConnection {
    private static Connection instance = null;

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

    public static Connection getInstance() {
        if (instance == null) {
            new SqliteConnection();
        }
        return instance;
    }
}