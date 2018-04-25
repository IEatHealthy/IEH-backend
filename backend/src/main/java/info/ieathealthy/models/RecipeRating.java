package info.ieathealthy.models;

import org.bson.types.ObjectId;
import java.util.ArrayList;

public class RecipeRating {
    private ObjectId _id;
    private ArrayList<UserRating> ratings;
    private double totalRating;

    public RecipeRating() {}

    public RecipeRating(ObjectId _id, ArrayList<UserRating> ratings, double totalRating){
        this._id = _id;
        this.ratings = ratings;
        this.totalRating = totalRating;
    }

    public ObjectId getId() { return _id; }
    public void setId(ObjectId _id) { this._id = _id; }
    public ArrayList<UserRating> getRatings() { return ratings; }
    public void setRatings(ArrayList<UserRating> ratings) { this.ratings = ratings; }
    public double getTotalRating() { return totalRating; }
    public void setTotalRating(double totalRating) { this.totalRating = totalRating; }
}
