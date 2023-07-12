package com.example.recipesearch.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.recipesearch.Entities.Recipe;
import com.example.recipesearch.Utilities.DBHandler;
import com.example.recipesearch.Utilities.Parser;
import com.example.recipesearch.databinding.ActivityRecipeDetailBinding;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;

public class RecipeDetailActivity extends AppCompatActivity {

    ActivityRecipeDetailBinding binding;
    Recipe recipe = new Recipe();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecipeDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent i = getIntent();
        String recipeString = i.getStringExtra("recipe");
        recipe = Parser.ParseRecipe(recipeString);
        bindData();
    }

    private void bindData() {
        binding.recipeLabelTextView.setText(recipe.getLabel());
        Glide.with(this).load(recipe.getImage()).into(binding.recipeImageView);
        if(recipe.getCuisineType() != null)
            binding.cuisineTextView.setText("Cuisine: " + TextUtils.join(",", recipe.getCuisineType()));
        if(recipe.getMealType() != null)
            binding.mealTypeTextView.setText("Meal: "+ TextUtils.join(",", recipe.getMealType()));
        if(recipe.getDishType() != null)
            binding.dishTypeTextView.setText("Type: " + TextUtils.join(",", recipe.getDishType()));
        binding.caloriesTextView.setText("Calories: " + String.format("%.2f", recipe.getCalories()));
        double totalTime = recipe.getTotalTime();
        if(totalTime == 0 ){
            binding.totalTimeTextView.setVisibility(View.GONE);
        } else {
            binding.totalTimeTextView.setVisibility(View.VISIBLE);
            binding.totalTimeTextView.setText(recipe.getTotalTime() + "m");
        }

        //Setup nutrition card views
        String[] diets = recipe.getDietLabels();
        if(diets.length == 0) {
            binding.dietsCardView.setVisibility(View.GONE);
        } else {
            for (String diet : diets) {
                Chip chip = new Chip(this);
                chip.setText(diet);
                binding.dietsChipGroup.addView(chip);
            }
        }
        binding.dietsChipGroup.setVisibility(View.GONE);
        binding.dietsCardView.setOnClickListener(v -> {
            if(binding.dietsChipGroup.getVisibility() == View.VISIBLE) {
                binding.dietsChipGroup.setVisibility(View.GONE);
                binding.dietsArrowImageView.setVisibility(View.VISIBLE);
            } else {
                binding.dietsChipGroup.setVisibility(View.VISIBLE);
                binding.dietsArrowImageView.setVisibility(View.GONE);
            }
        });

        String[] allergies = recipe.getHealthLabels();
        if(allergies.length == 0) {
            binding.healthCardView.setVisibility(View.GONE);
        } else {
            for (String allergy : allergies) {
                Chip chip = new Chip(this);
                chip.setText(allergy);
                binding.healthChipGroup.addView(chip);
            }
        }
        binding.healthChipGroup.setVisibility(View.GONE);
        binding.healthCardView.setOnClickListener(v -> {
            if(binding.healthChipGroup.getVisibility() == View.VISIBLE) {
                binding.healthChipGroup.setVisibility(View.GONE);
                binding.healthArrowImageView.setVisibility(View.VISIBLE);
            } else {
                binding.healthChipGroup.setVisibility(View.VISIBLE);
                binding.healthArrowImageView.setVisibility(View.GONE);
            }
        });

        String ingredients = String.join("\n", recipe.getIngredientLines());
        binding.ingredientsTextView.setText(ingredients);

        binding.instructionButton.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(recipe.getUrl()));
            startActivity(i);
        });
        DBHandler db = new DBHandler(this);
        ArrayList<String> uris = db.getRecipeUris();
        String uri = recipe.getUri();
        if(uris.contains(uri)) {
            binding.favouriteButton.setChecked(true);
        }
        binding.favouriteButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) db.addRecipe(uri);
            else db.removeRecipe(uri);
        });
    }
}