package com.example.recipesearch.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.recipesearch.Entities.Recipe;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "recipedb";
    private static final int DB_VERSION = 1;
    private static final String RECIPE_TABLE_NAME = "recipe";
    private static final String ID_COL = "id";
    private static final String URI_COL = "uri";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + RECIPE_TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + URI_COL + " TEXT)";
        db.execSQL(query);
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
}
