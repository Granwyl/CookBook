package id.ac.cookbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import id.ac.cookbook.data.Recipe;
import id.ac.cookbook.data.RecipeAdapter;
import id.ac.cookbook.data.User;

public class MyRecipeActivity extends AppCompatActivity {
    User user;
    RecyclerView rvData;
    RecipeAdapter recipeAdapter;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipe);

        rvData = findViewById(R.id.rvMyRecipe);
        context = this;
    }

    void setUpRecyclerView(ArrayList<Recipe> listRecipe, ArrayList<User> listUser){
        rvData.setLayoutManager(new LinearLayoutManager(this));
        rvData.setHasFixedSize(true);

        recipeAdapter = new RecipeAdapter(listRecipe);
        recipeAdapter.setOnItemClickCallback(new RecipeAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(Recipe recipe) {

            }
        });
        rvData.setAdapter(recipeAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_user, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.option_exit){
            Intent toHome = new Intent(MyRecipeActivity.this, MainActivity.class);
            startActivity(toHome);
        }else if (item.getItemId() == R.id.option_add){
            Intent toAdd = new Intent(MyRecipeActivity.this, AddRecipeActivity.class);
            toAdd.putExtra("user", user);
            startActivity(toAdd);
        }else if (item.getItemId() == R.id.option_home){
            Intent toHome = new Intent(MyRecipeActivity.this, MainActivity.class);
            toHome.putExtra("user", user);
            startActivity(toHome);
        }
        return super.onOptionsItemSelected(item);
    }
}