package backend.database;

import backend.Entity.Movie;
import backend.Entity.MovieHallName;
import java.sql.*;
import java.time.LocalDateTime;


public class AdminDAO {
    // Additional hall names for new movies
    private static final MovieHallName[] ADDITIONAL_HALLS = {
        MovieHallName.PLATINUM,
        MovieHallName.GOLD_CLASS
    };
    
    private static final LocalDateTime[] DEFAULT_TIMES = {
        LocalDateTime.of(2024, 11, 25, 10, 0),
        LocalDateTime.of(2024, 11, 25, 13, 0),
        LocalDateTime.of(2024, 11, 25, 16, 0),
        LocalDateTime.of(2024, 11, 25, 19, 0),
        LocalDateTime.of(2024, 11, 25, 22, 0)
    };
    
    private static final double[] DEFAULT_PRICES = {15.0, 15.0, 15.0, 18.0, 18.0};

    public boolean addMovie(Movie movie) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            
            // Insert movie
            String insertMovie = "INSERT INTO movies (title, poster_url, genre, synopsis, rating, age_rating, duration) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            ps = conn.prepareStatement(insertMovie, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, movie.getTitle());
            ps.setString(2, movie.getPosterUrl());
            ps.setString(3, movie.getGenre());
            ps.setString(4, movie.getSynopsis());
            ps.setDouble(5, movie.getRating());
            ps.setString(6, movie.getAgeRating());
            ps.setInt(7, movie.getDuration());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int movieId = rs.getInt(1);
                    // Add movie timings for additional halls
                    return addMovieTimings(conn, movieId);
                }
            }
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private boolean addMovieTimings(Connection conn, int movieId) throws SQLException {
        String insertTiming = "INSERT INTO movie_timings (movie_id, hall_name, show_datetime, price) " +
                            "VALUES (?, ?, ?, ?)";
                            
        for (MovieHallName hall : ADDITIONAL_HALLS) {
            for (int i = 0; i < DEFAULT_TIMES.length; i++) {
                PreparedStatement ps = conn.prepareStatement(insertTiming);
                ps.setInt(1, movieId);
                ps.setString(2, hall.name());
                ps.setTimestamp(3, Timestamp.valueOf(DEFAULT_TIMES[i]));
                ps.setDouble(4, DEFAULT_PRICES[i]);
                ps.executeUpdate();
                ps.close();
            }
        }
        return true;
    }
    
    public boolean updateMovie(Movie movie) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            String updateQuery = "UPDATE movies SET title=?, poster_url=?, genre=?, synopsis=?, " +
                               "rating=?, age_rating=?, duration=? WHERE id=?";
                               
            ps = conn.prepareStatement(updateQuery);
            ps.setString(1, movie.getTitle());
            ps.setString(2, movie.getPosterUrl());
            ps.setString(3, movie.getGenre());
            ps.setString(4, movie.getSynopsis());
            ps.setDouble(5, movie.getRating());
            ps.setString(6, movie.getAgeRating());
            ps.setInt(7, movie.getDuration());
            ps.setInt(8, movie.getId());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean deleteMovie(int movieId) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            
            // First delete related movie timings
            String deleteTimings = "DELETE FROM movie_timings WHERE movie_id = ?";
            ps = conn.prepareStatement(deleteTimings);
            ps.setInt(1, movieId);
            ps.executeUpdate();
            ps.close();
            
            // Then delete the movie
            String deleteMovie = "DELETE FROM movies WHERE id = ?";
            ps = conn.prepareStatement(deleteMovie);
            ps.setInt(1, movieId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}