package info.ieathealthy.models;

import org.bson.types.ObjectId;

import java.util.ArrayList;

public class ProtectedFullyPopulatedUser {
    private String username;
    private int skillLevel;
    private ArrayList<Badge> badgesEarned;
    private ArrayList<Title> titlesEarned;
    private ArrayList<String> recipesCreated;
    private ArrayList<String> bookmarkedRecipes;
    private Badge badgeSelected;
    private Title titleSelected;

    public ProtectedFullyPopulatedUser(User u){
        this.username = u.getUsername();
        this.skillLevel = u.getSkillLevel();

        this.recipesCreated = new ArrayList<>();
        this.bookmarkedRecipes = new ArrayList<>();

        ArrayList<ObjectId> rc = u.getRecipesCreated();
        ArrayList<ObjectId> br = u.getBookmarkedRecipes();

        if(rc != null){
            for(ObjectId oi: rc){
                this.recipesCreated.add(oi.toString());
            }
        }

        if(br != null){
            for(ObjectId oi: br){
                this.bookmarkedRecipes.add(oi.toString());
            }
        }

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

    public ArrayList<String> getRecipesCreated() {
        return recipesCreated;
    }

    public void setRecipesCreated(ArrayList<String> recipesCreated) {
        this.recipesCreated = recipesCreated;
    }

    public ArrayList<String> getBookmarkedRecipes() {
        return bookmarkedRecipes;
    }

    public void setBookmarkedRecipes(ArrayList<String> bookmarkedRecipes) {
        this.bookmarkedRecipes = bookmarkedRecipes;
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
}
