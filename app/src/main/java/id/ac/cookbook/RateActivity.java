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
import android.widget.RatingBar;
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

import java.net.Inet4Address;
import java.util.HashMap;
import java.util.Map;

import id.ac.cookbook.data.Recipe;
import id.ac.cookbook.data.User;
import id.ac.cookbook.volley.DbContract;
import id.ac.cookbook.volley.VolleyConnection;

public class RateActivity extends AppCompatActivity {

    User me;
    Recipe recipe;

    ProgressDialog progressDialog;

    RatingBar rating;
    EditText etReview;
    TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        progressDialog = new ProgressDialog(RateActivity.this);

        rating = findViewById(R.id.ratingBarRateAct);
        etReview = findViewById(R.id.etRateActReview);
        tvTitle = findViewById(R.id.tvRateActTitle);

        if (getIntent().hasExtra("user")){
            me = getIntent().getParcelableExtra("user");
        }
        if (getIntent().hasExtra("recipe")){
            recipe = getIntent().getParcelableExtra("recipe");
            tvTitle.setText(recipe.getNama());
        }

        getMyRate(me.getId()+"", recipe.getId()+"");
    }

    public void rateActClick(View v){
        if (v.getId() == R.id.btnRateAct){
            rateRecipe(recipe.getId()+"", me.getId()+"", rating.getRating()+"", etReview.getText().toString());
        }else if (v.getId() == R.id.btnRateActBack){
            getBack();
        }
    }

    void getBack(){
        Intent toBack = new Intent(RateActivity.this, DetailActivity.class);
        toBack.putExtra("user", me);
        toBack.putExtra("recipe", recipe);
        startActivity(toBack);
    }

    public void rateRecipe(final String id_recipe, final String id_user, final String star, final String review){
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
                        if (code == 1){
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
                            getBack();
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
                    params.put("function", "rateRecipe");
                    params.put("id_recipe", id_recipe);
                    params.put("id_user", id_user);
                    params.put("star", star);
                    params.put("review", review);
                    return params;
                }
            };

            VolleyConnection.getInstance(RateActivity.this).addToRequestQueue(stringRequest);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.cancel();
                }
            }, 1000);
        }else{
            Toast.makeText(getApplicationContext(), "Tidak ada koneksi internet!", Toast.LENGTH_SHORT).show();
        }
    }

    public void getMyRate(final String id_user, final String id_recipe){
        if (checkNetworkConnection()){
            progressDialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_MASTER_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int code = jsonObject.getInt("code");
                        String message = jsonObject.getString("message");
//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        if (code == 1){
                            JSONArray jsonArray = jsonObject.getJSONArray("datarate");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject rateObj = jsonArray.getJSONObject(i);
                                rating.setRating(rateObj.getInt("star"));
                                etReview.setText(rateObj.getString("review"));
                            }
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
                    params.put("function", "getMyRate");
                    params.put("id_user", id_user);
                    params.put("id_recipe", id_recipe);
                    return params;
                }
            };

            VolleyConnection.getInstance(RateActivity.this).addToRequestQueue(stringRequest);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.cancel();
                }
            }, 1000);
        }else{
            Toast.makeText(getApplicationContext(), "Tidak ada koneksi internet!", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkNetworkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}