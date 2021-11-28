package id.ac.cookbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import id.ac.cookbook.data.User;
import id.ac.cookbook.db.AppDatabase;

public class Login extends AppCompatActivity {
    EditText etUsername, etPassword;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etLoginUsername);
        etPassword = findViewById(R.id.etLoginPassword);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_loginregister, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.option_logreg_home){
            Intent toHome = new Intent(Login.this, MainActivity.class);
            startActivity(toHome);
        }
        return super.onOptionsItemSelected(item);
    }

    public void loginClick(View v){
        if (v.getId() == R.id.btnLogin){
            loginUser();
        }else if (v.getId() == R.id.btnLoginRegister){
            Intent toRegister = new Intent(Login.this, Register.class);
            startActivity(toRegister);
        }
    }

    void loginUser(){
        if (TextUtils.isEmpty(etUsername.getText()) ||
                TextUtils.isEmpty(etPassword.getText())
        ){
            Toast.makeText(getApplicationContext(), "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
        }else{
            user = new User(etUsername.getText().toString(), etPassword.getText().toString());
            new LoginUserAsync(user,
                    this,
                    new LoginUserAsync.LoginUserCallback() {
                @Override
                public void preExecute() {

                }

                @Override
                public void postExecute(String message) {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    if (message.equals("Login Success!")){
                        Intent toHome = new Intent(Login.this, MainActivity.class);
                        toHome.putExtra("user", user);
                        startActivity(toHome);
                    }
                }
            }).execute();
        }
    }
}

class LoginUserAsync{
    private final WeakReference<Context> weakContext;
    private final WeakReference<LoginUserCallback> weakCallback;
    private User user;

    public LoginUserAsync(User user, Context context, LoginUserCallback callback){
        this.weakContext = new WeakReference<>(context);
        this.weakCallback = new WeakReference<>(callback);
        this.user = user;
    }

    void execute(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        weakCallback.get().preExecute();
        executorService.execute(() -> {
            Context context = weakContext.get();
            AppDatabase appDatabase = AppDatabase.getAppDatabase(context);

            List<User> resultUsers = appDatabase.userDao().getUsersByUsernamePassword(user.getUsername(), user.getPassword());
            if (resultUsers.size() == 0){
                handler.post(()->{
                    String successMessage = "User Not Found!";
                    weakCallback.get().postExecute(successMessage);
                });
            }else{
                handler.post(()->{
                    String successMessage = "Login Success!";
                    weakCallback.get().postExecute(successMessage);
                });
            }
        });
    }


    interface LoginUserCallback{
        void preExecute();
        void postExecute(String message);
    }
}