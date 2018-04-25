package info.ieathealthy.models;

import org.bson.types.ObjectId;
import java.util.ArrayList;

public class Rating {
    private ObjectId _id;
    private ArrayList<UserRating> ratings;
    private int totalRating;
}
