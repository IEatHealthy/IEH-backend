package info.ieathealthy.models;
import org.bson.types.Binary;
import java.util.ArrayList;
import java.util.Base64;
import javax.xml.bind.DatatypeConverter;



//This class takes a recipe from the front end. Specifically used to create a recipe.
//The image data is accepted as a string and this class meets that requirement.

public class FrontRecipe extends IncompleteRecipe {

    private Binary foodImage;


    public FrontRecipe() {}

    public FrontRecipe (String name, int typeOfFood, int difficulty, double servings, double prepTime, double cookTime, double readyInTime, ArrayList<IngredientItem> ingredients,
                   ArrayList<String> steps, ArrayList<String> toolsNeeded, String description, String author, double calories, double protein,
                   double fat, double carbohydrate, double fiber, double sugar, double calcium, double iron, double potassium, double sodium,
                   double vitaminC, double vitAiu, double vitDiu, double cholestrol, String foodImage){
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

        setFoodImage(foodImage);
        setDifficulty(difficulty);

    }

    public Binary getFoodImage() { return foodImage; }
    //Image data should be passed as a base64string. That is then decoded into a byte[] to create Binary value.
    //If passed as anything else, the Binary data will still be valid but will be inconsistent with
    //how other images are being stored.
    //public void setFoodImage(String foodImage) { this.foodImage = new Binary(Base64.getDecoder().decode(foodImage.getBytes())); }
    public void setFoodImage(String foodImage) { this.foodImage = new Binary(DatatypeConverter.parseBase64Binary(foodImage)); }
}
