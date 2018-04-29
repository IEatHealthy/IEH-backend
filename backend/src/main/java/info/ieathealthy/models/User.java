package info.ieathealthy.models;

import org.bson.types.ObjectId;
import java.util.ArrayList;

public class User {
    private ObjectId _id;
    private String email;
    private String firstName;
    private String lastName;
    private String hash;
    private ArrayList<ObjectId> recipesCreated;

    public User(String email, String firstName, String lastName, String hash, ArrayList<ObjectId> recipesCreated) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.hash = hash;
        this.recipesCreated = recipesCreated;
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

    public ArrayList<ObjectId> getRecipesCreated() { return recipesCreated; }

    public void setRecipesCreated(ArrayList<ObjectId> recipesCreated) { this.recipesCreated = recipesCreated; }

}
