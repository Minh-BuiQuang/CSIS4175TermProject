package com.example.recipesearch.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.recipesearch.Entities.Recipe;
import com.example.recipesearch.R;
import com.example.recipesearch.Utilities.Parser;
import com.example.recipesearch.databinding.ActivityRecipeDetailBinding;
import com.google.android.material.chip.Chip;
import com.google.gson.Gson;

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
        binding.cuisineTextView.setText("Cuisine: " + TextUtils.join(",", recipe.getCuisineType()));
        binding.mealTypeTextView.setText("Meal: "+ TextUtils.join(",", recipe.getMealType()));
        binding.dishTypeTextView.setText("Type: " + TextUtils.join(",", recipe.getDishType()));
        binding.caloriesTextView.setText("Calories: " + String.format("%.2f", recipe.getCalories()));


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


    }
}