

package frontend;

// import java.awt.*;
// import java.net.URL;
// import java.sql.*;
// import javax.imageio.ImageIO;
// import javax.swing.*;
// import javax.swing.table.DefaultTableModel;

// public class MovieTheaterMainPage extends JFrame {
//     private JTable movieTable;
//     private DefaultTableModel tableModel;
//     private JPanel posterPanel;

//     // Database credentials
//     private static final String DB_URL = "jdbc:mysql://localhost:3306/MovieTheater"; 
//     private static final String DB_USER = "movieadmin";
//     private static final String DB_PASSWORD = "password";

//     public MovieTheaterMainPage() {
//         setTitle("AcmePlex - Movie Theater");
//         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         setSize(900, 500); // Increased width to accommodate poster
//         setLocationRelativeTo(null);

//         // Create main content panel
//         JPanel mainContent = new JPanel(new BorderLayout());

//         // Create poster panel
//         posterPanel = new JPanel();
//         posterPanel.setPreferredSize(new Dimension(300, 400));

//         // Create Table Model
//         tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Genre", "Duration (min)", "Rating", "Poster URL"}, 0) {
//             @Override
//             public boolean isCellEditable(int row, int column) {
//                 return false; // Make table read-only
//             }
//         };
        
//         movieTable = new JTable(tableModel);
//         movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

//         // Add selection listener to table
//         movieTable.getSelectionModel().addListSelectionListener(e -> {
//             if (!e.getValueIsAdjusting()) {
//                 int selectedRow = movieTable.getSelectedRow();
//                 if (selectedRow != -1) {
//                     String posterUrl = (String) tableModel.getValueAt(selectedRow, 5); // Assuming Poster URL is column 5
//                     loadPoster(posterUrl);
//                 }
//             }
//         });
        
        

//         // Layout
//         mainContent.add(new JScrollPane(movieTable), BorderLayout.CENTER);
//         mainContent.add(posterPanel, BorderLayout.EAST);

//         setLayout(new BorderLayout());
//         add(mainContent, BorderLayout.CENTER);

//         JLabel footerLabel = new JLabel("Movies Available at AcmePlex!", JLabel.CENTER);
//         footerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
//         add(footerLabel, BorderLayout.SOUTH);

//         // Fetch and Display Data from Database
//         loadMovies();
//     }

//     private void loadPoster(String posterUrl) {
//         posterPanel.removeAll();
//         try {
//             // Use URI to handle poster URL
//             URL imageUrl = new java.net.URI(posterUrl).toURL();
//             Image image = ImageIO.read(imageUrl);
    
//             // Scale the image to fit the panel
//             Image scaledImage = image.getScaledInstance(280, -1, Image.SCALE_SMOOTH);
//             ImageIcon imageIcon = new ImageIcon(scaledImage);
    
//             JLabel imageLabel = new JLabel(imageIcon);
//             imageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
//             posterPanel.add(imageLabel);
//             posterPanel.revalidate();
//             posterPanel.repaint();
//         } catch (Exception e) {
//             JLabel errorLabel = new JLabel("Could not load poster");
//             posterPanel.add(errorLabel);
//             e.printStackTrace();
//         }
//     }
    
    
    
    

//     private void loadMovies() {
//         try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
//             String query = "SELECT id, title, genre, duration, rating, poster_url FROM movies";
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(query);
    
//             while (rs.next()) {
//                 int id = rs.getInt("id");
//                 String title = rs.getString("title");
//                 String genre = rs.getString("genre");
//                 int duration = rs.getInt("duration");
//                 double rating = rs.getDouble("rating");
//                 String posterUrl = rs.getString("poster_url");
    
//                 tableModel.addRow(new Object[]{id, title, genre, duration, rating, posterUrl});
//             }
//         } catch (SQLException e) {
//             JOptionPane.showMessageDialog(this, "Error loading movies: " + e.getMessage(),
//                     "Database Error", JOptionPane.ERROR_MESSAGE);
//         }
//     }
    
    

//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> {
//             try {
//                 // Set system look and feel
//                 UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//             } catch (Exception e) {
//                 e.printStackTrace();
//             }
//             MovieTheaterMainPage mainPage = new MovieTheaterMainPage();
//             mainPage.setVisible(true);
//         });
//     }
// }

// import javax.swing.*;
// import java.awt.*;
// import java.awt.geom.RoundRectangle2D;
// import java.util.*;
// import java.util.List;


// public class SeatMapGUI extends JFrame {
//     private static final int[] SEATS_PER_ROW = {12, 16, 18, 20, 22, 22, 24};
//     private static final String[] ROW_LABELS = {"A", "B", "C", "D", "E", "F", "G"};
    
//     private final Map<String, RoundedButton> seatButtons = new HashMap<>();
//     private final Set<String> selectedSeats = new HashSet<>();
//     private final JLabel selectionInfo = new JLabel("Selected Seats: None");

//     // Custom rounded button class
//     private static class RoundedButton extends JButton {
//         public RoundedButton() {
//             setContentAreaFilled(false);
//             setFocusPainted(false);
//             setBorderPainted(false);
//         }

//         @Override
//         protected void paintComponent(Graphics g) {
//             Graphics2D g2 = (Graphics2D) g.create();
//             g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
//             if (getModel().isPressed()) {
//                 g2.setColor(getBackground().darker());
//             } else {
//                 g2.setColor(getBackground());
//             }
            
//             g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
//             g2.dispose();
//         }
//     }

//     public SeatMapGUI() {
//         setTitle("AcmePlex - Screen 1");
//         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         setupLayout();
//         pack();
//         setLocationRelativeTo(null);
//         setMinimumSize(new Dimension(800, 600));
//     }
    
//     private void setupLayout() {
//         JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
//         mainPanel.setBackground(new Color(245, 245, 245)); // Light gray background
//         mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

//         // Screen panel
//         mainPanel.add(createScreenPanel(), BorderLayout.NORTH);
        
//         // Center panel
//         JPanel centerPanel = new JPanel(new BorderLayout());
//         centerPanel.setOpaque(false);
        
//         // Exits panel
//         JPanel exitsPanel = new JPanel(new BorderLayout());
//         exitsPanel.setOpaque(false);
//         exitsPanel.add(createExitLabel("EXIT ←"), BorderLayout.WEST);
//         exitsPanel.add(createExitLabel("EXIT →"), BorderLayout.EAST);
        
//         // Seats panel with padding
//         JPanel seatsOuterPanel = new JPanel(new BorderLayout());
//         seatsOuterPanel.setOpaque(false);
//         seatsOuterPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
//         JPanel seatsPanel = createSeatsPanel();
//         seatsOuterPanel.add(seatsPanel, BorderLayout.CENTER);
        
//         centerPanel.add(seatsOuterPanel, BorderLayout.CENTER);
//         centerPanel.add(exitsPanel, BorderLayout.SOUTH);
        
//         mainPanel.add(centerPanel, BorderLayout.CENTER);
        
//         // Info and legend panel
//         JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
//         bottomPanel.setOpaque(false);
//         bottomPanel.add(createLegendPanel(), BorderLayout.NORTH);
//         selectionInfo.setForeground(new Color(33, 33, 33));
//         bottomPanel.add(selectionInfo, BorderLayout.SOUTH);
        
//         mainPanel.add(bottomPanel, BorderLayout.SOUTH);

//         add(mainPanel);
//     }

//     private JPanel createScreenPanel() {
//         JPanel screenPanel = new JPanel() {
//             @Override
//             protected void paintComponent(Graphics g) {
//                 super.paintComponent(g);
//                 Graphics2D g2d = (Graphics2D) g;
//                 g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
//                 int width = getWidth();
//                 int height = getHeight();
//                 int inset = 40;
                
//                 int[] xPoints = {inset, width - inset, width - 10, 10};
//                 int[] yPoints = {0, 0, height - 5, height - 5};
                
//                 g2d.setColor(new Color(180, 180, 180));
//                 g2d.fillPolygon(xPoints, yPoints, 4);
                
//                 g2d.setColor(Color.DARK_GRAY);
//                 g2d.setFont(new Font("Arial", Font.BOLD, 14));
//                 FontMetrics fm = g2d.getFontMetrics();
//                 String text = "SCREEN";
//                 int textWidth = fm.stringWidth(text);
//                 g2d.drawString(text, (width - textWidth) / 2, height / 2);
//             }
//         };
//         screenPanel.setPreferredSize(new Dimension(600, 40));
//         screenPanel.setOpaque(false);
//         return screenPanel;
//     }

//     private JLabel createExitLabel(String text) {
//         JLabel exit = new JLabel(text);
//         exit.setForeground(new Color(220, 53, 69)); // Bootstrap danger red
//         exit.setFont(new Font("Arial", Font.BOLD, 14));
//         exit.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
//         return exit;
//     }

//     private JPanel createSeatsPanel() {
//         JPanel seatsPanel = new JPanel(new GridBagLayout());
//         seatsPanel.setOpaque(false);
//         GridBagConstraints gbc = new GridBagConstraints();
//         gbc.insets = new Insets(4, 4, 4, 4); // Add spacing between seats
        
//         for (int row = 0; row < SEATS_PER_ROW.length; row++) {
//             gbc.gridy = row;
            
//             // Row label
//             gbc.gridx = 0;
//             JLabel rowLabel = new JLabel(ROW_LABELS[row]);
//             rowLabel.setForeground(new Color(33, 33, 33));
//             seatsPanel.add(rowLabel, gbc);
            
//             int seats = SEATS_PER_ROW[row];
//             int startX = (24 - seats) / 2;
            
//             for (int col = 0; col < seats; col++) {
//                 gbc.gridx = startX + col + 1;
//                 RoundedButton seat = createSeatButton(ROW_LABELS[row], col + 1);
                
//                 if (row == 0 && (col == 2 || col == seats - 3)) {
//                     seat.setBackground(new Color(13, 110, 253)); // Bootstrap primary blue
//                 }
                
//                 seatsPanel.add(seat, gbc);
//                 seatButtons.put(ROW_LABELS[row] + (col + 1), seat);
//             }
            
//             gbc.gridx = startX + seats + 1;
//             JLabel rightLabel = new JLabel(ROW_LABELS[row]);
//             rightLabel.setForeground(new Color(33, 33, 33));
//             seatsPanel.add(rightLabel, gbc);
//         }
        
//         return seatsPanel;
//     }
    
//     private RoundedButton createSeatButton(String row, int seatNum) {
//         RoundedButton seat = new RoundedButton();
//         seat.setPreferredSize(new Dimension(35, 35));
//         seat.setBackground(new Color(0, 123, 255)); // Bootstrap blue
        
//         String seatId = row + seatNum;
//         seat.setToolTipText(seatId);
        
//         seat.addActionListener(e -> toggleSeatSelection(seatId, seat));
        
//         return seat;
//     }

//     private void toggleSeatSelection(String seatId, RoundedButton seat) {
//         if (selectedSeats.contains(seatId)) {
//             selectedSeats.remove(seatId);
//             seat.setBackground(new Color(0, 123, 255)); // Bootstrap blue
//         } else {
//             selectedSeats.add(seatId);
//             seat.setBackground(new Color(40, 167, 69)); // Bootstrap success green
//         }
//         updateSelectionInfo();
//     }
    
//     private JPanel createLegendPanel() {
//         JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
//         legend.setOpaque(false);
        
//         createLegendItem(legend, new Color(0, 123, 255), "Available");
//         createLegendItem(legend, new Color(40, 167, 69), "Selected");
//         createLegendItem(legend, new Color(108, 117, 125), "Occupied");
//         createLegendItem(legend, new Color(13, 110, 253), "Wheelchair Accessible");
        
//         return legend;
//     }
    
//     private void createLegendItem(JPanel legend, Color color, String text) {
//         JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
//         item.setOpaque(false);
        
//         RoundedButton sample = new RoundedButton();
//         sample.setPreferredSize(new Dimension(20, 20));
//         sample.setBackground(color);
//         sample.setEnabled(false);
        
//         JLabel label = new JLabel(text);
//         label.setForeground(new Color(33, 33, 33));
        
//         item.add(sample);
//         item.add(label);
//         legend.add(item);
//     }
    
//     private void updateSelectionInfo() {
//         if (selectedSeats.isEmpty()) {
//             selectionInfo.setText("Selected Seats: None");
//         } else {
//             List<String> sortedSeats = new ArrayList<>(selectedSeats);
//             Collections.sort(sortedSeats);
//             selectionInfo.setText("Selected Seats: " + String.join(", ", sortedSeats));
//         }
//     }

//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> {
//             try {
//                 UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//             } catch (Exception e) {
//                 e.printStackTrace();
//             }
//             new SeatMapGUI().setVisible(true);
//         });
//     }
// }


import javax.swing.*;

import backend.Entity.*;
import backend.Entity.receipt.*;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Arrays;

public class EmailReceiptTestGUI extends JFrame {
    private JTextField emailField;
    private JTextArea resultArea;

    public EmailReceiptTestGUI() {
        setTitle("Email & Receipt Test");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.add(new JLabel("Recipient Email:"));
        emailField = new JTextField();
        inputPanel.add(emailField);

        // Button
        JButton testButton = new JButton("Send Test Email");
        testButton.addActionListener(e -> sendTestEmail());

        // Result area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(testButton, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        add(mainPanel);
        setLocationRelativeTo(null);
    }

    private void sendTestEmail() {
        try {
            // Create sample data
            Movie movie = new Movie("Test Movie", "poster.jpg", "Action", 
                "Test synopsis", 8.5, "PG-13", 120);
            
            MovieHall hall = new MovieHall(MovieHallName.IMAX_SUPREME);
            
            MovieTimings timings = new MovieTimings(movie, hall, 
                LocalDateTime.now().plusDays(1), 15.0);
            
            Seat seat1 = new Seat(0, 0);
            Seat seat2 = new Seat(0, 1);
            
            TicketInfo ticketInfo = new TicketInfo(timings, Arrays.asList(seat1, seat2));
            
            GenerateReceipt receipt = new GenerateReceipt(ticketInfo, 15.0, TheatreInfo .getInstance());
            
            // Send email
            SendEmail emailSender = new SendEmail();
            emailSender.sendTicketAndReceipt(emailField.getText(), ticketInfo, receipt);
            
            resultArea.setText("Email sent successfully!\n\nReceipt Preview:\n" + 
                receipt.generateReceiptContent());
            
        } catch (Exception e) {
            resultArea.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new EmailReceiptTestGUI().setVisible(true);
        });
    }
}