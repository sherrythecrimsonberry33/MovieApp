package backend.Entity;

public enum MovieHallName {
    IMAX_SUPREME("IMAX Supreme"),
    DOLBY_ATMOS("Dolby Atmos"),
    STARLIGHT("Starlight"),
    GALAXY("Galaxy"),
    ROYAL_SCREEN("Royal Screen"),
    PLATINUM("Platinum"),
    GOLD_CLASS("Gold Class"),
    SILVER_SCREEN("Silver Screen"),
    PREMIERE("Premiere"),
    VIP_LOUNGE("VIP Lounge");


    private final String displayName;

    MovieHallName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}