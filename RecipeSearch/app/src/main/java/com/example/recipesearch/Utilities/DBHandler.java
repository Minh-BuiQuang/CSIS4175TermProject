package com.example.recipesearch.Utilities;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.recipesearch.Entities.Filter;
import com.example.recipesearch.Entities.Ingredient;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "recipedb";
    private static final int DB_VERSION = 1;
    private static final String RECIPE_TABLE_NAME = "recipe";
    private static final String ID_COL = "id";
    private static final String URI_COL = "uri";
    private static final String INGREDIENT_TABLE_NAME = "ingredient";
    private static final String FOOD_COL = "food";
    private static final String QUANTITY_COL = "quantity";
    private static final String MEASURE_COL = "measure";
    private static final String FOOD_ID_COL = "foodId";

    private static final String FILTER_TABLE_NAME = "filter";
    private static final String FILTER_ID_COL = "id";
    private static final String NAME_COL = "name";
    private static final String KEYWORD_COL = "keyword";
    private static final String MIN_CAL_COL = "minCal";
    private static final String MAX_CAL_COL = "maxCal";
    private static final String DIETS_COL = "diets";
    private static final String HEALTH_COL = "health";
    private static final String CUISINES_COL = "cuisines";
    private static final String MEAL_COL = "meal";
    private static final String DISH_COL = "dish";



    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createRecipeTable = "CREATE TABLE " + RECIPE_TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + URI_COL + " TEXT)";
        db.execSQL(createRecipeTable);
        String createIngredientTable = "CREATE TABLE " + INGREDIENT_TABLE_NAME + " ("
                + FOOD_ID_COL + " TEXT PRIMARY KEY, "
                + FOOD_COL + " TEXT, "
                + QUANTITY_COL + " NUMBER,"
                + MEASURE_COL + " TEXT)";
        db.execSQL(createIngredientTable);
        String createFilterTable = "CREATE TABLE " + FILTER_TABLE_NAME + " ("
                + FILTER_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_COL + " TEXT, "
                + KEYWORD_COL + " TEXT, "
                + MIN_CAL_COL + " NUMBER,"
                + MAX_CAL_COL + " NUMBER,"
                + DIETS_COL + " TEXT,"
                + HEALTH_COL + " TEXT,"
                + CUISINES_COL + " TEXT,"
                + MEAL_COL + " TEXT,"
                + DISH_COL + " TEXT)";
        db.execSQL(createFilterTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RECIPE_TABLE_NAME);
        onCreate(db);
    }

    public void addRecipe(String uri){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(URI_COL, uri);
        db.insert(RECIPE_TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<String> getRecipeUris() {
        ArrayList<String> uris = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+ RECIPE_TABLE_NAME;
        Cursor c = db.rawQuery(query, new String[]{});
        if(c.moveToFirst()) {
            do {
                uris.add(c.getString(1));
            } while (c.moveToNext());
        }
        return uris;
    }

    public boolean removeRecipe(String uri)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(RECIPE_TABLE_NAME, URI_COL + "=?", new String[]{uri}) > 0;
    }

    public void addOrUpdateIngredient(Ingredient ingredient) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = FOOD_ID_COL + "=?";
        String[] selectionArgs = {ingredient.getFoodId()};
        //try to get the record from database if exist and increase the quantity
        Cursor c = db.query(INGREDIENT_TABLE_NAME, null, selection, selectionArgs, null, null, null);
        if(c != null && c.moveToFirst()) {
            @SuppressLint("Range") double currentQuantity = c.getDouble(c.getColumnIndex(QUANTITY_COL));
            double newQuantity = currentQuantity + ingredient.getQuantity();
            ContentValues values = new ContentValues();
            values.put(QUANTITY_COL, newQuantity);
            db.update(INGREDIENT_TABLE_NAME, values, selection, selectionArgs);
            c.close();
        } else {
            //Insert a new record if Food id is not found
            ContentValues values = new ContentValues();
            values.put(FOOD_ID_COL, ingredient.getFoodId());
            values.put(FOOD_COL, ingredient.getFood());
            values.put(QUANTITY_COL, ingredient.getQuantity());
            values.put(MEASURE_COL, ingredient.getMeasure());
            db.insert(INGREDIENT_TABLE_NAME, null, values);
        }
        db.close();
    }

    @SuppressLint("Range")
    public ArrayList<Ingredient> getIngredients() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        Cursor c = db.query(INGREDIENT_TABLE_NAME, null, null, null, null ,null , null);
        if(c.moveToFirst()) {
            do {
                Ingredient i = new Ingredient();
                i.setFoodId(c.getString(c.getColumnIndex(FOOD_ID_COL)));
                i.setFood(c.getString(c.getColumnIndex(FOOD_COL)));
                i.setQuantity(c.getDouble(c.getColumnIndex(QUANTITY_COL)));
                i.setMeasure(c.getString(c.getColumnIndex(MEASURE_COL)));
                ingredients.add(i);
            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return ingredients;
    }

    public void removeIngredient(Ingredient ingredient){
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = FOOD_ID_COL + "=?";
        String[] selectionArgs = {String.valueOf(ingredient.getFoodId())};
        db.delete(INGREDIENT_TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    @SuppressLint("Range")
    public ArrayList<Filter> getFilters(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Filter> filters = new ArrayList<>();
        Cursor c = db.query(FILTER_TABLE_NAME, null, null, null, null ,null , null);
        if(c.moveToFirst()) {
            do {
                Filter f = new Filter();
                f.setId(c.getInt(c.getColumnIndex(FILTER_ID_COL)));
                f.setName(c.getString(c.getColumnIndex(NAME_COL)));
                f.setKeyword(c.getString(c.getColumnIndex(KEYWORD_COL)));
                f.setMinCalories(c.getInt(c.getColumnIndex(MIN_CAL_COL)));
                f.setMaxCalories(c.getInt(c.getColumnIndex(MAX_CAL_COL)));
                try{
                    JSONArray diets = new JSONArray(c.getString(c.getColumnIndex(DIETS_COL)));
                    String[] dietArray = new String[diets.length()];
                    for (int i = 0; i < diets.length(); i++) {
                        dietArray[i] = diets.getString(i);
                    }
                    f.setDiets(dietArray);

                    JSONArray allergies = new JSONArray(c.getString(c.getColumnIndex(HEALTH_COL)));
                    String[] allergyArray = new String[allergies.length()];
                    for (int i = 0; i < allergies.length(); i++) {
                        allergyArray[i] = allergies.getString(i);
                    }
                    f.setAllergies(allergyArray);

                    JSONArray cuisines = new JSONArray(c.getString(c.getColumnIndex(CUISINES_COL)));
                    String[] cuisineArray = new String[cuisines.length()];
                    for (int i = 0; i < cuisines.length(); i++) {
                        cuisineArray[i] = cuisines.getString(i);
                    }
                    f.setCuisines(cuisineArray);

                    JSONArray mealTypes = new JSONArray(c.getString(c.getColumnIndex(MEAL_COL)));
                    String[] mealArray = new String[mealTypes.length()];
                    for (int i = 0; i < mealTypes.length(); i++) {
                        mealArray[i] = mealTypes.getString(i);
                    }
                    f.setMealTypes(mealArray);

                    JSONArray dishTypes = new JSONArray(c.getString(c.getColumnIndex(DISH_COL)));
                    String[] dishArray = new String[dishTypes.length()];
                    for (int i = 0; i < dishTypes.length(); i++) {
                        dishArray[i] = dishTypes.getString(i);
                    }
                    f.setDishTypes(dishArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                filters.add(f);
            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return filters;
    }

    public boolean addFilter(Filter filter) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME_COL, filter.getName());
        values.put(KEYWORD_COL, filter.getKeyword());
        values.put(MIN_CAL_COL, filter.getMinCalories());
        values.put(MAX_CAL_COL, filter.getMaxCalories());
        try {
            JSONArray diets = new JSONArray(filter.getDiets());
            values.put(DIETS_COL, diets.toString());
            JSONArray allergies = new JSONArray(filter.getAllergies());
            values.put(HEALTH_COL, allergies.toString());
            JSONArray cuisines = new JSONArray(filter.getCuisines());
            values.put(CUISINES_COL, cuisines.toString());
            JSONArray mealTypes = new JSONArray(filter.getMealTypes());
            values.put(MEAL_COL, mealTypes.toString());
            JSONArray dishTypes = new JSONArray(filter.getDishTypes());
            values.put(DISH_COL, dishTypes.toString());
            db.insert(FILTER_TABLE_NAME, null, values);

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void removeFilter(String filterName){
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = NAME_COL + "=?";
        String[] selectionArgs = {filterName};
        db.delete(FILTER_TABLE_NAME, selection, selectionArgs);
        db.close();
    }
}
