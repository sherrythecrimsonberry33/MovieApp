// package backend.database;

// import backend.Entity.Seat;
// import java.sql.*;
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;


// public class SeatBookingDAO {
//     private DatabaseConnection dbConnection;

//     public SeatBookingDAO() {
//         this.dbConnection = DatabaseConnection.getInstance();
//     }

//     public List<Seat> getBookedSeatsForTiming(int movieTimingId) {
//         List<Seat> bookedSeats = new ArrayList<>();
//         String query = "SELECT seat_row, seat_column FROM seat_bookings WHERE movie_timing_id = ? AND status = 'active' ";
        
//         try (Connection conn = dbConnection.getConnection();
//              PreparedStatement ps = conn.prepareStatement(query)) {
            
//             ps.setInt(1, movieTimingId);
//             ResultSet rs = ps.executeQuery();
            
//             while (rs.next()) {
//                 Seat seat = new Seat(rs.getInt("seat_row"), rs.getInt("seat_column"));
//                 seat.book(); // Mark as booked
//                 bookedSeats.add(seat);
//             }
//         } catch (SQLException e) {
//             throw new RuntimeException("Error fetching booked seats", e);
//         }
//         return bookedSeats;
//     }

//     public boolean bookSeats(int movieTimingId, List<Seat> seats, String ticketId) {
//         String query = "INSERT INTO seat_bookings (movie_timing_id, seat_row, seat_column, ticket_id) VALUES (?, ?, ?, ?)";
        
//         try (Connection conn = dbConnection.getConnection()) {
//             conn.setAutoCommit(false);
            
//             try (PreparedStatement ps = conn.prepareStatement(query)) {
//                 for (Seat seat : seats) {
//                     ps.setInt(1, movieTimingId);
//                     ps.setInt(2, seat.getRow());
//                     ps.setInt(3, seat.getColumn());
//                     ps.setString(4, ticketId);
//                     ps.addBatch();
//                 }
                
//                 int[] results = ps.executeBatch();
//                 conn.commit();
                
//                 return results.length == seats.size();
//             } catch (SQLException e) {
//                 conn.rollback();
//                 throw e;
//             }
//         } catch (SQLException e) {
//             throw new RuntimeException("Error booking seats", e);
//         }
//     }

//     // This method can be used for both cancellations and to check if seats are still available
//     public boolean areSeatsFree(int movieTimingId, List<Seat> seats) {
//         String query = "SELECT COUNT(*) FROM seat_bookings " +
//                       "WHERE movie_timing_id = ? AND seat_row = ? AND seat_column = ? AND status = 'active'";
        
//         try (Connection conn = dbConnection.getConnection();
//              PreparedStatement ps = conn.prepareStatement(query)) {
            
//             for (Seat seat : seats) {
//                 ps.setInt(1, movieTimingId);
//                 ps.setInt(2, seat.getRow());
//                 ps.setInt(3, seat.getColumn());
                
//                 ResultSet rs = ps.executeQuery();
//                 if (rs.next() && rs.getInt(1) > 0) {
//                     return false; // Seat is already booked
//                 }
//             }
//             return true; // All seats are free
//         } catch (SQLException e) {
//             throw new RuntimeException("Error checking seat availability", e);
//         }
//     }

//     // public boolean cancelBooking(String ticketId) {
//     //     String query = "DELETE FROM seat_bookings WHERE ticket_id = ?";
        
//     //     try (Connection conn = dbConnection.getConnection();
//     //          PreparedStatement ps = conn.prepareStatement(query)) {
            
//     //         ps.setString(1, ticketId);
//     //         int rowsAffected = ps.executeUpdate();
//     //         return rowsAffected > 0;
            
//     //     } catch (SQLException e) {
//     //         throw new RuntimeException("Error canceling seat bookings", e);
//     //     }
//     // }

//     public CancellationResult cancelBooking(String ticketId) {
//         String selectQuery = "SELECT sb.movie_timing_id, mt.show_date, mt.show_time " +
//                              "FROM seat_bookings sb " +
//                              "JOIN movie_timings mt ON sb.movie_timing_id = mt.id " +
//                              "WHERE sb.ticket_id = ? AND sb.status = 'active' LIMIT 1";
//         String updateQuery = "UPDATE seat_bookings SET status = 'cancelled' WHERE ticket_id = ?";
        
//         try (Connection conn = dbConnection.getConnection();
//              PreparedStatement selectPs = conn.prepareStatement(selectQuery)) {
            
//             selectPs.setString(1, ticketId);
//             ResultSet rs = selectPs.executeQuery();
            
//             if (rs.next()) {
//                 Date showDate = rs.getDate("show_date");
//                 Time showTime = rs.getTime("show_time");
//                 LocalDateTime showDateTime = LocalDateTime.of(showDate.toLocalDate(), showTime.toLocalTime());
                
//                 // Check if cancellation is allowed (e.g., more than 2 hours before showtime)
//                 if (showDateTime.isBefore(LocalDateTime.now().plusHours(2))) {
//                     // Cancellation not allowed
//                     return CancellationResult.CANCELLATION_NOT_ALLOWED;
//                 }
                
//                 // Proceed to cancel booking
//                 try (PreparedStatement updatePs = conn.prepareStatement(updateQuery)) {
//                     updatePs.setString(1, ticketId);
//                     int rowsAffected = updatePs.executeUpdate();
//                     if (rowsAffected > 0) {
//                         return CancellationResult.SUCCESS;
//                     } else {
//                         return CancellationResult.ERROR;
//                     }
//                 }
//             } else {
//                 // No active booking found with the provided ticketId
//                 return CancellationResult.NO_BOOKING_FOUND;
//             }
            
//         } catch (SQLException e) {
//             e.printStackTrace();
//             return CancellationResult.ERROR;
//         }
//     }
    


//     public List<Seat> getSeatsForTicket(String ticketId) {
//         List<Seat> seats = new ArrayList<>();
//         String query = "SELECT seat_row, seat_column FROM seat_bookings WHERE ticket_id = ?";
        
//         try (Connection conn = dbConnection.getConnection();
//              PreparedStatement ps = conn.prepareStatement(query)) {
            
//             ps.setString(1, ticketId);
//             ResultSet rs = ps.executeQuery();
            
//             while (rs.next()) {
//                 seats.add(new Seat(rs.getInt("seat_row"), rs.getInt("seat_column")));
//             }
            
//         } catch (SQLException e) {
//             throw new RuntimeException("Error fetching seats for ticket", e);
//         }
//         return seats;
//     }
// }


package backend.database;
import backend.Entity.Seat;
import backend.Entity.receipt.SendEmail;
import backend.actors.GuestUser;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SeatBookingDAO {
    private DatabaseConnection dbConnection;
    private SendEmail sendEmailService;

    public SeatBookingDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.sendEmailService = new SendEmail(); // Initialize your SendEmail class
    }

    public List<Seat> getBookedSeatsForTiming(int movieTimingId) {
        List<Seat> bookedSeats = new ArrayList<>();
        String query = "SELECT seat_row, seat_column FROM seat_bookings WHERE movie_timing_id = ? AND status = 'active'";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, movieTimingId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Seat seat = new Seat(rs.getInt("seat_row"), rs.getInt("seat_column"));
                seat.book(); // Mark as booked
                bookedSeats.add(seat);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching booked seats", e);
        }
        return bookedSeats;
    }

    public boolean bookSeats(int movieTimingId, List<Seat> seats, String ticketId, double pricePerSeat) {
        String query = "INSERT INTO seat_bookings (movie_timing_id, seat_row, seat_column, ticket_id, status, price) VALUES (?, ?, ?, ?, 'active', ?)";
        
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                for (Seat seat : seats) {
                    ps.setInt(1, movieTimingId);
                    ps.setInt(2, seat.getRow());
                    ps.setInt(3, seat.getColumn());
                    ps.setString(4, ticketId);
                    ps.setDouble(5, pricePerSeat);
                    ps.addBatch();
                }
                
                int[] results = ps.executeBatch();
                conn.commit();
                
                return results.length == seats.size();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error booking seats", e);
        }
    }

    // This method can be used for both cancellations and to check if seats are still available
    public boolean areSeatsFree(int movieTimingId, List<Seat> seats) {
        String query = "SELECT COUNT(*) FROM seat_bookings " +
                      "WHERE movie_timing_id = ? AND seat_row = ? AND seat_column = ? AND status = 'active'";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            for (Seat seat : seats) {
                ps.setInt(1, movieTimingId);
                ps.setInt(2, seat.getRow());
                ps.setInt(3, seat.getColumn());
                
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    return false; // Seat is already booked
                }
            }
            return true; // All seats are free
        } catch (SQLException e) {
            throw new RuntimeException("Error checking seat availability", e);
        }
    }


    public List<Seat> getSeatsForTicket(String ticketId) {
        List<Seat> seats = new ArrayList<>();
        String query = "SELECT seat_row, seat_column FROM seat_bookings WHERE ticket_id = ? AND status = 'active'";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, ticketId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                seats.add(new Seat(rs.getInt("seat_row"), rs.getInt("seat_column")));
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching seats for ticket", e);
        }
        return seats;
    }

    // public CancellationResult cancelBooking(String ticketId) {
    //     String selectQuery = "SELECT sb.movie_timing_id, mt.show_date, mt.show_time " +
    //                         "FROM seat_bookings sb " +
    //                         "JOIN movie_timings mt ON sb.movie_timing_id = mt.id " +
    //                         "WHERE sb.ticket_id = ? AND sb.status = 'active' LIMIT 1";
    //     String updateQuery = "UPDATE seat_bookings SET status = 'cancelled' WHERE ticket_id = ?";
        
    //     try (Connection conn = dbConnection.getConnection();
    //         PreparedStatement selectPs = conn.prepareStatement(selectQuery)) {
            
    //         selectPs.setString(1, ticketId);
    //         ResultSet rs = selectPs.executeQuery();
            
    //         if (rs.next()) {
    //             Date showDate = rs.getDate("show_date");
    //             Time showTime = rs.getTime("show_time");
    //             LocalDateTime showDateTime = LocalDateTime.of(showDate.toLocalDate(), showTime.toLocalTime());
                
    //             // Check if cancellation is allowed (e.g., more than 2 hours before showtime)
    //             if (showDateTime.isBefore(LocalDateTime.now().plusHours(2))) {
    //                 // Cancellation not allowed
    //                 return CancellationResult.CANCELLATION_NOT_ALLOWED;
    //             }
                
    //             // Proceed to cancel booking
    //             try (PreparedStatement updatePs = conn.prepareStatement(updateQuery)) {
    //                 updatePs.setString(1, ticketId);
    //                 int rowsAffected = updatePs.executeUpdate();
    //                 if (rowsAffected > 0) {
    //                     return CancellationResult.SUCCESS;
    //                 } else {
    //                     return CancellationResult.ERROR;
    //                 }
    //             }
    //         } else {
    //             // No active booking found with the provided ticketId
    //             return CancellationResult.NO_BOOKING_FOUND;
    //         }
            
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //         return CancellationResult.ERROR;
    //     }

 

    //Cancels a booking based on the ticket ID and sends a cancellation email.
//     public CancellationResult cancelBooking(String ticketId, String userEmail) {
//         String selectQuery = "SELECT sb.movie_timing_id, mt.show_date, mt.show_time, m.title " +
//                              "FROM seat_bookings sb " +
//                              "JOIN movie_timings mt ON sb.movie_timing_id = mt.id " +
//                              "JOIN movies m ON mt.movie_id = m.id " +
//                              "WHERE sb.ticket_id = ? AND sb.status = 'active' LIMIT 1";
//         String updateQuery = "UPDATE seat_bookings SET status = 'cancelled' WHERE ticket_id = ?";

//         try (Connection conn = dbConnection.getConnection();
//              PreparedStatement selectPs = conn.prepareStatement(selectQuery)) {

//             selectPs.setString(1, ticketId);
//             ResultSet rs = selectPs.executeQuery();

//             if (rs.next()) {
//                 Date showDate = rs.getDate("show_date");
//                 Time showTime = rs.getTime("show_time");
//                 LocalDateTime showDateTime = LocalDateTime.of(showDate.toLocalDate(), showTime.toLocalTime());

       
//                 if (showDateTime.isBefore(LocalDateTime.now().plusHours(72))) {
//                     // Cancellation not allowed
//                     return CancellationResult.CANCELLATION_NOT_ALLOWED;
//                 }

//                 // Proceed to cancel booking
//                 try (PreparedStatement updatePs = conn.prepareStatement(updateQuery)) {
//                     updatePs.setString(1, ticketId);
//                     int rowsAffected = updatePs.executeUpdate();
//                     if (rowsAffected > 0) {
//                         // Send simple cancellation email
//                         try {
//                             sendEmailService.sendCancellationEmail(userEmail);
//                         } catch (Exception e) {
//                             e.printStackTrace();
//                             // Even if email fails, consider the cancellation successful
//                             return CancellationResult.SUCCESS_EMAIL_FAILED;
//                         }

//                         return CancellationResult.SUCCESS;
//                     } else {
//                         return CancellationResult.ERROR;
//                     }
//                 }
//             } else {
//                 // No active booking found with the provided ticketId
//                 return CancellationResult.NO_BOOKING_FOUND;
//             }

//         } catch (SQLException e) {
//             e.printStackTrace();
//             return CancellationResult.ERROR;
//         }
//     }
// }

public CancellationResult cancelBooking(String ticketId, String userEmail) {
    String selectQuery = 
        "SELECT sb.movie_timing_id, mt.show_date, mt.show_time, m.title, " +
        "SUM(sb.price) as total_amount, ru.email as registered_email " +
        "FROM seat_bookings sb " +
        "JOIN movie_timings mt ON sb.movie_timing_id = mt.id " +
        "JOIN movies m ON mt.movie_id = m.id " +
        "LEFT JOIN registered_users ru ON ru.email = ? " +
        "WHERE sb.ticket_id = ? AND sb.status = 'active' " +
        "GROUP BY sb.movie_timing_id, mt.show_date, mt.show_time, m.title, ru.email";

    try (Connection conn = dbConnection.getConnection();
         PreparedStatement selectPs = conn.prepareStatement(selectQuery)) {

        selectPs.setString(1, userEmail);
        selectPs.setString(2, ticketId);
        ResultSet rs = selectPs.executeQuery();

        if (rs.next()) {
            Date showDate = rs.getDate("show_date");
            Time showTime = rs.getTime("show_time");
            LocalDateTime showDateTime = LocalDateTime.of(showDate.toLocalDate(), showTime.toLocalTime());
            double totalAmount = rs.getDouble("total_amount");
            String registeredEmail = rs.getString("registered_email");
            boolean isRegistered = registeredEmail != null;

            // Check if cancellation is allowed (72 hours before showtime)
            if (showDateTime.isBefore(LocalDateTime.now().plusHours(72))) {
                return CancellationResult.CANCELLATION_NOT_ALLOWED;
            }

            // Calculate refund amount
            double refundAmount = isRegistered ? 
                totalAmount : // Full refund for registered users
                totalAmount * (1 - GuestUser.CANCELLATION_FEE_PERCENTAGE);

            // Update booking status
            String updateQuery = "UPDATE seat_bookings SET status = 'cancelled' WHERE ticket_id = ?";
            try (PreparedStatement updatePs = conn.prepareStatement(updateQuery)) {
                updatePs.setString(1, ticketId);
                int rowsAffected = updatePs.executeUpdate();

                if (rowsAffected > 0) {
                    try {
                        // Send email with refund details - fixed method call
                        sendEmailService.sendCancellationEmailWithRefund(
                            userEmail,
                            ticketId,
                            totalAmount,
                            refundAmount,
                            isRegistered
                        );
                        return CancellationResult.SUCCESS;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return CancellationResult.SUCCESS_EMAIL_FAILED;
                    }
                }
            }
        }
        return CancellationResult.NO_BOOKING_FOUND;

    } catch (SQLException e) {
        e.printStackTrace();
        return CancellationResult.ERROR;
    }
}
}