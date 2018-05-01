package info.ieathealthy.models;

import org.bson.types.ObjectId;

public class UserRating {
    private String email;
    private StarRating userRating;

    public UserRating() {}

    public UserRating(String email, StarRating userRating) {
        this.email = email;
        this.userRating = userRating;
    }

    public String getUserEmail() { return email; }
    public void setUserEmail(String email) { this.email = email; }
    public int getUserRating() { return userRating.getStarRating(); }
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
}
