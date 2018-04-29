package info.ieathealthy.models;

import org.bson.types.ObjectId;
import info.ieathealthy.models.ClientUser;
import java.util.ArrayList;

public class User {
    private ObjectId _id;
    private String email;
    private String firstName;
    private String lastName;
    private String hash;
    private String username;
    private int skillLevel;
    private ArrayList<ObjectId> recipesCreated;

    public User(String email, String firstName, String lastName, String hash, ArrayList<ObjectId> recipesCreated) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.hash = hash;
        this.username = username;
        this.skillLevel = skillLevel;
        this.recipesCreated = recipesCreated;
    }

    public User(ClientUser cu, String hash){
        this.hash = hash;
        this.email = cu.getEmail();
        this.firstName = cu.getFirstName();
        this.lastName = cu.getLastName();
        this.username = cu.getUsername();
        this.skillLevel = cu.getSkillLevel();
    }

    public User(){
        //empty 0-param constructor
    }

    public ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId _id) {
        this._id = _id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getUsername() {
        return username;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSkillLevel(int skillLevel) {
        this.skillLevel = skillLevel;
    }
  
    public ArrayList<ObjectId> getRecipesCreated() { return recipesCreated; }

    public void setRecipesCreated(ArrayList<ObjectId> recipesCreated) { this.recipesCreated = recipesCreated; }

}
