package info.ieathealthy.models;

import org.bson.types.ObjectId;

public class UserRating {
    private ObjectId userId;
    private StarRating userRating;

    public UserRating() {}

    public UserRating(ObjectId userId, StarRating userRating) {
        this.userId = userId;
        this.userRating = userRating;
    }

    public ObjectId getUserId() { return userId; }
    public void setUserId(ObjectId userId) { this.userId = userId; }
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
            default: System.out.println("error in setUserRating()");
        }

    }
}
