package id.ac.cookbook;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import id.ac.cookbook.data.AppDatabaseLogged;
import id.ac.cookbook.data.Logged;
import id.ac.cookbook.data.User;
import id.ac.cookbook.volley.DbContract;
import id.ac.cookbook.volley.VolleyConnection;

public class SplashScreen extends AppCompatActivity {

    ProgressDialog progressDialog;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(SplashScreen.this);

        //menghilangkan ActionBar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new getLoggedAsync(getApplicationContext(), new getLoggedAsync.getLoggedCallback() {
                    @Override
                    public void preExecute() {

                    }

                    @Override
                    public void postExecute(String message, List<Logged> list) {
//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        if (list.size() > 0){
                            doLoginServer(list.get(0).getIdUser() + "");
                        }else{
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    }
                }).execute();
            }
        }, 4000L); //3000 L = 3 detik
    }

    public void doLoginServer(final String idUser){
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
                        if (code == 1){ // login user
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
                            Intent toHome = new Intent(SplashScreen.this, MainActivity.class);
                            toHome.putExtra("user", user);
                            startActivity(toHome);
                            finish();
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
                    params.put("function", "getUserById");
                    params.put("idUser", idUser);
                    return params;
                }
            };

            VolleyConnection.getInstance(SplashScreen.this).addToRequestQueue(stringRequest);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.cancel();
                }
            }, 500);
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

class getLoggedAsync{
    private final WeakReference<Context> weakContext;
    private final WeakReference<getLoggedCallback> weakCallback;

    public getLoggedAsync(Context context,
                           getLoggedCallback callback){
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

            List<Logged> listLogged = appDatabaseLogged.loggedDao().getAllLogged();

            handler.post(() -> {
                String successMsg = "Logged Got";
                weakCallback.get().postExecute(successMsg, listLogged);
            });
        });
    }

    interface getLoggedCallback{
        void preExecute();
        void postExecute(String message, List<Logged> list);
    }
}