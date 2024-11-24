package MovieReservationApp.tests;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.Arrays;
import java.util.List;

import backend.Entity.MovieHall;
import backend.Entity.MovieHallName;
import backend.Entity.Seat;
import backend.Entity.TheatreInfo;

class TheaterTests {
    private TheatreInfo theatre;
    private MovieHall movieHall;

    @BeforeEach
    void setUp() {
        theatre = TheatreInfo.getInstance();
        movieHall = theatre.getMovieHall(MovieHallName.IMAX_SUPREME);
    }

    @Test
    void testTheaterSingleton() {
        TheatreInfo anotherInstance = TheatreInfo.getInstance();
        assertSame(theatre, anotherInstance);
    }

    @Test
    void testTheaterBasicInfo() {
        assertEquals("AcmePlex", theatre.getName());
        assertTrue(theatre.isWheelchairAccessible());
        assertEquals(12, theatre.getNumberOfHalls());
    }

    @Test
    void testMovieHallCreation() {
        assertEquals(MovieHallName.IMAX_SUPREME, movieHall.getHallName());
        assertEquals(10, movieHall.getRows());
        assertEquals(12, movieHall.getColumns());
        assertEquals(120, movieHall.getTotalSeats());
    }

    @Test
    void testSeatBooking() {
        Seat seat = movieHall.getSeat(0, 0);
        assertTrue(seat.book());
        assertTrue(seat.isBooked());
        assertFalse(seat.book()); // Cannot book already booked seat
    }

    @Test
    void testSeatCancellation() {
        Seat seat = movieHall.getSeat(0, 0);
        seat.book();
        seat.cancelBooking();
        assertFalse(seat.isBooked());
        assertTrue(seat.book()); // Can book after cancellation
    }

    @Test
    void testSeatLabels() {
        Seat seat = movieHall.getSeat(0, 0);
        assertEquals("A1", seat.getSeatLabel());
        
        seat = movieHall.getSeat(1, 1);
        assertEquals("B2", seat.getSeatLabel());
    }

    @Test
    void testValidSeatSelection() {
        List<Seat> consecutiveSeats = Arrays.asList(
            movieHall.getSeat(0, 0),
            movieHall.getSeat(0, 1),
            movieHall.getSeat(0, 2)
        );
        assertTrue(movieHall.validateSeatSelection(consecutiveSeats));
    }

    @Test
    void testInvalidSeatSelection() {
        // Test seats with gap
        List<Seat> seatsWithGap = Arrays.asList(
            movieHall.getSeat(0, 0),
            movieHall.getSeat(0, 2)
        );
        assertFalse(movieHall.validateSeatSelection(seatsWithGap));

        // Test seats in different rows
        List<Seat> seatsInDifferentRows = Arrays.asList(
            movieHall.getSeat(0, 0),
            movieHall.getSeat(1, 1)
        );
        assertFalse(movieHall.validateSeatSelection(seatsInDifferentRows));
    }

    @Test
    void testInvalidSeatAccess() {
        assertThrows(IllegalArgumentException.class, () -> {
            movieHall.getSeat(-1, 0);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            movieHall.getSeat(0, 12);
        });
    }
}