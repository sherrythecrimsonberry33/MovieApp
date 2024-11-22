package backend.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TheatreInfo {
    private static final TheatreInfo instance = new TheatreInfo();
    private final String name = "AcmePlex";
    private final String address = "123 Cinema Boulevard, MovieCity, MC 12345";
    private final boolean wheelchairAccessible = true;
    private final int numberOfHalls = 12;
    private final List<MovieHall> movieHalls;

    private TheatreInfo() {
        this.movieHalls = initializeMovieHalls();
    }

    private List<MovieHall> initializeMovieHalls() {
        List<MovieHall> halls = new ArrayList<>();
        for (MovieHallName hallName : MovieHallName.values()) {
            halls.add(new MovieHall(hallName));
        }
        return Collections.unmodifiableList(halls);
    }

    public static TheatreInfo getInstance() {
        return instance;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public boolean isWheelchairAccessible() {
        return wheelchairAccessible;
    }

    public int getNumberOfHalls() {
        return numberOfHalls;
    }

    public List<MovieHall> getMovieHalls() {
        return movieHalls;
    }

    public MovieHall getMovieHall(MovieHallName name) {
        return movieHalls.stream()
                .filter(hall -> hall.getHallName() == name)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Hall not found"));
    }
}