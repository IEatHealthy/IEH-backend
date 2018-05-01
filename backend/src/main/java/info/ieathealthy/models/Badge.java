package info.ieathealthy.models;
import org.bson.types.*;

public class Badge {
    private ObjectId _id;
    private int badgeId;
    private String description;
    private boolean awardOnSignup;

    public Badge(){
        //public 0-param constructor
    }

    public ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId _id) {
        this._id = _id;
    }

    public int getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(int badgeId) {
        this.badgeId = badgeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAwardOnSignup() {
        return awardOnSignup;
    }

    public void setAwardOnSignup(boolean awardOnSignup) {
        this.awardOnSignup = awardOnSignup;
    }
}
