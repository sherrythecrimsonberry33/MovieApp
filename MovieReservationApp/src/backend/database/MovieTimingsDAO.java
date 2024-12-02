

package backend.database;

import backend.Entity.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MovieTimingsDAO {
    private DatabaseConnection dbConnection;
    private MovieDAO movieDAO;

    public MovieTimingsDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.movieDAO = new MovieDAO();
    }

    public List<MovieTimings> getAvailableTimingsForMovie(int movieId, LocalDate showDate) {
        System.out.println("=== MovieTimingsDAO Debug ===");
        System.out.println("Querying for Movie ID: " + movieId + " on Date: " + showDate);
        
        List<MovieTimings> timings = new ArrayList<>();
        
        // First get the movie object
        Movie movie = movieDAO.getMovieById(movieId);
        if (movie == null) {
            System.err.println("Movie not found for ID: " + movieId);
            return timings;
        }

        String query = "SELECT * FROM movie_timings WHERE movie_id = ? AND show_date = ? AND (show_date > CURDATE() OR (show_date = CURDATE() AND show_time >= CURTIME())) ORDER BY show_time";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, movieId);
            pstmt.setDate(2, Date.valueOf(showDate));
            System.out.println("Executing query: " + pstmt.toString());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    try {
                        int id = rs.getInt("id");
                        String hallName = rs.getString("hall_name");
                        Date showDateSql = rs.getDate("show_date");
                        Time showTimeSql = rs.getTime("show_time");
                        double price = rs.getDouble("price");
                        
                        System.out.println(String.format(
                            "Row Data - ID: %d, Hall: %s, Date: %s, Time: %s, Price: %.2f",
                            id, hallName, showDateSql, showTimeSql, price
                        ));

                        // Convert SQL Date and Time to Java LocalDate and LocalTime
                        LocalDate localShowDate = showDateSql.toLocalDate();
                        LocalTime localShowTime = showTimeSql.toLocalTime();

                        MovieHall hall = new MovieHall(MovieHallName.valueOf(hallName));
                        MovieTimings timing = new MovieTimings(
                            id,
                            movie,
                            hall,
                            localShowDate,
                            localShowTime,
                            price
                        );

                        // Combine showDate and showTime to LocalDateTime for comparison
                        LocalDateTime showDateTime = LocalDateTime.of(localShowDate, localShowTime);

                        // Check if the show time is after the current time
                        if (showDateTime.isAfter(LocalDateTime.now())) {
                            timings.add(timing);
                            System.out.println("Successfully created timing object");
                        }
                        
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

    public List<LocalDate> getAvailableDatesForMovie(int movieId) {
        List<LocalDate> dates = new ArrayList<>();
        String query = "SELECT DISTINCT show_date FROM movie_timings WHERE movie_id = ? AND show_date >= CURDATE() ORDER BY show_date";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, movieId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Date date = rs.getDate("show_date");
                    dates.add(date.toLocalDate());
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        return dates;
    }
}
