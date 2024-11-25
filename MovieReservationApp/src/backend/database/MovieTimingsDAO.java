// package backend.database;


// import backend.Entity.*;

// import java.sql.*;
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;

// public class MovieTimingsDAO {
//     private DatabaseConnection dbConnection;
//     private MovieDAO movieDAO;  // Assuming you have this

//     public MovieTimingsDAO() {
//         this.dbConnection = DatabaseConnection.getInstance();
//         this.movieDAO = new MovieDAO();
//     }

//     public List<MovieTimings> getAvailableTimingsForMovie(int movieId) {
//         List<MovieTimings> timings = new ArrayList<>();
//         LocalDateTime currentTime = LocalDateTime.now();
        
//         String query = "SELECT * FROM movie_timings WHERE movie_id = ? " +
//                       "AND show_datetime > ? AND DATE(show_datetime) = CURDATE() " +
//                       "ORDER BY show_datetime";
        
//         try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(query)) {
//             pstmt.setInt(1, movieId);
//             pstmt.setTimestamp(2, Timestamp.valueOf(currentTime));
            
//             ResultSet rs = pstmt.executeQuery();
//             while (rs.next()) {
//                 Movie movie = movieDAO.getMovieById(rs.getInt("movie_id"));
//                 MovieHall hall = new MovieHall(MovieHallName.valueOf(rs.getString("hall_name")));
                
//                 // Create MovieTimings object using the constructor that matches your class
//                 MovieTimings timing = new MovieTimings(
//                     rs.getInt("id"),
//                     movie,
//                     hall,
//                     rs.getTimestamp("show_datetime").toLocalDateTime(),
//                     rs.getDouble("price")
//                 );
                
//                 timings.add(timing);
//             }
//         } catch (SQLException e) {
//             System.err.println("Error getting available timings: " + e.getMessage());
//             e.printStackTrace();
//         }
//         return timings;
//     }

//     public MovieTimings getTimingById(int timingId) {
//         String query = "SELECT * FROM movie_timings WHERE id = ?";
//         try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(query)) {
//             pstmt.setInt(1, timingId);
//             ResultSet rs = pstmt.executeQuery();
            
//             if (rs.next()) {
//                 Movie movie = movieDAO.getMovieById(rs.getInt("movie_id"));
//                 MovieHall hall = new MovieHall(MovieHallName.valueOf(rs.getString("hall_name")));
                
//                 return new MovieTimings(
//                     rs.getInt("id"),
//                     movie,
//                     hall,
//                     rs.getTimestamp("show_datetime").toLocalDateTime(),
//                     rs.getDouble("price")
//                 );
//             }
//         } catch (SQLException e) {
//             System.err.println("Error getting timing by ID: " + e.getMessage());
//             e.printStackTrace();
//         }
//         return null;
//     }

//     // Additional utility method to check if a timing exists and is available
//     public boolean isTimingAvailable(int timingId) {
//         String query = "SELECT show_datetime FROM movie_timings WHERE id = ? " +
//                       "AND show_datetime > NOW() AND DATE(show_datetime) = CURDATE()";
                      
//         try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(query)) {
//             pstmt.setInt(1, timingId);
//             ResultSet rs = pstmt.executeQuery();
//             return rs.next(); // Returns true if timing exists and is available
//         } catch (SQLException e) {
//             System.err.println("Error checking timing availability: " + e.getMessage());
//             e.printStackTrace();
//             return false;
//         }
//     }

//     // Method to get all available timings for the current day (might be useful for admin view)
//     public List<MovieTimings> getAllAvailableTimingsForToday() {
//         List<MovieTimings> timings = new ArrayList<>();
//         LocalDateTime currentTime = LocalDateTime.now();
        
//         String query = "SELECT * FROM movie_timings " +
//                       "WHERE show_datetime > ? AND DATE(show_datetime) = CURDATE() " +
//                       "ORDER BY show_datetime";
        
//         try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(query)) {
//             pstmt.setTimestamp(1, Timestamp.valueOf(currentTime));
            
//             ResultSet rs = pstmt.executeQuery();
//             while (rs.next()) {
//                 Movie movie = movieDAO.getMovieById(rs.getInt("movie_id"));
//                 MovieHall hall = new MovieHall(MovieHallName.valueOf(rs.getString("hall_name")));
                
//                 MovieTimings timing = new MovieTimings(
//                     rs.getInt("id"),
//                     movie,
//                     hall,
//                     rs.getTimestamp("show_datetime").toLocalDateTime(),
//                     rs.getDouble("price")
//                 );
                
//                 timings.add(timing);
//             }
//         } catch (SQLException e) {
//             System.err.println("Error getting all available timings: " + e.getMessage());
//             e.printStackTrace();
//         }
//         return timings;
//     }
// }



package backend.database;

import backend.Entity.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieTimingsDAO {
    private DatabaseConnection dbConnection;
    private MovieDAO movieDAO;

    public MovieTimingsDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.movieDAO = new MovieDAO();
    }

    public List<MovieTimings> getAvailableTimingsForMovie(int movieId) {
        System.out.println("=== MovieTimingsDAO Debug ===");
        System.out.println("Querying for Movie ID: " + movieId);
        
        List<MovieTimings> timings = new ArrayList<>();
        
        // First get the movie object
        Movie movie = movieDAO.getMovieById(movieId);
        if (movie == null) {
            System.err.println("Movie not found for ID: " + movieId);
            return timings;
        }

        String query = "SELECT * FROM movie_timings WHERE movie_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, movieId);
            System.out.println("Executing query: " + pstmt.toString());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    try {
                        int id = rs.getInt("id");
                        String hallName = rs.getString("hall_name");
                        Timestamp showTime = rs.getTimestamp("show_datetime");
                        double price = rs.getDouble("price");
                        
                        System.out.println(String.format(
                            "Row Data - ID: %d, Hall: %s, DateTime: %s, Price: %.2f",
                            id, hallName, showTime, price
                        ));

                        MovieHall hall = new MovieHall(MovieHallName.valueOf(hallName));
                        MovieTimings timing = new MovieTimings(
                            id,
                            movie,
                            hall,
                            showTime.toLocalDateTime(),
                            price
                        );
                        
                        timings.add(timing);
                        System.out.println("Successfully created timing object");
                        
                    } catch (Exception e) {
                        System.err.println("Error creating timing object: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Returning " + timings.size() + " timings");
        return timings;
    }
}
