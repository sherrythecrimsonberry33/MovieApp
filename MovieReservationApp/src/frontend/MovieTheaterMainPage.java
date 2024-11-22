// package frontend;


// import java.awt.*;
// import java.sql.*;
// import javax.swing.*;
// import javax.swing.table.DefaultTableModel;

// public class MovieTheaterMainPage extends JFrame {
//     private JTable movieTable;
//     private DefaultTableModel tableModel;

//     // Database credentials
//     private static final String DB_URL = "jdbc:mysql://localhost:3306/MovieTheater";
//     private static final String DB_USER = "root";
//     private static final String DB_PASSWORD = "password";

//     public MovieTheaterMainPage() {
//         setTitle("AcmePlex - Movie Theater");
//         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         setSize(600, 400);
//         setLocationRelativeTo(null);

//         // Create Table Model
//         tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Genre", "Duration (min)", "Rating"}, 0);
//         movieTable = new JTable(tableModel);

//         // Fetch and Display Data from Database
//         loadMovies();

//         // Layout
//         setLayout(new BorderLayout());
//         add(new JScrollPane(movieTable), BorderLayout.CENTER);

//         JLabel footerLabel = new JLabel("Movies Available at AcmePlex!", JLabel.CENTER);
//         footerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
//         add(footerLabel, BorderLayout.SOUTH);
//     }

//     private void loadMovies() {
//         try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
//             String query = "SELECT * FROM movies";
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(query);

//             while (rs.next()) {
//                 int id = rs.getInt("id");
//                 String title = rs.getString("title");
//                 String genre = rs.getString("genre");
//                 int duration = rs.getInt("duration");
//                 double rating = rs.getDouble("rating");

//                 tableModel.addRow(new Object[]{id, title, genre, duration, rating});
//             }
//         } catch (SQLException e) {
//             JOptionPane.showMessageDialog(this, "Error loading movies: " + e.getMessage(),
//                     "Database Error", JOptionPane.ERROR_MESSAGE);
//         }
//     }

//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> {
//             MovieTheaterMainPage mainPage = new MovieTheaterMainPage();
//             mainPage.setVisible(true);
//         });
//     }
// }


package frontend;

import java.awt.*;
import java.net.URL;
import java.sql.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MovieTheaterMainPage extends JFrame {
    private JTable movieTable;
    private DefaultTableModel tableModel;
    private JPanel posterPanel;

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/MovieTheater"; 
    private static final String DB_USER = "movieadmin";
    private static final String DB_PASSWORD = "password";

    public MovieTheaterMainPage() {
        setTitle("AcmePlex - Movie Theater");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500); // Increased width to accommodate poster
        setLocationRelativeTo(null);

        // Create main content panel
        JPanel mainContent = new JPanel(new BorderLayout());

        // Create poster panel
        posterPanel = new JPanel();
        posterPanel.setPreferredSize(new Dimension(300, 400));

        // Create Table Model
        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Genre", "Duration (min)", "Rating", "Poster URL"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        movieTable = new JTable(tableModel);
        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add selection listener to table
        movieTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = movieTable.getSelectedRow();
                if (selectedRow != -1) {
                    String posterUrl = (String) tableModel.getValueAt(selectedRow, 5); // Assuming Poster URL is column 5
                    loadPoster(posterUrl);
                }
            }
        });
        
        

        // Layout
        mainContent.add(new JScrollPane(movieTable), BorderLayout.CENTER);
        mainContent.add(posterPanel, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(mainContent, BorderLayout.CENTER);

        JLabel footerLabel = new JLabel("Movies Available at AcmePlex!", JLabel.CENTER);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        add(footerLabel, BorderLayout.SOUTH);

        // Fetch and Display Data from Database
        loadMovies();
    }

    private void loadPoster(String posterUrl) {
        posterPanel.removeAll();
        try {
            // Use URI to handle poster URL
            URL imageUrl = new java.net.URI(posterUrl).toURL();
            Image image = ImageIO.read(imageUrl);
    
            // Scale the image to fit the panel
            Image scaledImage = image.getScaledInstance(280, -1, Image.SCALE_SMOOTH);
            ImageIcon imageIcon = new ImageIcon(scaledImage);
    
            JLabel imageLabel = new JLabel(imageIcon);
            imageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
            posterPanel.add(imageLabel);
            posterPanel.revalidate();
            posterPanel.repaint();
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("Could not load poster");
            posterPanel.add(errorLabel);
            e.printStackTrace();
        }
    }
    
    
    
    

    private void loadMovies() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT id, title, genre, duration, rating, poster_url FROM movies";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
    
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String genre = rs.getString("genre");
                int duration = rs.getInt("duration");
                double rating = rs.getDouble("rating");
                String posterUrl = rs.getString("poster_url");
    
                tableModel.addRow(new Object[]{id, title, genre, duration, rating, posterUrl});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading movies: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            MovieTheaterMainPage mainPage = new MovieTheaterMainPage();
            mainPage.setVisible(true);
        });
    }
}