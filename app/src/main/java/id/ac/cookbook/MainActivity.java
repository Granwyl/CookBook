package id.ac.cookbook;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import id.ac.cookbook.data.User;
import id.ac.cookbook.fragments.BookmarkFragment;
import id.ac.cookbook.fragments.HomeFragment;
import id.ac.cookbook.fragments.LoginFragment;
import id.ac.cookbook.fragments.MyRecipeFragment;

public class MainActivity extends AppCompatActivity {
    TextView tvWelcome;
    User user;

    BottomNavigationView botNav;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        tvWelcome = findViewById(R.id.tvMainWelcome);
        botNav = findViewById(R.id.botNavMain);

        if (getIntent().hasExtra("user")){
            user = getIntent().getParcelableExtra("user");
//            tvWelcome.setText("Welcome, " + user.getUsername());
            botNav.getMenu().clear();
            botNav.inflateMenu(R.menu.bot_nav_main_user);
        }

        botNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                if (getIntent().hasExtra("user")){
                    switch (item.getItemId()){
                        case R.id.bot_nav_user_home:
                            fragment = HomeFragment.newInstance(user);
                            break;
                        case R.id.bot_nav_user_myRecipe:
                            fragment = MyRecipeFragment.newInstance(user);
                            break;
                        case R.id.bot_nav_user_myBookmark:
                            fragment = BookmarkFragment.newInstance(user);
                            break;
                        default:
                            fragment = HomeFragment.newInstance(user);
                            break;
                    }
                }else{
                    switch (item.getItemId()){
                        case R.id.bot_nav_main_login:
                            fragment = LoginFragment.newInstance();
                            break;
                        case R.id.bot_nav_main_home:
                            fragment = HomeFragment.newInstance();
                            break;
                        default:
                            fragment = HomeFragment.newInstance();
                            break;
                    }
                }

                try {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragContainerMain, fragment)
                            .commit();
                }catch (Exception e){
                    Log.e("MainActivity", e.getMessage());
                }
                return true;
            }
        });

        if (savedInstanceState == null){
            if (getIntent().hasExtra("fragment")){
                botNav.setSelectedItemId(R.id.bot_nav_user_myRecipe);
            }else if (getIntent().hasExtra("user")){
                botNav.setSelectedItemId(R.id.bot_nav_user_home);
            }else{
                botNav.setSelectedItemId(R.id.bot_nav_main_home);
            }
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                            android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    111);
            return;

        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    112);
            return;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getIntent().hasExtra("user")){
            getMenuInflater().inflate(R.menu.option_user, menu);
        }else{
//            getMenuInflater().inflate(R.menu.option_anonymous, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (getIntent().hasExtra("user")){
            if (item.getItemId() == R.id.option_exit){
                Intent toHome = new Intent(MainActivity.this, MainActivity.class);
                startActivity(toHome);
            }
        }else{
//            if (item.getItemId() == R.id.option_login){
//                Intent toLogin = new Intent(MainActivity.this, Login.class);
//                startActivity(toLogin);
//            }
        }
        return super.onOptionsItemSelected(item);
    }
}