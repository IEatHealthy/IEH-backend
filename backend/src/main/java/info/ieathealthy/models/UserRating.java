package info.ieathealthy.models;

import org.bson.types.ObjectId;

public class UserRating {
    private String userEmail;
    private int userRating;

    public UserRating() {}

    public UserRating(String userEmail, int userRating) {
        this.userEmail = userEmail;
        this.userRating = userRating;
    }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public int getUserRating() { return userRating; }
    public void setUserRating(int userRating) { this.userRating = userRating; }
    /*
    public void setUserRating(int userRating) {
        switch(userRating){
            case 1: this.userRating = StarRating.ONESTAR;
                break;
            case 2: this.userRating = StarRating.TWOSTAR;
                break;
            case 3: this.userRating = StarRating.THREESTAR;
                break;
            case 4: this.userRating = StarRating.FOURSTAR;
                break;
            case 5: this.userRating = StarRating.FIVESTAR;
                break;
            default: this.userRating = StarRating.INVALID;
        }
    }
    */
}
