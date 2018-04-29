package info.ieathealthy.models;
import info.ieathealthy.models.User;

public class ProtectedUser {
    private String firstName;
    private String lastName;
    private String username;
    private int skillLevel;

    public ProtectedUser(){
        //public 0-param constructor
    }

    public ProtectedUser(User u){
        this.firstName = u.getFirstName();
        this.lastName = u.getLastName();
        this.username = u.getUsername();
        this.skillLevel = u.getSkillLevel();
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(int skillLevel) {
        this.skillLevel = skillLevel;
    }
}
