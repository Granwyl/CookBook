package id.ac.cookbook;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.ac.cookbook.data.Recipe;
import id.ac.cookbook.data.User;
import id.ac.cookbook.volley.DbContract;
import id.ac.cookbook.volley.VolleyConnection;

public class UpdateRecipeActivity extends AppCompatActivity {

    User me;
    Recipe recipe;

    EditText etTitle, etIngredients, etSteps;
    Spinner spinnerKategori;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_recipe);

        etTitle = findViewById(R.id.etUpdateRecipeNama);
        etIngredients = findViewById(R.id.etUpdateRecipeBahan);
        etSteps = findViewById(R.id.etUpdateRecipeLangkah);
        spinnerKategori = findViewById(R.id.spinnerUpdateRecipe);

        progressDialog = new ProgressDialog(UpdateRecipeActivity.this);

        if (getIntent().hasExtra("user")){
            me = getIntent().getParcelableExtra("user");
        }
        if (getIntent().hasExtra("recipe")){
            recipe = getIntent().getParcelableExtra("recipe");
            setComponent();
        }
    }

    public void updateRecipeClick(View v){
        if (v.getId() == R.id.btnUpdateRecipe){
            String nama = etTitle.getText().toString();
            String bahan = etIngredients.getText().toString();
            String langkah = etSteps.getText().toString();
            String kategori = spinnerKategori.getSelectedItem().toString();

            if (nama.isEmpty()){
                etTitle.setError("Nama resep perlu diisi!");
                etTitle.requestFocus();
                return;
            }
            if(bahan.isEmpty()){
                etIngredients.setError("Bahan-bahan perlu diisi!");
                etIngredients.requestFocus();
                return;
            }
            if (langkah.isEmpty()){
                etSteps.setError("Langkah-langkah perlu diisi!");
                etSteps.requestFocus();
                return;
            }

            int idKategori = getSpinnerValuePosition(spinnerKategori, kategori) + 1;

            updateRecipe(nama, idKategori+"", bahan, langkah, recipe.getId()+"");
        }else if (v.getId() == R.id.btnUpdateRecipeBack){
            backDetail();
        }
    }

    public void updateRecipe(final String title, final String idCategory, final String ingredients, final String steps, final String idRecipe){
        if (checkNetworkConnection()){
            progressDialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_MASTER_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int code = jsonObject.getInt("code");
                        String message = jsonObject.getString("message");
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        if (code == 1){ // inserted
                            JSONArray jsonArray = jsonObject.getJSONArray("datarecipe");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject recipeObj = jsonArray.getJSONObject(i);
                                String kategori;
                                if (recipeObj.getInt("id_category") == 1){
                                    kategori = "Food";
                                }else if (recipeObj.getInt("id_category") == 2){
                                    kategori = "Beverage";
                                }else {
                                    kategori = "Side Dish";
                                }
                                recipe = new Recipe(
                                        recipeObj.getInt("id"),
                                        recipeObj.getString("title"),
                                        recipeObj.getString("username"),
                                        kategori,
                                        recipeObj.getString("ingredients"),
                                        recipeObj.getString("steps"),
                                        recipeObj.getInt("status_publish"),
                                        recipeObj.getDouble("rate")
                                );
                            }
                            backDetail();
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("function", "updateRecipe");
                    params.put("title", title);
                    params.put("id_category", idCategory);
                    params.put("ingredients", ingredients);
                    params.put("steps", steps);
                    params.put("id_recipe", idRecipe);
                    return params;
                }
            };

            VolleyConnection.getInstance(UpdateRecipeActivity.this).addToRequestQueue(stringRequest);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.cancel();
                }
            }, 2000);
        }else{
            Toast.makeText(getApplicationContext(), "Tidak ada koneksi internet!", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkNetworkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    void backDetail(){
        Intent toBack = new Intent(UpdateRecipeActivity.this, DetailActivity.class);
        toBack.putExtra("user", me);
        toBack.putExtra("recipe", recipe);
        startActivity(toBack);
    }

    void setComponent(){
        etTitle.setText(recipe.getNama());
        etIngredients.setText(recipe.getTextBahan());
        etSteps.setText(recipe.getTextLangkah());
        if (recipe.getKategori().equalsIgnoreCase("food")){
            spinnerKategori.setSelection(0);
        }else if (recipe.getKategori().equalsIgnoreCase("beverage")){
            spinnerKategori.setSelection(1);
        }else{
            spinnerKategori.setSelection(2);
        }
    }

    int getSpinnerValuePosition(Spinner spinner, String value){
        int position = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            String item = spinner.getItemAtPosition(i).toString();
            if (item.equalsIgnoreCase(value)){
                position = i;
                break;
            }
        }
        return position;
    }
}