// package backend.database;

// import backend.Entity.Movie;
// import backend.Entity.MovieHallName;
// import java.sql.*;
// import java.time.LocalDateTime;


// public class AdminDAO {
//     // Additional hall names for new movies
//     private static final MovieHallName[] ADDITIONAL_HALLS = {
//         MovieHallName.PLATINUM,
//         MovieHallName.GOLD_CLASS
//     };
    
//     private static final LocalDateTime[] DEFAULT_TIMES = {
//         LocalDateTime.of(2024, 11, 25, 10, 0),
//         LocalDateTime.of(2024, 11, 25, 13, 0),
//         LocalDateTime.of(2024, 11, 25, 16, 0),
//         LocalDateTime.of(2024, 11, 25, 19, 0),
//         LocalDateTime.of(2024, 11, 25, 22, 0)
//     };
    
//     private static final double[] DEFAULT_PRICES = {15.0, 15.0, 15.0, 18.0, 18.0};

//     public boolean addMovie(Movie movie) {
//         Connection conn = null;
//         PreparedStatement ps = null;
//         ResultSet rs = null;
        
//         try {
//             conn = DatabaseConnection.getInstance().getConnection();
            
//             // Insert movie
//             String insertMovie = "INSERT INTO movies (title, poster_url, genre, synopsis, rating, age_rating, duration) " +
//                                "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
//             ps = conn.prepareStatement(insertMovie, Statement.RETURN_GENERATED_KEYS);
//             ps.setString(1, movie.getTitle());
//             ps.setString(2, movie.getPosterUrl());
//             ps.setString(3, movie.getGenre());
//             ps.setString(4, movie.getSynopsis());
//             ps.setDouble(5, movie.getRating());
//             ps.setString(6, movie.getAgeRating());
//             ps.setInt(7, movie.getDuration());
            
//             int affectedRows = ps.executeUpdate();
            
//             if (affectedRows > 0) {
//                 rs = ps.getGeneratedKeys();
//                 if (rs.next()) {
//                     int movieId = rs.getInt(1);
//                     // Add movie timings for additional halls
//                     return addMovieTimings(conn, movieId);
//                 }
//             }
//             return false;
            
//         } catch (SQLException e) {
//             e.printStackTrace();
//             return false;
//         } finally {
//             // Close resources
//             try {
//                 if (rs != null) rs.close();
//                 if (ps != null) ps.close();
//             } catch (SQLException e) {
//                 e.printStackTrace();
//             }
//         }
//     }
    
//     private boolean addMovieTimings(Connection conn, int movieId) throws SQLException {
//         String insertTiming = "INSERT INTO movie_timings (movie_id, hall_name, show_datetime, price) " +
//                             "VALUES (?, ?, ?, ?)";
                            
//         for (MovieHallName hall : ADDITIONAL_HALLS) {
//             for (int i = 0; i < DEFAULT_TIMES.length; i++) {
//                 PreparedStatement ps = conn.prepareStatement(insertTiming);
//                 ps.setInt(1, movieId);
//                 ps.setString(2, hall.name());
//                 ps.setTimestamp(3, Timestamp.valueOf(DEFAULT_TIMES[i]));
//                 ps.setDouble(4, DEFAULT_PRICES[i]);
//                 ps.executeUpdate();
//                 ps.close();
//             }
//         }
//         return true;
//     }
    
//     public boolean updateMovie(Movie movie) {
//         Connection conn = null;
//         PreparedStatement ps = null;
        
//         try {
//             conn = DatabaseConnection.getInstance().getConnection();
//             String updateQuery = "UPDATE movies SET title=?, poster_url=?, genre=?, synopsis=?, " +
//                                "rating=?, age_rating=?, duration=? WHERE id=?";
                               
//             ps = conn.prepareStatement(updateQuery);
//             ps.setString(1, movie.getTitle());
//             ps.setString(2, movie.getPosterUrl());
//             ps.setString(3, movie.getGenre());
//             ps.setString(4, movie.getSynopsis());
//             ps.setDouble(5, movie.getRating());
//             ps.setString(6, movie.getAgeRating());
//             ps.setInt(7, movie.getDuration());
//             ps.setInt(8, movie.getId());
            
//             return ps.executeUpdate() > 0;
            
//         } catch (SQLException e) {
//             e.printStackTrace();
//             return false;
//         } finally {
//             try {
//                 if (ps != null) ps.close();
//             } catch (SQLException e) {
//                 e.printStackTrace();
//             }
//         }
//     }
    
//     public boolean deleteMovie(int movieId) {
//         Connection conn = null;
//         PreparedStatement ps = null;
        
//         try {
//             conn = DatabaseConnection.getInstance().getConnection();
            
//             // First delete related movie timings
//             String deleteTimings = "DELETE FROM movie_timings WHERE movie_id = ?";
//             ps = conn.prepareStatement(deleteTimings);
//             ps.setInt(1, movieId);
//             ps.executeUpdate();
//             ps.close();
            
//             // Then delete the movie
//             String deleteMovie = "DELETE FROM movies WHERE id = ?";
//             ps = conn.prepareStatement(deleteMovie);
//             ps.setInt(1, movieId);
            
//             return ps.executeUpdate() > 0;
            
//         } catch (SQLException e) {
//             e.printStackTrace();
//             return false;
//         } finally {
//             try {
//                 if (ps != null) ps.close();
//             } catch (SQLException e) {
//                 e.printStackTrace();
//             }
//         }
//     }
// }

package backend.database;

import backend.Entity.Movie;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class AdminDAO {

    //Adds a new movie and assigns it to either PLATINUM or GOLD_CLASS hall alternately.

    public boolean addMovie(Movie movie) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();

            // Get the current count of movies to determine hall assignment
            String countQuery = "SELECT COUNT(*) FROM movies";
            Statement stmt = conn.createStatement();
            ResultSet countRs = stmt.executeQuery(countQuery);
            int movieCount = 0;
            if (countRs.next()) {
                movieCount = countRs.getInt(1);
            }
            countRs.close();
            stmt.close();

            // Insert the new movie
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

                    // Determine the hall name based on the movie count
                    String hallName = (movieCount % 2 == 0) ? "PLATINUM" : "GOLD_CLASS";

                    // Generate movie timings for the assigned hall
                    return addMovieTimings(conn, movieId, hallName);
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
                if (conn != null) conn.close(); // Close the connection
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    //Generates showtimes for the next 14 days for the specified hall.

    private boolean addMovieTimings(Connection conn, int movieId, String hallName) throws SQLException {
        String insertTiming = "INSERT INTO movie_timings (movie_id, hall_name, show_date, show_time, price) " +
                              "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(insertTiming);

        LocalDate today = LocalDate.now();
        LocalTime[] showTimes = {
            LocalTime.of(10, 0),
            LocalTime.of(13, 0),
            LocalTime.of(16, 0),
            LocalTime.of(19, 0),
            LocalTime.of(22, 0)
        };

        double basePrice = hallName.equals("PLATINUM") ? 15.00 : 12.00;

        for (int day = 0; day < 14; day++) {
            LocalDate showDate = today.plusDays(day);
            for (LocalTime showTime : showTimes) {
                double price = basePrice;
                if (showTime.equals(LocalTime.of(19, 0)) || showTime.equals(LocalTime.of(22, 0))) {
                    price += 3.00; // Evening show surcharge
                }

                ps.setInt(1, movieId);
                ps.setString(2, hallName);
                ps.setDate(3, Date.valueOf(showDate));
                ps.setTime(4, Time.valueOf(showTime));
                ps.setDouble(5, price);
                ps.addBatch();
            }
        }

        ps.executeBatch();
        ps.close();
        return true;
    }


    //Retrieves all movies from the database.
 
    public ObservableList<Movie> getAllMovies() {
        ObservableList<Movie> movies = FXCollections.observableArrayList();
        String query = "SELECT * FROM movies";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
             
             while (rs.next()) {
                 Movie movie = new Movie(
                     rs.getInt("id"),
                     rs.getString("title"),
                     rs.getString("poster_url"),
                     rs.getString("genre"),
                     rs.getString("synopsis"),
                     rs.getDouble("rating"),
                     rs.getString("age_rating"),
                     rs.getInt("duration")
                 );
                 movies.add(movie);
             }
             
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        
        return movies;
    }


    //Updates an existing movie's details.

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
                if (conn != null) conn.close(); // Close the connection
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    //Deletes a movie and its associated timings from the database.

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
                if (conn != null) conn.close(); // Close the connection
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
