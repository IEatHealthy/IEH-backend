package info.ieathealthy.models;

import org.bson.types.ObjectId;
import org.bson.types.Binary;
import java.util.List;



class IngredientItem {
    private ObjectId ingredientId;
    private String unitOfMeasure;
    private double amount;

    public IngredientItem(ObjectId ingredientId, String unitOfMeasure, double amount){
        this.ingredientId = ingredientId;
        this.unitOfMeasure = unitOfMeasure;
        this.amount = amount;
    }

    public ObjectId getIngredientId() { return ingredientId; }
    public void setIngredientId(ObjectId ingredientId) { this.ingredientId = ingredientId; }
    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}


public class Recipe {
    private String name;
    private DifficultyType difficulty;
    private double servings;
    private double prepTime;
    private double cookTime;
    private double readyInTime;
    private List<IngredientItem> ingredients;
    private List<String> steps;
    private List<String> toolsNeeded;
    private String description;
    private String author;
    private double calories;
    private double protein;
    private double fat;
    private double carbohydrate;
    private double fiber;
    private double sugar;
    private double calcium;
    private double iron;
    private double potassium;
    private double sodium;
    private double vitaminC;
    private double vitAiu;
    private double vitDiu;
    private double cholestrol;
    private Binary foodImage;

    public Recipe() {}

    public Recipe (String name, int difficulty, double servings, double prepTime, double cookTime, double readyInTime, List<IngredientItem> ingredients,
                   List<String> steps, List<String> toolsNeeded, String description, String author, double calories, double protein,
                   double fat, double carbohydrate, double fiber, double sugar, double calcium, double iron, double potassium, double sodium,
                   double vitaminC, double vitAiu, double vitDiu, double cholestrol, Binary foodImage){
        this.name = name;
        this.servings = servings;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.readyInTime = readyInTime;
        this.ingredients = ingredients;
        this.steps = steps;
        this.toolsNeeded = toolsNeeded;
        this.description = description;
        this.author = author;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrate = carbohydrate;
        this.fiber = fiber;
        this.sugar = sugar;
        this.calcium = calcium;
        this.iron = iron;
        this.potassium = potassium;
        this.sodium = sodium;
        this.vitaminC = vitaminC;
        this.vitAiu = vitAiu;
        this.vitDiu = vitDiu;
        this.cholestrol = cholestrol;
        this.foodImage = foodImage;

        setDifficulty(difficulty);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getDifficulty() { return difficulty.getDifficulty(); }
    public double getServings() { return servings; }
    public void setServings(double servings) { this.servings = servings;}
    public double getPrepTime() { return prepTime; }
    public void setPrepTime(double prepTime) { this.prepTime = prepTime; }
    public double setCookTime() { return cookTime; }
    public void getCookTime(double cookTime) { this.cookTime = cookTime; }
    public double getReadyInTime() { return readyInTime; }
    public void setReadyInTime(double readyInTime) { this.readyInTime = readyInTime; }
    public List<IngredientItem> getIngredients() { return ingredients; }
    public void setIngredients(List<IngredientItem> ingredients) {this.ingredients = ingredients; }
    public List<String> getSteps() { return steps; }
    public void setSteps(List<String> steps) { this.steps = steps; }
    public List<String> getToolsNeeded() { return toolsNeeded; }
    public void setToolsNeeded(List<String> tooldNeeded) { this.toolsNeeded = toolsNeeded; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public double getCalories() { return calories; }
    public void setCalories(double calories) { this.calories = calories; }
    public double getProtein() { return protein; }
    public void setProtein(double protein) { this.protein = protein; }
    public double getFat() { return fat; }
    public void setFat(double fat) { this.fat = fat; }
    public double getCarbohydrate() { return carbohydrate; }
    public void setCarbohyrdate(double carbohydrate) { this.carbohydrate = carbohydrate; }
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
    public Binary getFoodImage() { return foodImage; }
    public void setFoodImage(Binary foodImage) { this.foodImage = foodImage; }


    public void setDifficulty(int difficulty) {
        switch(difficulty){
            case 1: this.difficulty = DifficultyType.EASY;
                break;
            case 2: this.difficulty = DifficultyType.INTERMEDIATE;
                break;
            case 3: this.difficulty = DifficultyType.HARD;
                break;
            default: System.out.println("error in setDiffculty");
        }
    }

    private enum DifficultyType {
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


}
