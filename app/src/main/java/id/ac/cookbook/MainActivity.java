package id.ac.cookbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getIntent().hasExtra("user")){
            getMenuInflater().inflate(R.menu.option_user, menu);
        }else{
            getMenuInflater().inflate(R.menu.option_anonymous, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (getIntent().hasExtra("user")){

        }else{
            if (item.getItemId() == R.id.option_login){
                Intent toLogin = new Intent(MainActivity.this, Login.class);
                startActivity(toLogin);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}