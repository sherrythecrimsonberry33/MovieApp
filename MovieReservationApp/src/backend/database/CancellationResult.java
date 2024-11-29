package backend.database;

public enum CancellationResult {
    SUCCESS,
    SUCCESS_EMAIL_FAILED, // New enum value
    NO_BOOKING_FOUND,
    CANCELLATION_NOT_ALLOWED,
    ERROR
}
