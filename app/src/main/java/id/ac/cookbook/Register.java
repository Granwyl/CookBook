package id.ac.cookbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
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

public class Register extends AppCompatActivity {
    EditText etUsername, etPassword, etConfirm;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etRegisterUsername);
        etPassword = findViewById(R.id.etRegisterPassword);
        etConfirm = findViewById(R.id.etRegisterConfirm);
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

    void registerUser(){
        if (TextUtils.isEmpty(etUsername.getText()) ||
                TextUtils.isEmpty(etPassword.getText()) ||
                TextUtils.isEmpty(etConfirm.getText())
        ){
            Toast.makeText(getApplicationContext(), "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
        }else{
            if (etPassword.getText().toString().equals(etConfirm.getText().toString())){
                user = new User(etUsername.getText().toString(), etPassword.getText().toString());
                new RegisterAddUserAsync(user,
                        this,
                        new RegisterAddUserAsync.RegisterAddUserCallback() {
                    @Override
                    public void preExecute() {

                    }

                    @Override
                    public void postExecute(String message) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        if (message.equals("Register Success!")){
                            Intent toHome = new Intent(Register.this, MainActivity.class);
                            toHome.putExtra("user", user);
                            startActivity(toHome);
                        }
                    }
                }).execute();
            }else{
                Toast.makeText(getApplicationContext(), "Konfirmasi password tidak sesuai!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

class RegisterAddUserAsync{
    private final WeakReference<Context> weakContext;
    private final WeakReference<RegisterAddUserCallback> weakCallback;
    private User user;

    public RegisterAddUserAsync(User user,
                                Context context,
                                RegisterAddUserCallback callback){
        this.weakContext = new WeakReference<>(context);
        this.weakCallback = new WeakReference<>(callback);
        this.user = user;
    }

    void execute(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        weakCallback.get().preExecute();
        executorService.execute( () -> {
            Context context = weakContext.get();
            AppDatabase appDatabase = AppDatabase.getAppDatabase(context);

            List<User> resultUsers = appDatabase.userDao().getUsersByUsername(user.getUsername());
            if (resultUsers.size() == 0){
                appDatabase.userDao().insertUser(user);
                handler.post( () -> {
                    String successMessage = "Register Success!";
                    weakCallback.get().postExecute(successMessage);
                });
            }else {
                handler.post( () -> {
                    String successMessage = "Username Taken!";
                    weakCallback.get().postExecute(successMessage);
                });
            }
        });
    }

    interface RegisterAddUserCallback {
        void preExecute();
        void postExecute(String message);
    }
}