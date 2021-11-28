package id.ac.cookbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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

        }else if (v.getId() == R.id.btnRegisterLogin){
            Intent toLogin = new Intent(Register.this, Login.class);
            startActivity(toLogin);
        }
    }
}