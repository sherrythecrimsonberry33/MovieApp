

import backend.Entity.Movie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class MovieTest {
    private Movie movie;

    @BeforeEach
    public void setUp() {
        movie = new Movie(
            "Test Movie",
            "http://example.com/poster.jpg",
            "Action",
            "Test synopsis",
            7.5,
            "PG-13",
            120
        );
    }

    @Test
    public void testMovieCreation() {
        assertNotNull(movie);
        assertEquals("Test Movie", movie.getTitle());
        assertEquals("Action", movie.getGenre());
        assertEquals(7.5, movie.getRating(), 0.001); // delta for double comparison
        assertEquals("PG-13", movie.getAgeRating());
        assertEquals(120, movie.getDuration());
    }

    public @Test
    void testInvalidTitle() {
        assertThrows(IllegalArgumentException.class, () -> {
            movie.setTitle("");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            movie.setTitle(null);
        });

        String longTitle = "a".repeat(101);
        assertThrows(IllegalArgumentException.class, () -> {
            movie.setTitle(longTitle);
        });
    }

    @Test
    public void testInvalidRating() {
        assertThrows(IllegalArgumentException.class, () -> {
            movie.setRating(10.1);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            movie.setRating(-0.1);
        });

        // Test valid ratings
        movie.setRating(7.55);
        assertEquals(7.6, movie.getRating(), 0.001); // Should round to 7.6
        
        movie.setRating(7.54);
        assertEquals(7.5, movie.getRating(), 0.001); // Should round to 7.5
    }

    @Test
    public void testFormattedDuration() {
        assertEquals("2h 00m", movie.getFormattedDuration());
        
        Movie shortMovie = new Movie(
            "Short",
            "http://example.com/poster.jpg",
            "Action",
            "Synopsis",
            7.5,
            "PG",
            45
        );
        assertEquals("45m", shortMovie.getFormattedDuration());
    }

    @Test
    public void testMovieEquality() {
        Movie movie1 = new Movie(1, "Test Movie", "url", "Action", "Synopsis", 7.5, "PG-13", 120);
        Movie movie2 = new Movie(1, "Test Movie", "url", "Action", "Synopsis", 7.5, "PG-13", 120);
        Movie differentMovie = new Movie(2, "Different Movie", "url", "Action", "Synopsis", 7.5, "PG-13", 120);
        
        assertEquals(movie1, movie2);
        assertNotEquals(movie1, differentMovie);
        assertEquals(movie1.hashCode(), movie2.hashCode());
    }
}