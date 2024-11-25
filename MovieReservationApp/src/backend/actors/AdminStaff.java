package backend.actors;

public class AdminStaff {
    private final String username;
    private final String password;
    
    public AdminStaff(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public boolean authenticate(String attemptedUsername, String attemptedPassword) {
        return this.username.equals(attemptedUsername) && 
               this.password.equals(attemptedPassword);
    }
}