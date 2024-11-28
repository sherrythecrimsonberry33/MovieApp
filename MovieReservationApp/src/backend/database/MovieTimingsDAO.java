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
