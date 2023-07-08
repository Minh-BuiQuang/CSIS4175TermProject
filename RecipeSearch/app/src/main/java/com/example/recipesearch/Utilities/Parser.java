package com.example.recipesearch.Utilities;

import android.util.Log;

import com.example.recipesearch.Entities.Recipe;
import com.google.gson.Gson;

import org.json.JSONObject;

public class Parser {
    public static Recipe ParseRecipe(JSONObject json) {
        Recipe recipe = new Recipe();
        Gson g = new Gson();
        try {
            recipe = g.fromJson(json.getString("recipe"), Recipe.class);
        } catch (Exception e) {
            Log.e("JsonParse", "Failed to parse json object");
        }
        return recipe;
    }
}
