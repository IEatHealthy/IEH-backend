package info.ieathealthy.models;

import org.bson.types.ObjectId;

public class UserReview {
    private String userEmail;
    private String userReview;

    public UserReview() {}

    public UserReview(String userEmail, String userReview){
        this.userEmail = userEmail;
        this.userReview = userReview;
    }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getUserReview() { return userReview; }
    public void setUserReview(String userReview) { this.userReview = userReview; }
}
