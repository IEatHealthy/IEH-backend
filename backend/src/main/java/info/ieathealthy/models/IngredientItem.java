package info.ieathealthy.models;

import org.bson.types.ObjectId;

class IngredientItem {
    private ObjectId ingredientId;
    private String unitOfMeasure;
    private double amount;

    public IngredientItem() {}

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