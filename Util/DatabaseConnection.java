package Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The DatabaseConnection class provides a Singleton connection to the database. 
 */
public class DatabaseConnection {
    private static Connection connection = null;

    private DatabaseConnection() {}

    /**
     * Returns the connection to the database.
     * If the connection is null, it establishes a new connection.
     * 
     * @return the connection to the database
     */
    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/quack", "john", "password");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    /**
     * Closes the connection to the database.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Main method to test the DatabaseConnection class.
     * It gets a connection and prints it to the console.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Connection conn = DatabaseConnection.getConnection();
        System.out.println(conn);
    }
}