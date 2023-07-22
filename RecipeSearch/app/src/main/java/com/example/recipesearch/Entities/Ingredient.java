package com.example.recipesearch.Entities;

public class Ingredient {
    private String text;
    private double quantity;
    private String measure;
    private String food;
    private String foodId;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }
}
