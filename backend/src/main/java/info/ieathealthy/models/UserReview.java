package info.ieathealthy.models;

import org.bson.types.ObjectId;

public class UserReview {
    private ObjectId userId;
    private String userReview;

    public UserReview() {}

    public UserReview(ObjectId userId, String userReview){
        this.userId = userId;
        this.userReview = userReview;
    }

    public ObjectId getUserId() { return userId; }
    public void setUserId(ObjectId userId) { this.userId = userId; }
    public String getUserReview() { return userReview; }
    public void setUserReview(String userReview) { this.userReview = userReview; }
}
