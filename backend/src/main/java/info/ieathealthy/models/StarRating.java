package info.ieathealthy.models;

public enum StarRating {
    ONESTAR(1),
    TWOSTAR(2),
    THREESTAR(3),
    FOURSTAR(4),
    FIVESTAR(5),
    INVALID(50);

    private int starRating;

    private StarRating(int rating) { this.starRating= rating; }

    public void setStarRating(int rating) { this.starRating = rating; }
    public int getStarRating() { return starRating; }
}
