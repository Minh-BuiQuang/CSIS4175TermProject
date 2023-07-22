package com.example.recipesearch.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipesearch.Entities.Ingredient;
import com.example.recipesearch.R;
import com.example.recipesearch.Utilities.DBHandler;

import java.util.List;

public class IngredientRecyclerAdapter extends RecyclerView.Adapter<IngredientRecyclerAdapter.ViewHolder> {
    Context context;
    List<Ingredient> ingredients;
    public IngredientRecyclerAdapter(Context context, List<Ingredient> ingredients) {
        this.context = context;
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grocery_line, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient i = ingredients.get(position);
        if(i.getQuantity() == 0) {
            holder.groceryLineTextView.setText(i.getFood());
        } else {
            holder.groceryLineTextView.setText(i.getQuantity() + " " + i.getMeasure() + " of " + i.getFood());
        }
        holder.doneButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                DBHandler db = new DBHandler(context);
                db.removeIngredient(i);
                db.close();
                buttonView.setEnabled(false);
                holder.view.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView groceryLineTextView;
        ToggleButton doneButton;
        View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            groceryLineTextView = itemView.findViewById(R.id.groceryLineTextView);
            doneButton = itemView.findViewById(R.id.doneButton);
        }
    }
}
