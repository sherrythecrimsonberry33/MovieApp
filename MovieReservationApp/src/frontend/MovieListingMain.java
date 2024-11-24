package frontend;

import javafx.application.Application;

public class MovieListingMain {
    public static void main(String[] args) {
        try {
            Application.launch(MovieListingView.class, args);
        } catch (Exception e) {
            System.err.println("Failed to start application:");
            e.printStackTrace();
        }
    }
}