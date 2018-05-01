package info.ieathealthy.models;

import org.bson.types.ObjectId;

public class UserReview {
    private String email;
    private String userReview;

    public UserReview() {}

    public UserReview(String email, String userReview){
        this.email = email;
        this.userReview = userReview;
    }

    public String getUserEmail() { return email; }
    public void setUserEmail(String email) { this.email = email; }
    public String getUserReview() { return userReview; }
    public void setUserReview(String userReview) { this.userReview = userReview; }
}
