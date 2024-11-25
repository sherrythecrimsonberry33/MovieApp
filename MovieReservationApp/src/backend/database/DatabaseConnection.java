// package backend.database;


// import java.sql.*;

// public class DatabaseConnection {
//     private static final String DB_URL = "jdbc:mysql://localhost:3306/MovieTheater";
//     private static final String DB_USER = "movieadmin";
//     private static final String DB_PASSWORD = "password";
//     private static DatabaseConnection instance;
//     private Connection connection;

//     private DatabaseConnection() {
//         try {
//             connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//         } catch (SQLException e) {
//             throw new RuntimeException("Failed to connect to database", e);
//         }
//     }

//     public static synchronized DatabaseConnection getInstance() {
//         if (instance == null) {
//             instance = new DatabaseConnection();
//         }
//         return instance;
//     }

//     public Connection getConnection() {
//         return connection;
//     }

//     public void closeConnection() {
//         try {
//             if (connection != null && !connection.isClosed()) {
//                 connection.close();
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }
//     }
// }


package backend.database;

import java.sql.*;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/MovieTheater";
    private static final String DB_USER = "movieadmin";
    private static final String DB_PASSWORD = "password";
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        createConnection();
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    private void createConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                createConnection();
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get database connection", e);
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}