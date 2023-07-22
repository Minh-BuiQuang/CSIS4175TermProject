package com.example.recipesearch.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.example.recipesearch.Entities.Ingredient;
import com.example.recipesearch.R;
import com.example.recipesearch.RecyclerView.IngredientRecyclerAdapter;
import com.example.recipesearch.RecyclerView.RecipeRecyclerAdapter;
import com.example.recipesearch.Utilities.DBHandler;
import com.example.recipesearch.databinding.ActivityGroceryBinding;

import java.util.ArrayList;

public class GroceryActivity extends AppCompatActivity {
    ActivityGroceryBinding binding;
    private ArrayList<Ingredient> ingredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroceryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DBHandler db = new DBHandler(this);
        ingredients = db.getIngredients();
        db.close();
        binding.groceryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        IngredientRecyclerAdapter adapter = new IngredientRecyclerAdapter(GroceryActivity.this, ingredients);
        binding.groceryRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}