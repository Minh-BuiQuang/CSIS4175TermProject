package com.example.recipesearch.RecyclerView;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipesearch.Entities.Recipe;
import com.example.recipesearch.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    Context context;
    List<Recipe> recipes;

    public RecyclerAdapter(Context context, List<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        Glide.with(context).load(recipe.getImage()).into(holder.imageView);
        holder.labelTextView.setText(recipe.getLabel());
        holder.cuisineTextView.setText("Cuisine: " + TextUtils.join(",", recipe.getCuisineType()));
        holder.mealTypeTextView.setText("Meal: "+ TextUtils.join(",", recipe.getMealType()));
        holder.dishTypeTextView.setText("Type: " + TextUtils.join(",", recipe.getDishType()));
        holder.caloriesTextView.setText("Calories: " + String.format("%.2f", recipe.getCalories()));
        loadDietLabel(holder, recipe.getDietLabels());
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
        ImageView imageView;
        TextView labelTextView, cuisineTextView, mealTypeTextView, dishTypeTextView, caloriesTextView;
        ChipGroup chipGroup;
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
        }
    }
}
