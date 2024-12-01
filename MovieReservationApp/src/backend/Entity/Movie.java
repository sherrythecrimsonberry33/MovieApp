package backend.Entity;

import java.util.Objects;

public class Movie {
    private Integer id;
    private String title;
    private String posterUrl;
    private String genre;
    private String synopsis;
    private double rating;
    private String ageRating;
    private Integer duration;  // in minutes

    // Constructor with all fields except ID (for new movies)
    public Movie(String title, String posterUrl, String genre, String synopsis, 
                double rating, String ageRating, Integer duration) {
        setTitle(title);
        setPosterUrl(posterUrl);
        setGenre(genre);
        setSynopsis(synopsis);
        setRating(rating);
        setAgeRating(ageRating);
        setDuration(duration);
    }

    // Constructor with ID (for existing movies from database)
    public Movie(Integer id, String title, String posterUrl, String genre, 
                String synopsis, double rating, String ageRating, Integer duration) {
        this(title, posterUrl, genre, synopsis, rating, ageRating, duration);
        this.id = id;
    }

    // Getters and Setters with validation
    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (title.length() > 100) {
            throw new IllegalArgumentException("Title cannot exceed 100 characters");
        }
        this.title = title.trim();
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        if (posterUrl == null || posterUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Poster URL cannot be empty");
        }
        if (posterUrl.length() > 255) {
            throw new IllegalArgumentException("Poster URL cannot exceed 255 characters");
        }
        this.posterUrl = posterUrl.trim();
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            throw new IllegalArgumentException("Genre cannot be empty");
        }
        if (genre.length() > 50) {
            throw new IllegalArgumentException("Genre cannot exceed 50 characters");
        }
        this.genre = genre.trim();
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        if (synopsis == null || synopsis.trim().isEmpty()) {
            throw new IllegalArgumentException("Synopsis cannot be empty");
        }
        this.synopsis = synopsis.trim();
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        if (rating < 0.0 || rating > 10.0) {
            throw new IllegalArgumentException("Rating must be between 0 and 10");
        }
        // Round to one decimal place
        this.rating = Math.round(rating * 10.0) / 10.0;
    }

    public String getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(String ageRating) {
        if (ageRating == null || ageRating.trim().isEmpty()) {
            throw new IllegalArgumentException("Age rating cannot be empty");
        }
        if (ageRating.length() > 10) {
            throw new IllegalArgumentException("Age rating cannot exceed 10 characters");
        }
        this.ageRating = ageRating.trim();
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        if (duration == null || duration <= 0) {
            throw new IllegalArgumentException("Duration must be a positive number");
        }
        this.duration = duration;
    }

    // Utility method to format duration as hours and minutes
    public String getFormattedDuration() {
        int hours = duration / 60;
        int minutes = duration % 60;
        return hours > 0 ? 
            String.format("%dh %02dm", hours, minutes) : 
            String.format("%dm", minutes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie)) return false;
        Movie movie = (Movie) o;
        return Objects.equals(id, movie.id) &&
               Objects.equals(title, movie.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s", 
            title, 
            ageRating, 
            getFormattedDuration());
    }
}