package info.ieathealthy.models;

import org.bson.types.ObjectId;
import java.util.ArrayList;

//Might want to put these in a different package so that the rest of the package doesn't have access to it.


//Contains everything except for the image. Image data must be accepted as a string
//so extended two other classes from this one - one that accepts a string for the image
//data and the other accepts a Binary value.

public class IncompleteRecipe {
    protected ObjectId _id;
    protected String name;
    protected int typeOfFood;
    protected DifficultyType difficulty;
    protected double servings;
    protected double prepTime;
    protected double cookTime;
    protected double readyInTime;
    protected ArrayList<IngredientItem> ingredients;
    protected ArrayList<String> steps;
    protected ArrayList<String> toolsNeeded;
    protected String description;
    protected String author;
    protected double calories;
    protected double protein;
    protected double fat;
    protected double carbohydrate;
    protected double fiber;
    protected double sugar;
    protected double calcium;
    protected double iron;
    protected double potassium;
    protected double sodium;
    protected double vitaminC;
    protected double vitAiu;
    protected double vitDiu;
    protected double cholestrol;

    private enum DifficultyType {
        INVALID(-1),
        EASY(1),
        INTERMEDIATE(2),
        HARD(3);

        private int difficulty;

        private DifficultyType(int difficulty)
        {
            this.difficulty = difficulty;
        }

        public int getDifficulty(){
            return difficulty;
        }
    }


    public IncompleteRecipe() {}

    public ObjectId getId() {return _id;}
    public void setId(ObjectId _id) { this._id = _id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getTypeOfFood() { return typeOfFood; }
    public void setTypeOfFood(int typeOfFood) { this.typeOfFood = typeOfFood; }
    public int getDifficulty() { return difficulty.getDifficulty(); }
    public double getServings() { return servings; }
    public void setServings(double servings) { this.servings = servings;}
    public double getPrepTime() { return prepTime; }
    public void setPrepTime(double prepTime) { this.prepTime = prepTime; }
    public double getCookTime() { return cookTime; }
    public void setCookTime(double cookTime) { this.cookTime = cookTime; }
    public double getReadyInTime() { return readyInTime; }
    public void setReadyInTime(double readyInTime) { this.readyInTime = readyInTime; }
    public ArrayList<IngredientItem> getIngredients() { return ingredients; }
    public void setIngredients(ArrayList<IngredientItem> ingredients) { this.ingredients = ingredients; }
    public ArrayList<String> getSteps() { return steps; }
    public void setSteps(ArrayList<String> steps) { this.steps = steps; }
    public ArrayList<String> getToolsNeeded() { return toolsNeeded; }
    public void setToolsNeeded(ArrayList<String> toolsNeeded) { this.toolsNeeded = toolsNeeded; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public double getCalories() { return calories; }
    public void setCalories(double calories) { this.calories = calories; }
    public double getProtein() { return protein; }
    public void setProtein(double protein) { this.protein = protein; }
    public double getFat() { return fat; }
    public void setFat(double fat) { this.fat = fat; }
    public double getCarbohydrate() { return carbohydrate; }
    public void setCarbohydrate(double carbohydrate) { this.carbohydrate = carbohydrate; }
    public double getFiber() { return fiber; }
    public void setFiber(double fiber) { this.fiber = fiber; }
    public double getSugar() { return sugar; }
    public void setSugar(double sugar) { this.sugar = sugar; }
    public double getCalcium() { return calcium; }
    public void setCalcium(double calcium) { this.calcium = calcium; }
    public double getIron() { return iron; }
    public void setIron(double iron) { this.iron = iron; }
    public double getPotassium() { return potassium; }
    public void setPotassium(double potassium) { this.potassium = potassium; }
    public double getSodium() { return sodium; }
    public void setSodium(double sodium) { this.sodium = sodium; }
    public double getVitaminC() { return vitaminC; }
    public void setVitaminC(double vitaminC) { this.vitaminC = vitaminC; }
    public double getVitAiu() { return vitAiu; }
    public void setVitAiu(double vitAiu) { this.vitAiu = vitAiu; }
    public double getVitDiu() { return vitDiu; }
    public void setVitDiu(double vitDiu) { this.vitDiu = vitDiu; }
    public double getCholestrol() { return cholestrol; }
    public void setCholestrol(double cholestrol) { this.cholestrol = cholestrol; }


    public void setDifficulty(int difficulty) {
        switch(difficulty){
            case 1: this.difficulty = DifficultyType.EASY;
                break;
            case 2: this.difficulty = DifficultyType.INTERMEDIATE;
                break;
            case 3: this.difficulty = DifficultyType.HARD;
                break;
            default: this.difficulty = DifficultyType.INVALID;
                break;
        }
    }
}
