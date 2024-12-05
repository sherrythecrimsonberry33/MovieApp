import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import backend.Entity.Movie;
import backend.Entity.MovieHall;
import backend.Entity.MovieHallName;
import backend.Entity.MovieTimings;

public class MovieTimingsTest {
    
    @Test
    public void testRegisteredUserSeatReservation() {
        // Create test movie
        Movie testMovie = new Movie(
            1,
            "Test Movie",
            "test.jpg",
            "Action",
            "Test synopsis",
            8.0,
            "PG-13",
            120
        );
        
        // Create movie hall (10 rows x 12 columns = 120 seats)
        MovieHall testHall = new MovieHall(MovieHallName.PLATINUM);
        
        // Create movie timing
        LocalDate showDate = LocalDate.now().plusDays(1);
        LocalTime showTime = LocalTime.of(14, 0); // 2 PM show
        MovieTimings timing = new MovieTimings(testMovie, testHall, showDate, showTime, 15.0);
        
        // Calculate expected number of reserved seats (10%)
        int totalSeats = testHall.getRows() * testHall.getColumns(); // 120 seats
        int expectedReservedSeats = (totalSeats * 10) / 100; // Should be 12 seats
        
        // Test booking seats as guest during early access
        List<String> middleSeats = Arrays.asList("E6", "E7", "E8", "E9"); // Middle row seats
        boolean guestBookingResult = timing.bookSeats(middleSeats, "Guest");
        
        // Guest booking should fail during early access
        assertFalse("Guest should not be able to book reserved seats", guestBookingResult);
        
        // Test booking same seats as registered user
        boolean ruBookingResult = timing.bookSeats(middleSeats, "Registered");
        
        // Registered user booking should succeed
        assertTrue("Registered user should be able to book reserved seats", ruBookingResult);
        
        // Verify seat count
        int availableSeats = timing.getAvailableSeatsCount();
        assertEquals("Should have expected number of available seats", 
                    totalSeats - middleSeats.size(), availableSeats);
        
        // Test individual seat availability
        assertFalse("Booked seat should not be available", timing.isSeatAvailable("E6"));
        assertTrue("Unreserved seat should be available", timing.isSeatAvailable("A1"));
    }
}