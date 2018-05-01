package info.ieathealthy.models;

import org.bson.types.ObjectId;

public class IngredientItem {
    private String desc;
    //private ObjectId ingredientId;
    private String unitOfMeasure;
    private double amount;

    public IngredientItem() {}

    public IngredientItem(String desc, String unitOfMeasure, double amount){
        this.desc = desc;
        this.unitOfMeasure = unitOfMeasure;
        this.amount = amount;
    }

    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }
    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}

/*
public class IngredientItem {
    private String desc;
    private ObjectId ingredientId;
    private String unitOfMeasure;
    private double amount;

    public IngredientItem() {}

    public IngredientItem(String desc, ObjectId ingredientId, String unitOfMeasure, double amount){
        this.desc = desc;
        this.ingredientId = ingredientId;
        this.unitOfMeasure = unitOfMeasure;
        this.amount = amount;
    }

    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }
    public ObjectId getIngredientId() { return ingredientId; }
    public void setIngredientId(ObjectId ingredientId) { this.ingredientId = ingredientId; }
    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}
*/
