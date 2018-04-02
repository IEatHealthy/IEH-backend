package info.ieathealthy.models;

import org.bson.types.ObjectId;

public class Ingredient {
    private ObjectId _id;
    private String nbdNo;
    private String shrtDesc;
    private String gmWt1;
    private String gmWtDesc1;
    private String gmWt2;
    private String gmWtDesc2;
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

    public Ingredient() {}

    public Ingredient(String shrt_Desc, String gmWt1, String gmWt_Desc1, String gmWt2, String gmWt_Desc2, double calories, double protein, double fat, double carbohydrate, double fiber, double sugar, double calcium, double iron, double potassium, double sodium, double vitamin_C, double vitAiu, double vitDiu, double cholestrol) {
        shrtDesc = shrt_Desc;
        this.gmWt1 = gmWt1;
        gmWtDesc1 = gmWt_Desc1;
        this.gmWt2 = gmWt2;
        gmWtDesc2 = gmWt_Desc2;
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
        vitaminC = vitamin_C;
        this.vitAiu = vitAiu;
        this.vitDiu = vitDiu;
        this.cholestrol = cholestrol;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getNbdNo() {
        return nbdNo;
    }

    public void setNbdNo(String nbdNo) {
        this.nbdNo = nbdNo;
    }

    public String getShrtDesc() {
        return shrtDesc;
    }

    public void setShrtDesc(String shrtDesc) {
        this.shrtDesc = shrtDesc;
    }

    public String getGmWt1() {
        return gmWt1;
    }

    public void setGmWt1(String gmWt1) {
        this.gmWt1 = gmWt1;
    }

    public String getGmWtDesc1() {
        return gmWtDesc1;
    }

    public void setGmWtDesc1(String gmWtDesc1) {
        this.gmWtDesc1 = gmWtDesc1;
    }

    public String getGmWt2() {
        return gmWt2;
    }

    public void setGmWt2(String gmWt2) {
        this.gmWt2 = gmWt2;
    }

    public String getGmWtDesc2() {
        return gmWtDesc2;
    }

    public void setGmWtDesc2(String gmWtDesc2) {
        this.gmWtDesc2 = gmWtDesc2;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getCarbohydrate() {
        return carbohydrate;
    }

    public void setCarbohydrate(double carbohydrate) {
        this.carbohydrate = carbohydrate;
    }

    public double getFiber() {
        return fiber;
    }

    public void setFiber(double fiber) {
        this.fiber = fiber;
    }

    public double getSugar() {
        return sugar;
    }

    public void setSugar(double sugar) {
        this.sugar = sugar;
    }

    public double getCalcium() {
        return calcium;
    }

    public void setCalcium(double calcium) {
        this.calcium = calcium;
    }

    public double getIron() {
        return iron;
    }

    public void setIron(double iron) {
        this.iron = iron;
    }

    public double getPotassium() {
        return potassium;
    }

    public void setPotassium(double potassium) {
        this.potassium = potassium;
    }

    public double getSodium() {
        return sodium;
    }

    public void setSodium(double sodium) {
        this.sodium = sodium;
    }

    public double getVitaminC() {
        return vitaminC;
    }

    public void setVitaminC(double vitaminC) {
        this.vitaminC = vitaminC;
    }

    public double getVitAiu() {
        return vitAiu;
    }

    public void setVitAiu(double vitAiu) {
        this.vitAiu = vitAiu;
    }

    public double getVitDiu() {
        return vitDiu;
    }

    public void setVitDiu(double vitDiu) {
        this.vitDiu = vitDiu;
    }

    public double getCholestrol() {
        return cholestrol;
    }

    public void setCholestrol(double cholestrol) {
        this.cholestrol = cholestrol;
    }
}
