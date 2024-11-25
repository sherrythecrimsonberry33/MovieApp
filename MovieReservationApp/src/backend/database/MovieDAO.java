package backend.database;


import java.sql.*;
import java.util.*;
import backend.Entity.Movie;

public class MovieDAO {
    private static final String SELECT_ALL_MOVIES = "SELECT * FROM movies";
    private static final String SELECT_MOVIE_BY_ID = "SELECT * FROM movies WHERE id = ?";
    private static final String INSERT_MOVIE = 
        "INSERT INTO movies (title, poster_url, genre, synopsis, rating, age_rating, duration) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_MOVIE = 
        "UPDATE movies SET title=?, poster_url=?, genre=?, synopsis=?, rating=?, age_rating=?, " +
        "duration=? WHERE id=?";
    private static final String DELETE_MOVIE = "DELETE FROM movies WHERE id=?";

    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_MOVIES)) {
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                movies.add(mapResultSetToMovie(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching movies", e);
        }
        return movies;
    }

    public Movie getMovieById(Integer id) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_MOVIE_BY_ID)) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToMovie(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching movie by ID", e);
        }
        return null;
    }

    public void saveMovie(Movie movie) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 movie.getId() == null ? INSERT_MOVIE : UPDATE_MOVIE,
                 PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            setMovieParameters(ps, movie);
            
            if (movie.getId() != null) {
                ps.setInt(8, movie.getId());
            }
            
            ps.executeUpdate();
            
            // Only for new movies
            if (movie.getId() == null) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        // Create a new Movie instance with the generated ID since Movie is immutable
                        Movie updatedMovie = new Movie(
                            rs.getInt(1),
                            movie.getTitle(),
                            movie.getPosterUrl(),
                            movie.getGenre(),
                            movie.getSynopsis(),
                            movie.getRating(),
                            movie.getAgeRating(),
                            movie.getDuration()
                        );
                        // You might want to assign this updated movie back to wherever it's being used
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving movie", e);
        }
    }

    public void deleteMovie(Integer id) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_MOVIE)) {
            
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting movie", e);
        }
    }

    private Movie mapResultSetToMovie(ResultSet rs) throws SQLException {
        return new Movie(
            rs.getInt("id"),           // Add this line to include the ID
            rs.getString("title"),
            rs.getString("poster_url"),
            rs.getString("genre"),
            rs.getString("synopsis"),
            rs.getDouble("rating"),
            rs.getString("age_rating"),
            rs.getInt("duration")
        );
    }

    private void setMovieParameters(PreparedStatement ps, Movie movie) throws SQLException {
        ps.setString(1, movie.getTitle());
        ps.setString(2, movie.getPosterUrl());
        ps.setString(3, movie.getGenre());
        ps.setString(4, movie.getSynopsis());
        ps.setDouble(5, movie.getRating());
        ps.setString(6, movie.getAgeRating());
        ps.setInt(7, movie.getDuration());
    }
}