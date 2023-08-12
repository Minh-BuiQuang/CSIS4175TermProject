package com.example.recipesearch.Entities;

public class Filter {
    private int id;
    private String name;
    private String keyword;
    private int maxCalories;
    private int minCalories;
    private String[] diets;
    private String[] allergies;
    private String[] cuisines;
    private String[] mealTypes;
    private String[] dishTypes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getMaxCalories() {
        return maxCalories;
    }

    public void setMaxCalories(int maxCalories) {
        this.maxCalories = maxCalories;
    }

    public int getMinCalories() {
        return minCalories;
    }

    public void setMinCalories(int minCalories) {
        this.minCalories = minCalories;
    }

    public String[] getDiets() {
        return diets;
    }

    public void setDiets(String[] diets) {
        this.diets = diets;
    }

    public String[] getAllergies() {
        return allergies;
    }

    public void setAllergies(String[] allergies) {
        this.allergies = allergies;
    }

    public String[] getCuisines() {
        return cuisines;
    }

    public void setCuisines(String[] cuisines) {
        this.cuisines = cuisines;
    }

    public String[] getMealTypes() {
        return mealTypes;
    }

    public void setMealTypes(String[] mealTypes) {
        this.mealTypes = mealTypes;
    }

    public String[] getDishTypes() {
        return dishTypes;
    }

    public void setDishTypes(String[] dishTypes) {
        this.dishTypes = dishTypes;
    }
}
