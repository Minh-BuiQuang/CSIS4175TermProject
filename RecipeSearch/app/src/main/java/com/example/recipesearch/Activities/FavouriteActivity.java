package com.example.recipesearch.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.recipesearch.Entities.Recipe;
import com.example.recipesearch.R;
import com.example.recipesearch.RecyclerView.RecyclerAdapter;
import com.example.recipesearch.Utilities.DBHandler;
import com.example.recipesearch.Utilities.Parser;
import com.example.recipesearch.Utilities.VolleySingleton;
import com.example.recipesearch.databinding.ActivityFavouriteBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FavouriteActivity extends AppCompatActivity {
    ActivityFavouriteBinding binding;
    private RequestQueue requestQueue;
    private ArrayList<Recipe> recipes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavouriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.favouriteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        getRecipes();
    }

    private void getRecipes() {
        DBHandler db = new DBHandler(this);
        ArrayList<String> uris = db.getRecipeUris();
        for (String u : uris) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("api.edamam.com")
                    .appendPath("api")
                    .appendPath("recipes")
                    .appendPath("v2")
                    .appendPath("by-uri")
                    .appendQueryParameter("type", "public")
                    .appendQueryParameter("app_id", "419ca9bc")
                    .appendQueryParameter("app_key", "9a5c7a063153a0ace918f9f443dd4e81")
                    .appendQueryParameter("uri",u);
            String uri = builder.build().toString();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray jsonArray = response.getJSONArray("hits");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Log.i("JSON Response", jsonObject.toString());
                            recipes.add(Parser.ParseRecipe(jsonObject.getString("recipe")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RecyclerAdapter adapter = new RecyclerAdapter(FavouriteActivity.this, recipes);
                    binding.favouriteRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(FavouriteActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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

    }
}