package info.ieathealthy.restcontrollers;

import org.bson.types.ObjectId;

public class Ingredient {
    private ObjectId _id;
    private String Shrt_Desc;
    private String GmWt1;
    private String GmWt_Desc1;
    private String GmWt2;
    private String GmWt_Desc2;
    private double Calories;
    private double Protein;
    private double Fat;
    private double Carbohydrate;
    private double Fiber;
    private double Sugar;
    private double Calcium;
    private double Iron;
    private double Potassium;
    private double Sodium;
    private double Vitamin_C;
    private double Vit_A_IU;
    private double Vit_D_IU;
    private double Cholestrol;

    public Ingredient() {}

    public Ingredient(String shrt_Desc, String gmWt1, String gmWt_Desc1, String gmWt2, String gmWt_Desc2, double calories, double protein, double fat, double carbohydrate, double fiber, double sugar, double calcium, double iron, double potassium, double sodium, double vitamin_C, double vit_A_IU, double vit_D_IU, double cholestrol) {
        Shrt_Desc = shrt_Desc;
        GmWt1 = gmWt1;
        GmWt_Desc1 = gmWt_Desc1;
        GmWt2 = gmWt2;
        GmWt_Desc2 = gmWt_Desc2;
        Calories = calories;
        Protein = protein;
        Fat = fat;
        Carbohydrate = carbohydrate;
        Fiber = fiber;
        Sugar = sugar;
        Calcium = calcium;
        Iron = iron;
        Potassium = potassium;
        Sodium = sodium;
        Vitamin_C = vitamin_C;
        Vit_A_IU = vit_A_IU;
        Vit_D_IU = vit_D_IU;
        Cholestrol = cholestrol;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getShrt_Desc() {
        return Shrt_Desc;
    }

    public void setShrt_Desc(String shrt_Desc) {
        Shrt_Desc = shrt_Desc;
    }

    public String getGmWt1() {
        return GmWt1;
    }

    public void setGmWt1(String gmWt1) {
        GmWt1 = gmWt1;
    }

    public String getGmWt_Desc1() {
        return GmWt_Desc1;
    }

    public void setGmWt_Desc1(String gmWt_Desc1) {
        GmWt_Desc1 = gmWt_Desc1;
    }

    public String getGmWt2() {
        return GmWt2;
    }

    public void setGmWt2(String gmWt2) {
        GmWt2 = gmWt2;
    }

    public String getGmWt_Desc2() {
        return GmWt_Desc2;
    }

    public void setGmWt_Desc2(String gmWt_Desc2) {
        GmWt_Desc2 = gmWt_Desc2;
    }

    public double getCalories() {
        return Calories;
    }

    public void setCalories(double calories) {
        Calories = calories;
    }

    public double getProtein() {
        return Protein;
    }

    public void setProtein(double protein) {
        Protein = protein;
    }

    public double getFat() {
        return Fat;
    }

    public void setFat(double fat) {
        Fat = fat;
    }

    public double getCarbohydrate() {
        return Carbohydrate;
    }

    public void setCarbohydrate(double carbohydrate) {
        Carbohydrate = carbohydrate;
    }

    public double getFiber() {
        return Fiber;
    }

    public void setFiber(double fiber) {
        Fiber = fiber;
    }

    public double getSugar() {
        return Sugar;
    }

    public void setSugar(double sugar) {
        Sugar = sugar;
    }

    public double getCalcium() {
        return Calcium;
    }

    public void setCalcium(double calcium) {
        Calcium = calcium;
    }

    public double getIron() {
        return Iron;
    }

    public void setIron(double iron) {
        Iron = iron;
    }

    public double getPotassium() {
        return Potassium;
    }

    public void setPotassium(double potassium) {
        Potassium = potassium;
    }

    public double getSodium() {
        return Sodium;
    }

    public void setSodium(double sodium) {
        Sodium = sodium;
    }

    public double getVitamin_C() {
        return Vitamin_C;
    }

    public void setVitamin_C(double vitamin_C) {
        Vitamin_C = vitamin_C;
    }

    public double getVit_A_IU() {
        return Vit_A_IU;
    }

    public void setVit_A_IU(double vit_A_IU) {
        Vit_A_IU = vit_A_IU;
    }

    public double getVit_D_IU() {
        return Vit_D_IU;
    }

    public void setVit_D_IU(double vit_D_IU) {
        Vit_D_IU = vit_D_IU;
    }

    public double getCholestrol() {
        return Cholestrol;
    }

    public void setCholestrol(double cholestrol) {
        Cholestrol = cholestrol;
    }
}
