package com.example.recipesearch.Entities;

public class Recipe {
    private String uri;
    private String label;
    private String image;
    private String source;
    private String url;
    private String[] dietLabels;
    private String[] healthLabels;
    private String[] cautions;
    private String[] ingredientLines;

    private Ingredient[] ingredients;
    private double calories;
    private double totalCO2Emissions;
    private String co2EmissionsClass;
    private double totalWeight;
    private double totalTime;
    private String[] cuisineType;
    private String[] mealType;
    private String[] dishType;

    public String[] getCuisineType() {
        return cuisineType;
    }

    public String[] getMealType() {
        return mealType;
    }

    public String[] getDishType() {
        return dishType;
    }

    public String getUri() {
        return uri;
    }

    public String getLabel() {
        return label;
    }

    public String getImage() {
        return image;
    }

    public String getSource() {
        return source;
    }

    public String getUrl() {
        return url;
    }

    public String[] getDietLabels() {
        return dietLabels;
    }

    public String[] getHealthLabels() {
        return healthLabels;
    }

    public String[] getCautions() {
        return cautions;
    }

    public String[] getIngredientLines() {
        return ingredientLines;
    }

    public Ingredient[] getIngredients() {
        return ingredients;
    }

    public double getCalories() {
        return calories;
    }

    public double getTotalCO2Emissions() {
        return totalCO2Emissions;
    }

    public String getCo2EmissionsClass() {
        return co2EmissionsClass;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public double getTotalTime() {
        return totalTime;
    }
}
