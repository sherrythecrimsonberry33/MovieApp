package frontend;


import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MovieTheaterMainPage extends JFrame {
    private JTable movieTable;
    private DefaultTableModel tableModel;

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/MovieTheater";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    public MovieTheaterMainPage() {
        setTitle("AcmePlex - Movie Theater");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Create Table Model
        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Genre", "Duration (min)", "Rating"}, 0);
        movieTable = new JTable(tableModel);

        // Fetch and Display Data from Database
        loadMovies();

        // Layout
        setLayout(new BorderLayout());
        add(new JScrollPane(movieTable), BorderLayout.CENTER);

        JLabel footerLabel = new JLabel("Movies Available at AcmePlex!", JLabel.CENTER);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        add(footerLabel, BorderLayout.SOUTH);
    }

    private void loadMovies() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM movies";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String genre = rs.getString("genre");
                int duration = rs.getInt("duration");
                double rating = rs.getDouble("rating");

                tableModel.addRow(new Object[]{id, title, genre, duration, rating});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading movies: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MovieTheaterMainPage mainPage = new MovieTheaterMainPage();
            mainPage.setVisible(true);
        });
    }
}
