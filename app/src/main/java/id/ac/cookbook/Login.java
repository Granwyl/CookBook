package id.ac.cookbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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

        }else if (v.getId() == R.id.btnLoginRegister){
            Intent toRegister = new Intent(Login.this, Register.class);
            startActivity(toRegister);
        }
    }
}