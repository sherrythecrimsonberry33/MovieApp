package frontend;


import backend.Entity.Movie;
import backend.database.AdminDAO;
import backend.database.MovieDAO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminView extends JFrame {
    private AdminDAO adminDAO;
    private MovieDAO movieDAO;
    private JList<Movie> movieList;
    private DefaultListModel<Movie> listModel;
    
    public AdminView() {
        this.adminDAO = new AdminDAO();
        this.movieDAO = new MovieDAO();
        
        setTitle("AcmePlex Admin Panel");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        createUI();
        refreshMovieList();
    }
    
    private void createUI() {
        // Main layout
        setLayout(new BorderLayout(10, 10));
        
        // Movie list panel
        listModel = new DefaultListModel<>();
        movieList = new JList<>(listModel);
        movieList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(movieList);
        add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Movie");
        JButton editButton = new JButton("Edit Movie");
        JButton deleteButton = new JButton("Delete Movie");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Button actions
        addButton.addActionListener(e -> showMovieDialog(null));
        
        editButton.addActionListener(e -> {
            Movie selected = movieList.getSelectedValue();
            if (selected != null) {
                showMovieDialog(selected);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a movie to edit");
            }
        });
        
        deleteButton.addActionListener(e -> {
            Movie selected = movieList.getSelectedValue();
            if (selected != null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete " + selected.getTitle() + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
                    
                if (confirm == JOptionPane.YES_OPTION) {
                    if (adminDAO.deleteMovie(selected.getId())) {
                        refreshMovieList();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete movie");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a movie to delete");
            }
        });
    }
    
    private void showMovieDialog(Movie movie) {
        JDialog dialog = new JDialog(this, movie == null ? "Add Movie" : "Edit Movie", true);
        dialog.setLayout(new GridLayout(0, 2, 5, 5));
        
        // Create form fields
        JTextField titleField = new JTextField(movie != null ? movie.getTitle() : "");
        JTextField posterUrlField = new JTextField(movie != null ? movie.getPosterUrl() : "");
        JTextField genreField = new JTextField(movie != null ? movie.getGenre() : "");
        JTextArea synopsisArea = new JTextArea(movie != null ? movie.getSynopsis() : "", 5, 20);
        JTextField ratingField = new JTextField(movie != null ? String.valueOf(movie.getRating()) : "");
        JTextField ageRatingField = new JTextField(movie != null ? movie.getAgeRating() : "");
        JTextField durationField = new JTextField(movie != null ? String.valueOf(movie.getDuration()) : "");
        
        // Add components to dialog
        dialog.add(new JLabel("Title:"));
        dialog.add(titleField);
        dialog.add(new JLabel("Poster URL:"));
        dialog.add(posterUrlField);
        dialog.add(new JLabel("Genre:"));
        dialog.add(genreField);
        dialog.add(new JLabel("Synopsis:"));
        dialog.add(new JScrollPane(synopsisArea));
        dialog.add(new JLabel("Rating (0-10):"));
        dialog.add(ratingField);
        dialog.add(new JLabel("Age Rating:"));
        dialog.add(ageRatingField);
        dialog.add(new JLabel("Duration (minutes):"));
        dialog.add(durationField);
        
        // Add save button
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                Movie newMovie = new Movie(
                    titleField.getText(),
                    posterUrlField.getText(),
                    genreField.getText(),
                    synopsisArea.getText(),
                    Double.parseDouble(ratingField.getText()),
                    ageRatingField.getText(),
                    Integer.parseInt(durationField.getText())
                );
                
                boolean success;
                if (movie != null) {
                    success = adminDAO.updateMovie(new Movie(
                        movie.getId(),
                        titleField.getText(),
                        posterUrlField.getText(),
                        genreField.getText(),
                        synopsisArea.getText(),
                        Double.parseDouble(ratingField.getText()),
                        ageRatingField.getText(),
                        Integer.parseInt(durationField.getText())
                    ));
                } else {
                    success = adminDAO.addMovie(newMovie);
                }
                
                if (success) {
                    refreshMovieList();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Operation failed");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for rating and duration");
            }
        });
        
        dialog.add(saveButton);
        
        // Show dialog
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void refreshMovieList() {
        listModel.clear();
        List<Movie> movies = movieDAO.getAllMovies();
        for (Movie movie : movies) {
            listModel.addElement(movie);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AdminView().setVisible(true);
        });
    }
}