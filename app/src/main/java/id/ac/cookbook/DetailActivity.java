package id.ac.cookbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import id.ac.cookbook.data.Rate;
import id.ac.cookbook.data.RateAdapter;
import id.ac.cookbook.data.Recipe;
import id.ac.cookbook.data.RecipeAdapter;
import id.ac.cookbook.data.User;
import id.ac.cookbook.volley.DbContract;
import id.ac.cookbook.volley.VolleyConnection;

public class DetailActivity extends AppCompatActivity {

    User me;
    Recipe recipe, tempRec;
    Rate rate;
    ArrayList<Rate> listRate;
    RateAdapter rateAdapter;

    TextView tvTitle, tvKategori, tvCreator, tvRate, tvBahan, tvStep;
    ImageButton btnBookmark;
    Button btnPublish, btnRate, btnEdit, btnDelete;
    RecyclerView rvData;
    ProgressDialog progressDialog;

    boolean bookmark, adaUser;
    ImageView gbr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        progressDialog = new ProgressDialog(DetailActivity.this);

        tvTitle = findViewById(R.id.tvDetailTitle);
        tvKategori = findViewById(R.id.tvDetailKategori);
        tvCreator = findViewById(R.id.tvDetailCreator);
        tvRate = findViewById(R.id.tvDetailRate);
        tvBahan = findViewById(R.id.tvDetailBahan);
        tvStep = findViewById(R.id.tvDetailStep);
        btnBookmark = findViewById(R.id.ibtnDetailBookmark);
        btnPublish = findViewById(R.id.btnDetailPublish);
        btnRate = findViewById(R.id.btnDetailRate);
        btnEdit = findViewById(R.id.btnDetailEdit);
        btnDelete = findViewById(R.id.btnDetailDelete);
        rvData = findViewById(R.id.rvDetailResponses);

        gbr=findViewById(R.id.gbr);

        if (getIntent().hasExtra("user")){
            me = getIntent().getParcelableExtra("user");
            adaUser = true;
        }else{
            btnBookmark.setVisibility(View.GONE);
            btnRate.setVisibility(View.GONE);
            adaUser = false;
        }

        if (getIntent().hasExtra("recipe")){
            recipe = getIntent().getParcelableExtra("recipe");

            StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.DETAIL_BARANG+"?id="+recipe.getId(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String url=DbContract.DETAIL_GAMBR+jsonObject.getString("gambar");
                        Picasso.get()
                                .load(url)
                                .into(gbr);

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
                    params.put("id", recipe.getId()+"");
                    return params;
                }
            };

            VolleyConnection.getInstance(DetailActivity.this).addToRequestQueue(stringRequest);


            setComponents();

            //LOAD REVIEW
            if (getIntent().hasExtra("user")){
                if (me.getUsername().equalsIgnoreCase(recipe.getUsernameCreator())){
                    btnPublish.setVisibility(View.VISIBLE);
                    btnEdit.setVisibility(View.VISIBLE);
                    btnDelete.setVisibility(View.VISIBLE);
                    btnRate.setVisibility(View.GONE);
                }
                getRates(recipe.getId()+"", me.getId()+"");
            }else{
                getRates(recipe.getId()+"", "noUser");
            }

        }
    }

    void setComponents(){
        tvTitle.setText(recipe.getNama());
        String kategori = "Kategori : " + recipe.getKategori();
        tvKategori.setText(kategori);
        String creator = "Created by : " + recipe.getUsernameCreator();
        tvCreator.setText(creator);
        tvRate.setText(String.format("%.2f", recipe.getRate()));
        tvBahan.setText(recipe.getTextBahan());
        tvStep.setText(recipe.getTextLangkah());
        if (recipe.getStatus() == 0){
            btnPublish.setText("Publish");
        }else{
            btnPublish.setText("Unpublish");
        }
    }

    void setBtnBookmark(){
        if (bookmark){
            btnBookmark.setTag("active");
            ImageButton ib = (ImageButton) btnBookmark;
            ib.setImageResource(R.drawable.ic_bookmark_active);
        }else{
            btnBookmark.setTag("passive");
            ImageButton ib = (ImageButton) btnBookmark;
            ib.setImageResource(R.drawable.ic_bookmark_passive);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_loginregister, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.option_logreg_home){
            Intent toHome = new Intent(DetailActivity.this, MainActivity.class);
            if (adaUser){
                toHome.putExtra("user", me);
            }
            startActivity(toHome);
        }
        return super.onOptionsItemSelected(item);
    }

    public void detailClick(View v){
        if (v.getId() == R.id.ibtnDetailBookmark){
            if (getIntent().hasExtra("user")){
                bookmarkRecipe(recipe.getId()+"", me.getId()+"");
            }
        }else if (v.getId() == R.id.btnDetailPublish){
            publishRecipe(recipe.getId()+"");
        }else if (v.getId() == R.id.btnDetailRate){
            Intent toRate = new Intent(DetailActivity.this, RateActivity.class);
            toRate.putExtra("user", me);
            toRate.putExtra("recipe", recipe);
            startActivity(toRate);
        }else if (v.getId() == R.id.btnDetailEdit){
            Intent toUpdate = new Intent(DetailActivity.this, UpdateRecipeActivity.class);
            toUpdate.putExtra("user", me);
            toUpdate.putExtra("recipe", recipe);
            startActivity(toUpdate);
        }else if (v.getId() == R.id.btnDetailDelete){
            if (getIntent().hasExtra("user")){
                deleteRecipe(recipe.getId()+"");
            }
        }
    }

    public void deleteRecipe(final String id_recipe){
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
                            Intent toHome = new Intent(DetailActivity.this, MainActivity.class);
                            toHome.putExtra("fragment", "myrecipe");
                            toHome.putExtra("user", me);
                            startActivity(toHome);
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
                    params.put("function", "deleteRecipe");
                    params.put("id_recipe", id_recipe);
                    return params;
                }
            };

            VolleyConnection.getInstance(DetailActivity.this).addToRequestQueue(stringRequest);

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

    public void getRates(final String id_recipe, final String id_user){
        listRate = new ArrayList<>();
        if (checkNetworkConnection()){
            progressDialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_MASTER_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int code = jsonObject.getInt("code");
                        String message = jsonObject.getString("message");
                        bookmark = jsonObject.getBoolean("bookmark");
//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        if (code == 1){ //rate load
                            JSONArray jsonArray = jsonObject.getJSONArray("datarate");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject rateObj = jsonArray.getJSONObject(i);
                                rate = new Rate(
                                        rateObj.getInt("id"),
                                        rateObj.getInt("star"),
                                        rateObj.getString("username"),
                                        rateObj.getString("review")
                                );
                                listRate.add(rate);
                            }
                        }
                        setBtnBookmark();
                        setUpRecyclerView(listRate);
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
                    params.put("function", "getRates");
                    params.put("id_recipe", id_recipe);
                    params.put("id_user", id_user);
                    return params;
                }
            };

            VolleyConnection.getInstance(DetailActivity.this).addToRequestQueue(stringRequest);

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

    public void publishRecipe(final String id_recipe){
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
                        if (code == 1){ //rate load
                            int code2 = jsonObject.getInt("code2");
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
                            if (code2 == 1){
                                Toast.makeText(getApplicationContext(), "PUBLISHED", Toast.LENGTH_SHORT).show();
                            }else if (code2 == 0){
                                Toast.makeText(getApplicationContext(), "UNPUBLISHED", Toast.LENGTH_SHORT).show();
                            }
                            setComponents();
                        }else{ // login admin
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
                    params.put("function", "publishRecipe");
                    params.put("id_recipe", id_recipe);
                    return params;
                }
            };

            VolleyConnection.getInstance(DetailActivity.this).addToRequestQueue(stringRequest);

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

    public void bookmarkRecipe(final String id_recipe, final String id_user){
        if (checkNetworkConnection()){
            progressDialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_MASTER_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        bookmark = jsonObject.getBoolean("bookmark");
                        int code = jsonObject.getInt("code");
                        String message = jsonObject.getString("message");
                        if (code == 1){
                            int code2 = jsonObject.getInt("code2");
                            if (code2 == 1){
                                Toast.makeText(getApplicationContext(), "Bookmark removed!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(), "Bookmarked!", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                        setBtnBookmark();
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
                    params.put("function", "bookmarkRecipe");
                    params.put("id_user", id_user);
                    params.put("id_recipe", id_recipe);
                    return params;
                }
            };

            VolleyConnection.getInstance(DetailActivity.this).addToRequestQueue(stringRequest);

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

    void setUpRecyclerView(ArrayList<Rate> listRate){
        rvData.setLayoutManager(new LinearLayoutManager(this));
        rvData.setHasFixedSize(true);

        rateAdapter = new RateAdapter(listRate);
        rvData.setAdapter(rateAdapter);
    }
}