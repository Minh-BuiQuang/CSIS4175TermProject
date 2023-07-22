package com.example.recipesearch.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipesearch.Activities.RecipeDetailActivity;
import com.example.recipesearch.Entities.Ingredient;
import com.example.recipesearch.Entities.Recipe;
import com.example.recipesearch.R;
import com.example.recipesearch.Utilities.DBHandler;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecipeRecyclerAdapter.ViewHolder> {
    Context context;
    List<Recipe> recipes;

    public RecipeRecyclerAdapter(Context context, List<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public RecipeRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        Glide.with(context).load(recipe.getImage()).into(holder.imageView);
        holder.labelTextView.setText(recipe.getLabel());
        if(recipe.getCuisineType() != null)
            holder.cuisineTextView.setText("Cuisine: " + TextUtils.join(",", recipe.getCuisineType()));
        if(recipe.getMealType() != null)
            holder.mealTypeTextView.setText("Meal: "+ TextUtils.join(",", recipe.getMealType()));
        if(recipe.getDishType() != null)
            holder.dishTypeTextView.setText("Type: " + TextUtils.join(",", recipe.getDishType()));
        holder.caloriesTextView.setText("Calories: " + String.format("%.2f", recipe.getCalories()));
        double totalTime = recipe.getTotalTime();
        if(totalTime == 0 ){
            holder.totalTimeTextView.setVisibility(View.GONE);
        } else {
            holder.totalTimeTextView.setVisibility(View.VISIBLE);
            holder.totalTimeTextView.setText(recipe.getTotalTime() + "m");
        }
        loadDietLabel(holder, recipe.getDietLabels());

        holder.cardView.setOnClickListener(v -> {
            Intent i = new Intent(context, RecipeDetailActivity.class );
            Gson gson = new Gson();
            i.putExtra("recipe", gson.toJson(recipe));
            context.startActivity(i);
        });

        DBHandler db = new DBHandler(context);
        ArrayList<String> uris = db.getRecipeUris();
        db.close();
        String uri = recipe.getUri();
        if(uris.contains(uri)) {
            holder.favouriteButton.setChecked(true);
        }
        holder.favouriteButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            DBHandler dbHandler = new DBHandler(context);
            if(isChecked) dbHandler.addRecipe(uri);
            else dbHandler.removeRecipe(uri);
            dbHandler.close();
        });
        holder.addToCartButton.setOnClickListener(v -> {
            DBHandler dbHandler = new DBHandler(context);
            for (Ingredient i : recipe.getIngredients()) {
                dbHandler.addOrUpdateIngredient(i);
            }
            dbHandler.close();
            Toast.makeText(context, "Ingredients have been added to grocery list", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    private void loadDietLabel(ViewHolder holder, String[] dietLabels) {
        holder.chipGroup.removeAllViews();
        for (String dietLabel : dietLabels) {
            Chip chip = new Chip(this.context);
            chip.setText(dietLabel);
            holder.chipGroup.addView(chip);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imageView;
        TextView labelTextView, cuisineTextView, mealTypeTextView, dishTypeTextView, caloriesTextView, totalTimeTextView;
        ChipGroup chipGroup;
        ToggleButton favouriteButton;
        ImageButton addToCartButton;
        View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            imageView = view.findViewById(R.id.imageView);
            labelTextView = view.findViewById(R.id.labelTextView);
            cuisineTextView = view.findViewById(R.id.cuisineTextView);
            mealTypeTextView = view.findViewById(R.id.mealTypeTextView);
            dishTypeTextView = view.findViewById(R.id.dishTypeTextView);
            caloriesTextView = view.findViewById(R.id.caloriesTextView);
            chipGroup = view.findViewById(R.id.chipGroup);
            cardView = view.findViewById(R.id.card_view);
            totalTimeTextView = view.findViewById(R.id.totalTimeTextView);
            favouriteButton = view.findViewById(R.id.favouriteButton);
            addToCartButton = view.findViewById(R.id.addToCartButton);
        }
    }
}
