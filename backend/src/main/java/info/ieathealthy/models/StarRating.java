package info.ieathealthy.models;

public enum StarRating {
    ONESTAR(1),
    TWOSTAR(2),
    THREESTAR(3),
    FOURSTAR(4),
    FIVESTAR(5);

    private int rating;

    private StarRating(int rating) { this.rating = rating; }

    public void setRating(int rating) { this.rating = rating; }
    public int getRating() { return rating; }
}
