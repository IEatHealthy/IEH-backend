package info.ieathealthy.models;
import org.bson.types.*;

public class Badge {
    private ObjectId _id;
    private int badgeId;
    private String decsription;
    private boolean awardOnSignup;

    public Badge(){
        //public 0-param constructor
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public int getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(int badgeId) {
        this.badgeId = badgeId;
    }

    public String getDecsription() {
        return decsription;
    }

    public void setDecsription(String decsription) {
        this.decsription = decsription;
    }

    public boolean isAwardOnSignup() {
        return awardOnSignup;
    }

    public void setAwardOnSignup(boolean awardOnSignup) {
        this.awardOnSignup = awardOnSignup;
    }
}
