package info.ieathealthy.models;

import org.bson.types.ObjectId;

import java.util.ArrayList;

public class FullyPopulatedUser {
    private String email;
    private String firstName;
    private String lastName;
    private String username;
    private int skillLevel;
    private ArrayList<Badge> badgesEarned;
    private ArrayList<Title> titlesEarned;
    private ArrayList<ObjectId> recipesCreated;
    private ArrayList<ObjectId> bookmarkedRecipes;
    private Badge badgeSelected;
    private Title titleSelected;

    public FullyPopulatedUser(User u){
        this.email = u.getEmail();
        this.firstName = u.getFirstName();
        this.lastName = u.getLastName();
        this.username = u.getUsername();
        this.skillLevel = u.getSkillLevel();
        this.bookmarkedRecipes = u.getBookmarkedRecipes();
        this.recipesCreated = u.getRecipesCreated();
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

    public ArrayList<Badge> getBadgesEarned() {
        return badgesEarned;
    }

    public void setBadgesEarned(ArrayList<Badge> badgesEarned) {
        this.badgesEarned = badgesEarned;
    }

    public ArrayList<Title> getTitlesEarned() {
        return titlesEarned;
    }

    public void setTitlesEarned(ArrayList<Title> titlesEarned) {
        this.titlesEarned = titlesEarned;
    }

    public Badge getBadgeSelected() {
        return badgeSelected;
    }

    public void setBadgeSelected(Badge badgeSelected) {
        this.badgeSelected = badgeSelected;
    }

    public Title getTitleSelected() {
        return titleSelected;
    }

    public void setTitleSelected(Title titleSelected) {
        this.titleSelected = titleSelected;
    }

    public ArrayList<ObjectId> getRecipesCreated() {
        return recipesCreated;
    }

    public void setRecipesCreated(ArrayList<ObjectId> recipesCreated) {
        this.recipesCreated = recipesCreated;
    }

    public ArrayList<ObjectId> getBookmarkedRecipes() {
        return bookmarkedRecipes;
    }

    public void setBookmarkedRecipes(ArrayList<ObjectId> bookmarkedRecipes) {
        this.bookmarkedRecipes = bookmarkedRecipes;
    }
}
