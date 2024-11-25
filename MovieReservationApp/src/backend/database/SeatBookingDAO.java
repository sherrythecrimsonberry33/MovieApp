package backend.database;


import backend.Entity.Seat;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatBookingDAO {
    private DatabaseConnection dbConnection;

    public SeatBookingDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public List<Seat> getBookedSeatsForTiming(int movieTimingId) {
        List<Seat> bookedSeats = new ArrayList<>();
        String query = "SELECT seat_row, seat_column FROM seat_bookings WHERE movie_timing_id = ?";
        
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

    public boolean bookSeats(int movieTimingId, List<Seat> seats, String ticketId) {
        String query = "INSERT INTO seat_bookings (movie_timing_id, seat_row, seat_column, ticket_id) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                for (Seat seat : seats) {
                    ps.setInt(1, movieTimingId);
                    ps.setInt(2, seat.getRow());
                    ps.setInt(3, seat.getColumn());
                    ps.setString(4, ticketId);
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
                      "WHERE movie_timing_id = ? AND seat_row = ? AND seat_column = ?";
        
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

    public boolean cancelBooking(String ticketId) {
        String query = "DELETE FROM seat_bookings WHERE ticket_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, ticketId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error canceling seat bookings", e);
        }
    }

    public List<Seat> getSeatsForTicket(String ticketId) {
        List<Seat> seats = new ArrayList<>();
        String query = "SELECT seat_row, seat_column FROM seat_bookings WHERE ticket_id = ?";
        
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
}