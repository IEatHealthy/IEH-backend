package info.ieathealthy.models;

import org.bson.types.ObjectId;
import java.util.ArrayList;

public class Review {
    private ObjectId _id;
    private ArrayList<UserReview> reviews;

    public Review () {}

    public Review(ObjectId _id, ArrayList<UserReview> reviews){
        this._id = _id;
        this.reviews = reviews;
    }

    public ObjectId getId() { return _id; }
    public void setId(ObjectId _id) { this._id = _id; }
    public ArrayList<UserReview> getReviews() { return reviews; }
    public void setReviews(ArrayList<UserReview> reviews) { this.reviews = reviews; }
}
