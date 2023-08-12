package com.example.recipesearch.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.recipesearch.Entities.Filter;
import com.example.recipesearch.Entities.Recipe;
import com.example.recipesearch.R;
import com.example.recipesearch.RecyclerView.RecipeRecyclerAdapter;
import com.example.recipesearch.Utilities.DBHandler;
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
    ArrayList<Filter> filters = new ArrayList<>();
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
        loadSavedSearches();
        binding.saveButton.setOnClickListener(v -> {
            //show a dialog to get the name of the filter
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Title");

            final EditText input = new EditText(this);

            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String filterName = input.getText().toString();
                    if(filterName.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                    } else {
                        Filter f = new Filter();
                        f.setName(filterName);
                        f.setKeyword(binding.searchEditText.getText().toString());
                        f.setMinCalories(Math.round(binding.caloriesRangeSlider.getValues().get(0)));
                        f.setMaxCalories(Math.round(binding.caloriesRangeSlider.getValues().get(1)));

                        List<Integer> selectedDiets = binding.dietsChipGroup.getCheckedChipIds();
                        String[] diets = new String[selectedDiets.size()];
                        int dietCount = 0;
                        for(int i = 0; i < binding.dietsChipGroup.getChildCount(); i++) {
                            Chip chip = (Chip) binding.dietsChipGroup.getChildAt(i);
                            if(chip.isChecked()) {
                                diets[dietCount] = chip.getText().toString();
                                dietCount++;
                            }
                        }
                        f.setDiets(diets);

                        List<Integer> selectedHealth = binding.healthChipGroup.getCheckedChipIds();
                        String[] health = new String[selectedHealth.size()];
                        int healthCount = 0;
                        for (int i = 0; i < selectedHealth.size(); i++) {
                            Chip chip = (Chip) binding.healthChipGroup.getChildAt(i);
                            health[healthCount] = chip.getText().toString();
                            healthCount++;
                        }
                        f.setAllergies(health);

                        List<Integer> selectedCuisine = binding.cuisineChipGroup.getCheckedChipIds();
                        String[] cuisine = new String[selectedCuisine.size()];
                        int cuisineCount = 0;
                        for (int i = 0; i < selectedCuisine.size(); i++) {
                            Chip chip = (Chip) binding.cuisineChipGroup.getChildAt(i);
                            cuisine[cuisineCount] = chip.getText().toString();
                            cuisineCount++;
                        }
                        f.setCuisines(cuisine);

                        List<Integer> selectedMealType = binding.mealTypeChipGroup.getCheckedChipIds();
                        String[] mealType = new String[selectedMealType.size()];
                        int mealTypeCount = 0;
                        for (int i = 0; i < selectedMealType.size(); i++) {
                            Chip chip = (Chip) binding.mealTypeChipGroup.getChildAt(i);
                            mealType[mealTypeCount] = chip.getText().toString();
                            mealTypeCount++;
                        }
                        f.setMealTypes(mealType);

                        List<Integer> selectedDishType = binding.dishTypeChipGroup.getCheckedChipIds();
                        String[] dishType = new String[selectedDishType.size()];
                        int dishTypeCount = 0;
                        for (int i = 0; i < selectedDishType.size(); i++) {
                            Chip chip = (Chip) binding.dishTypeChipGroup.getChildAt(i);
                            dishType[dishTypeCount] = chip.getText().toString();
                            dishTypeCount++;
                        }
                        f.setDishTypes(dishType);

                        DBHandler dbHandler = new DBHandler(MainActivity.this);
                        boolean success = dbHandler.addFilter(f);
                        if(success) {
                            Toast.makeText(MainActivity.this, "Filter saved", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Error saving filter", Toast.LENGTH_SHORT).show();
                        }
                        loadSavedSearches();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
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

    private void loadSavedSearches() {
        DBHandler dbHandler = new DBHandler(this);
        binding.savedSearchChipGroup.removeAllViews();
        filters = dbHandler.getFilters();
        for (Filter filter: filters){
            Chip chip = new Chip(this);
            chip.setCloseIconVisible(true);
            chip.setText(filter.getName());
            chip.setTag( String.valueOf(filter.getId()));

            //Load preset when chip is clicked
            chip.setOnClickListener(v -> {
                //Find the filter that was clicked and load its presets
                for (Filter f : filters) {
                    if (f.getId() == Integer.parseInt(chip.getTag().toString())) {
                        binding.searchEditText.setText(f.getKeyword());
                        int minCal = f.getMinCalories();
                        int maxCal = f.getMaxCalories();
                        binding.caloriesRangeSlider.setValues((float)minCal, (float)maxCal);
                        for(int i =0; i < binding.dietsChipGroup.getChildCount(); i++){
                            String name = ((Chip)binding.dietsChipGroup.getChildAt(i)).getText().toString();
                            for (String d : f.getDiets()) {
                                if (name.equals(d)) {
                                    ((Chip)binding.dietsChipGroup.getChildAt(i)).setChecked(true);
                                }
                            }
                        }
                        for(int i =0; i < binding.healthChipGroup.getChildCount(); i++){
                            String name = ((Chip)binding.healthChipGroup.getChildAt(i)).getText().toString();
                            for (String h : f.getAllergies()) {
                                if (name.equals(h)) {
                                    ((Chip)binding.healthChipGroup.getChildAt(i)).setChecked(true);
                                }
                            }
                        }
                        for(int i =0; i < binding.cuisineChipGroup.getChildCount(); i++){
                            String name = ((Chip)binding.cuisineChipGroup.getChildAt(i)).getText().toString();
                            for (String c : f.getCuisines()) {
                                if (name.equals(c)) {
                                    ((Chip)binding.cuisineChipGroup.getChildAt(i)).setChecked(true);
                                }
                            }
                        }
                        for(int i =0; i < binding.mealTypeChipGroup.getChildCount(); i++){
                            String name = ((Chip)binding.mealTypeChipGroup.getChildAt(i)).getText().toString();
                            for (String m : f.getMealTypes()) {
                                if (name.equals(m)) {
                                    ((Chip)binding.mealTypeChipGroup.getChildAt(i)).setChecked(true);
                                }
                            }
                        }
                        for(int i =0; i < binding.dishTypeChipGroup.getChildCount(); i++){
                            String name = ((Chip)binding.dishTypeChipGroup.getChildAt(i)).getText().toString();
                            for (String d : f.getDishTypes()) {
                                if (name.equals(d)) {
                                    ((Chip)binding.dishTypeChipGroup.getChildAt(i)).setChecked(true);
                                }
                            }
                        }
                    }
                }
            });

            //delete filter when close icon is clicked
            chip.setOnCloseIconClickListener(v -> {
            //Delete the filter that was clicked
                String name = chip.getText().toString();
                dbHandler.removeFilter(name);
                Toast.makeText(this, "Filter " + name + " deleted", Toast.LENGTH_SHORT).show();
                loadSavedSearches();
            });
            binding.savedSearchChipGroup.addView(chip);
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