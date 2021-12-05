package id.ac.cookbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.EditText;
import android.widget.Spinner;
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

public class AddRecipeActivity extends AppCompatActivity {
    EditText etNama, etBahan, etLangkah;
    User user;
    Spinner spinner;
    Recipe recipe;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        etNama = findViewById(R.id.etAddRecipeNama);
        etBahan = findViewById(R.id.etAddRecipeBahan);
        etLangkah = findViewById(R.id.etAddRecipeLangkah);
        spinner = findViewById(R.id.spinnerAddRecipe);

        progressDialog = new ProgressDialog(AddRecipeActivity.this);

        if (getIntent().hasExtra("user")){
            user = getIntent().getParcelableExtra("user");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_loginregister, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.option_logreg_home){
            Intent toHome = new Intent(AddRecipeActivity.this, MainActivity.class);
            toHome.putExtra("user", user);
            startActivity(toHome);
        }
        return super.onOptionsItemSelected(item);
    }

    public void addRecipeClick(View v){
        if (v.getId() == R.id.btnAddRecipe){
//            Toast.makeText(getApplicationContext(), spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
            addRecipe();
        }
    }

    void addRecipe(){
        String nama = etNama.getText().toString();
        String bahan = etBahan.getText().toString();
        String langkah = etLangkah.getText().toString();
        String kategori = spinner.getSelectedItem().toString();

        if (nama.isEmpty()){
            etNama.setError("Nama resep perlu diisi!");
            etNama.requestFocus();
            return;
        }
        if(bahan.isEmpty()){
            etBahan.setError("Bahan-bahan perlu diisi!");
            etBahan.requestFocus();
            return;
        }
        if (langkah.isEmpty()){
            etLangkah.setError("Langkah-langkah perlu diisi!");
            etLangkah.requestFocus();
            return;
        }

        int idKategori = getSpinnerValuePosition(spinner, kategori) + 1;

        doAddRecipeToServer(nama, ""+user.getId(), ""+idKategori, bahan, langkah);
    }

    public void doAddRecipeToServer(final String title, final String idUser, final String idCategory, final String ingredients, final String steps){
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
                            clearInput();
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
                    params.put("function", "addRecipe");
                    params.put("title", title);
                    params.put("id_user", idUser);
                    params.put("id_category", idCategory);
                    params.put("ingredients", ingredients);
                    params.put("steps", steps);
                    return params;
                }
            };

            VolleyConnection.getInstance(AddRecipeActivity.this).addToRequestQueue(stringRequest);

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

    void clearInput(){
        etNama.setText("");
        etBahan.setText("");
        etLangkah.setText("");
        spinner.setSelection(0);
    }
}