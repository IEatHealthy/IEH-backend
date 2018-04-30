package info.ieathealthy.models;
import org.bson.types.ObjectId;

public class Title {
    private ObjectId _id;
    private String title;
    private String description;
    private boolean awardOnSignUp;

    public Title(){
        //public 0-param constructor
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAwardOnSignUp() {
        return awardOnSignUp;
    }

    public void setAwardOnSignUp(boolean awardOnSignUp) {
        this.awardOnSignUp = awardOnSignUp;
    }
}
