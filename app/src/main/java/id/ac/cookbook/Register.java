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
import android.os.Looper;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import id.ac.cookbook.data.AppDatabaseLogged;
import id.ac.cookbook.data.Logged;
import id.ac.cookbook.data.User;
import id.ac.cookbook.volley.DbContract;
import id.ac.cookbook.volley.VolleyConnection;

public class Register extends AppCompatActivity {
    EditText etUsername, etPassword, etConfirm, etEmail;
    User user;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etRegisterUsername);
        etPassword = findViewById(R.id.etRegisterPassword);
        etConfirm = findViewById(R.id.etRegisterConfirm);
        etEmail = findViewById(R.id.etRegisterEmail);

        progressDialog = new ProgressDialog(Register.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_loginregister, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.option_logreg_home){
            Intent toHome = new Intent(Register.this, MainActivity.class);
            startActivity(toHome);
        }
        return super.onOptionsItemSelected(item);
    }

    public void registerClick(View v){
        if (v.getId() == R.id.btnRegister){
            registerUser();
        }else if (v.getId() == R.id.btnRegisterLogin){
            Intent toLogin = new Intent(Register.this, Login.class);
            startActivity(toLogin);
        }
    }

    public void registerUser(){
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirm = etConfirm.getText().toString().trim();
        if (username.isEmpty()){
            etUsername.setError("Username perlu diisi!");
            etUsername.requestFocus();
            return;
        }

        if (email.isEmpty()){
            etEmail.setError("Email perlu diisi!");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("Emailnya yang valid dong!");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()){
            etPassword.setError("Password perlu diisi!");
            etPassword.requestFocus();
            return;
        }

        if (confirm.isEmpty()){
            etConfirm.setError("Konfirmasi Password perlu diisi!");
            etConfirm.requestFocus();
            return;
        }

        if (!confirm.equals(password)){
            etConfirm.setError("Konfirmasi tidak sama!");
            etConfirm.requestFocus();
            return;
        }

        createDataToServer(username, password, email);
    }

    public void createDataToServer(final String username, final String password, final String email){
        if (checkNetworkConnection()){
            progressDialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_MASTER_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
//                        String resp = jsonObject.getString("server_response");
//                        if (resp.equals("[{\"status\":\"OK\"}]")){
//                            Toast.makeText(getApplicationContext(), "Register berhasil!", Toast.LENGTH_SHORT).show();
//                            user = new User(username, password, email);
//                            Intent toHome = new Intent(Register.this, MainActivity.class);
//                            toHome.putExtra("user", user);
//                            startActivity(toHome);
//                        }else{
//                            Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_SHORT).show();
//                        }

                        int code = jsonObject.getInt("code");
                        String message = jsonObject.getString("message");
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        if (code == 1){
                            JSONArray jsonArray = jsonObject.getJSONArray("datauser");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject userObj = jsonArray.getJSONObject(i);
                                user = new User(
                                        userObj.getInt("id"),
                                        userObj.getString("username"),
                                        userObj.getString("password"),
                                        userObj.getString("email")
                                );
                            }
                            Logged logged = new Logged(user.getId());
                            new AddLoggedAsync3(logged, getApplicationContext(), new AddLoggedAsync3.AddLoggedCallback3() {
                                @Override
                                public void preExecute() {

                                }

                                @Override
                                public void postExecute(String message) {
                                    // Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                    Intent toHome = new Intent(Register.this, MainActivity.class);
                                    toHome.putExtra("user", user);
                                    startActivity(toHome);
                                }
                            }).execute();
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
//                    params.put("username", username);
//                    params.put("password", password);
//                    params.put("email", email);

                    params.put("function", "register");
                    params.put("username", username);
                    params.put("password", password);
                    params.put("email", email);
                    return params;
                }
            };

            VolleyConnection.getInstance(Register.this).addToRequestQueue(stringRequest);
            
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
}

class AddLoggedAsync3{
    private final WeakReference<Context> weakContext;
    private final WeakReference<AddLoggedCallback3> weakCallback;
    private Logged logged;

    public AddLoggedAsync3(Logged logged,
                          Context context,
                          AddLoggedCallback3 callback){
        this.logged = logged;
        this.weakContext = new WeakReference<>(context);
        this.weakCallback = new WeakReference<>(callback);
    }

    void execute(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        weakCallback.get().preExecute();
        executorService.execute(() -> {
            Context context = weakContext.get();
            AppDatabaseLogged appDatabaseLogged = AppDatabaseLogged.getAppDatabase(context);

            appDatabaseLogged.loggedDao().insertLogged(logged);

            handler.post(() -> {
                String successMsg = "Logged Inserted";
                weakCallback.get().postExecute(successMsg);
            });
        });
    }

    interface AddLoggedCallback3{
        void preExecute();
        void postExecute(String message);
    }
}