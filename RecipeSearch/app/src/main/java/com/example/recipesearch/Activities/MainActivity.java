package com.example.recipesearch.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.recipesearch.Entities.Recipe;
import com.example.recipesearch.R;
import com.example.recipesearch.RecyclerView.RecipeRecyclerAdapter;
import com.example.recipesearch.Utilities.Parser;
import com.example.recipesearch.Utilities.VolleySingleton;
import com.example.recipesearch.databinding.ActivityMainBinding;
import com.google.android.material.chip.Chip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private RequestQueue requestQueue;
    private ArrayList<Recipe> recipes = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.recipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        binding.searchButton.setOnClickListener(v -> {
            searchRecipe();
        });
        binding.searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                searchRecipe();
            }
            return true;
        });
        prepareSearchOptions();
        binding.filterConstraintLayout.setVisibility(View.GONE);
        binding.cardView.setOnClickListener(v -> {
            if(binding.filterConstraintLayout.getVisibility() == View.GONE) {
                binding.filterConstraintLayout.setVisibility(View.VISIBLE);
            } else {
                binding.filterConstraintLayout.setVisibility(View.GONE);
            }
        });

    }
    private void prepareSearchOptions() {
        String[] diets = {"balanced","high-fiber","high-protein", "low-carb", "low-fat", "low-sodium" };
        String[] health = {"alcohol-cocktail","alcohol-free","celery-free","crustacean-free","dairy-free","DASH","egg-free","fish-free",
                "fodmap-free","gluten-free","immuno-supportive","keto-friendly","kidney-friendly","kosher","low-potassium","low-sugar",
                "lupine-free","Mediterranean","mollusk-free","mustard-free","no-oil-added","paleo","peanut-free","pescatarian","pork-free",
                "red-meat-free","sesame-free","shellfish-free","soy-free","sugar-conscious","sulfite-free","tree-nut-free","vegan",
                "vegetarian","wheat-free"};
        String[] cuisine = {"American","Asian","British","Caribbean","Central Europe","Chinese","Eastern Europe","French","Indian","Italian","Japanese",
                "Kosher","Mediterranean","Mexican","Middle Eastern","Nordic","South American","South East Asian"};
        String[] mealType = {"Breakfast","Dinner","Lunch","Snack","Teatime"};
        String[] dishType = {"Biscuits and cookies","Bread","Cereals","Condiments and sauces","Desserts","Drinks","Main course","Pancake","Preps","Preserve","Salad","Sandwiches","Side dish","Soup","Starter","Sweets"};
        for (String d : diets) {
            Chip chip = new Chip(this);
            chip.setText(d);
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);
            chip.setChipBackgroundColor(AppCompatResources.getColorStateList(this, R.color.chip_state));
            binding.dietsChipGroup.addView(chip);
        }
        for (String h : health) {
            Chip chip = new Chip(this);
            chip.setText(h);
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);
            chip.setChipBackgroundColor(AppCompatResources.getColorStateList(this, R.color.chip_state));
            binding.healthChipGroup.addView(chip);
        }
        for (String c : cuisine) {
            Chip chip = new Chip(this);
            chip.setText(c);
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);
            chip.setChipBackgroundColor(AppCompatResources.getColorStateList(this, R.color.chip_state));
            binding.cuisineChipGroup.addView(chip);
        }
        for (String m : mealType) {
            Chip chip = new Chip(this);
            chip.setText(m);
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);
            chip.setChipBackgroundColor(AppCompatResources.getColorStateList(this, R.color.chip_state));
            binding.mealTypeChipGroup.addView(chip);
        }
        for (String d : dishType) {
            Chip chip = new Chip(this);
            chip.setText(d);
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);
            chip.setChipBackgroundColor(AppCompatResources.getColorStateList(this, R.color.chip_state));
            binding.dishTypeChipGroup.addView(chip);
        }
    }

    private void searchRecipe() {
        //Hide advance search and keyboard
        binding.filterConstraintLayout.setVisibility(View.GONE);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.searchEditText.getWindowToken(), 0);
        //Build uri
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.edamam.com")
                .appendPath("api")
                .appendPath("recipes")
                .appendPath("v2")
                .appendQueryParameter("type", "public")
                .appendQueryParameter("app_id", "419ca9bc")
                .appendQueryParameter("app_key", "9a5c7a063153a0ace918f9f443dd4e81");

        //Add search keywords to search query
        String keywords = binding.searchEditText.getText().toString();
        if(!keywords.isEmpty()) {
            builder.appendQueryParameter("q", keywords);
        }

        int minCal = Math.round(binding.caloriesRangeSlider.getValues().get(0));
        int maxCal = Math.round(binding.caloriesRangeSlider.getValues().get(1));
        builder.appendQueryParameter("calories",minCal + "-" + maxCal);

        List<Integer> diets = binding.dietsChipGroup.getCheckedChipIds();
        for (int id : diets){
            Chip chip = binding.dietsChipGroup.findViewById(id);
            if(chip != null && chip.isChecked()) {
                builder.appendQueryParameter("diet",chip.getText().toString());
            }
        }
        List<Integer> health = binding.healthChipGroup.getCheckedChipIds();
        for (int id : health){
            Chip chip = binding.healthChipGroup.findViewById(id);
            if(chip != null && chip.isChecked()) {
                builder.appendQueryParameter("health",chip.getText().toString());
            }
        }
        List<Integer> cuisine = binding.cuisineChipGroup.getCheckedChipIds();
        for (int id : cuisine){
            Chip chip = binding.cuisineChipGroup.findViewById(id);
            if(chip != null && chip.isChecked()) {
                builder.appendQueryParameter("cuisineType",chip.getText().toString());
            }
        }
        List<Integer> meals = binding.mealTypeChipGroup.getCheckedChipIds();
        for (int id : meals){
            Chip chip = binding.mealTypeChipGroup.findViewById(id);
            if(chip != null && chip.isChecked()) {
                builder.appendQueryParameter("mealType",chip.getText().toString());
            }
        }
        List<Integer> dishes = binding.dishTypeChipGroup.getCheckedChipIds();
        for (int id : dishes){
            Chip chip = binding.dishTypeChipGroup.findViewById(id);
            if(chip != null && chip.isChecked()) {
                builder.appendQueryParameter("dishType",chip.getText().toString());
            }
        }

        String uri = builder.build().toString();
        Log.i("Url", uri);
        //Send query
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    recipes.clear();
                    JSONArray jsonArray = response.getJSONArray("hits");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Log.i("JSON Response", jsonObject.toString());
                        recipes.add(Parser.ParseRecipe(jsonObject.getString("recipe")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RecipeRecyclerAdapter adapter = new RecipeRecyclerAdapter(MainActivity.this, recipes);
                binding.recipeRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Edamam-Account-User", "minh1806");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.favourite:
                startActivity(new Intent(this, FavouriteActivity.class));
                return true;
            case R.id.grocery:
                startActivity(new Intent(this, GroceryActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}