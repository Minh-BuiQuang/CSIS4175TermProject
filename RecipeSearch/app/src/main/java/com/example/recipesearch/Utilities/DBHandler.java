package com.example.recipesearch.Utilities;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.recipesearch.Entities.Ingredient;
import com.example.recipesearch.Entities.Recipe;

import java.util.ArrayList;
import java.util.List;

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
}
