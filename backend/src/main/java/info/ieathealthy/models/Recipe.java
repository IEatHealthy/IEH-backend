package info.ieathealthy.models;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import java.util.ArrayList;


//This class is like the original recipe. It can be used for anything except
//getting an entire recipe from the front end with the image data included as a string.

public class Recipe extends IncompleteRecipe {

    protected Binary foodImage;


    public Recipe() {}

    public Recipe (ObjectId _id, String name, int typeOfFood, int difficulty, double servings, double prepTime, double cookTime,
                   double readyInTime, ArrayList<IngredientItem> ingredients, ArrayList<String> steps, ArrayList<String> toolsNeeded,
                   String description, String author, double calories, double protein, double fat, double carbohydrate,
                   double fiber, double sugar, double calcium, double iron, double potassium, double sodium, double vitaminC,
                   double vitAiu, double vitDiu, double cholestrol, Binary foodImage){

        this._id = _id;
        this.name = name;
        this.typeOfFood = typeOfFood;
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

    public Binary getFoodImage() { return foodImage; }
    public void setFoodImage(Binary foodImage) { this.foodImage = foodImage; }

    //Function to update a recipe. Name and image of the food cannot be updated so don't take those values as parameters.
    public void updateRecipe(int typeOfFood, int difficulty, double servings, double prepTime, double cookTime, double readyInTime, ArrayList<IngredientItem> ingredients,
                             ArrayList<String> steps, ArrayList<String> toolsNeeded, String description, String author) {
        this.typeOfFood = typeOfFood;
        this.servings = servings;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.readyInTime = readyInTime;
        this.ingredients = ingredients;
        this.steps = steps;
        this.toolsNeeded = toolsNeeded;
        this.description = description;
        this.author = author;

        setDifficulty(difficulty);
    }
}
