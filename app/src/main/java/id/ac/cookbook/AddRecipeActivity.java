package id.ac.cookbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import id.ac.cookbook.data.Recipe;
import id.ac.cookbook.data.User;
import id.ac.cookbook.db.AppDatabase;
import id.ac.cookbook.db.RecipeDao;

public class AddRecipeActivity extends AppCompatActivity {
    EditText etNama, etBahan, etLangkah;
    User user;
    Spinner spinner;
    Recipe recipe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        etNama = findViewById(R.id.etAddRecipeNama);
        etBahan = findViewById(R.id.etAddRecipeBahan);
        etLangkah = findViewById(R.id.etAddRecipeLangkah);
        spinner = findViewById(R.id.spinnerAddRecipe);

        if (getIntent().hasExtra("user")){
            user = getIntent().getParcelableExtra("user");
        }
    }

    public void addRecipeClick(View v){
        if (v.getId() == R.id.btnAddRecipe){
//            Toast.makeText(getApplicationContext(), spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
            addRecipe();
        }else if (v.getId() == R.id.btnAddRecipeHome){
            Intent toHome = new Intent(AddRecipeActivity.this, MainActivity.class);
            toHome.putExtra("user", user);
            startActivity(toHome);
        }
    }

    void addRecipe(){
        if (TextUtils.isEmpty(etNama.getText()) ||
                TextUtils.isEmpty(etBahan.getText()) ||
                TextUtils.isEmpty(etLangkah.getText())
        ){
            Toast.makeText(getApplicationContext(), "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
        }else{
            String nama = etNama.getText().toString();
            String bahan = etBahan.getText().toString();
            String langkah = etLangkah.getText().toString();
            String kategori = spinner.getSelectedItem().toString();
            recipe = new Recipe(nama, user.getUsername(), kategori, bahan, langkah, 0);

            new AddRecipeAsync(recipe,
                    this,
                    new AddRecipeAsync.AddRecipeCallback() {
                @Override
                public void preExecute() {

                }

                @Override
                public void postExecute(String message) {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    Intent toHome = new Intent(AddRecipeActivity.this, MainActivity.class);
                    toHome.putExtra("user", user);
                    startActivity(toHome);
                }
            }).execute();
        }
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
}

class AddRecipeAsync{
    private final WeakReference<Context> weakContext;
    private final WeakReference<AddRecipeCallback> weakCallback;
    private Recipe recipe;

    public AddRecipeAsync(Recipe recipe,
                          Context context,
                          AddRecipeCallback callback){
        this.weakContext = new WeakReference<>(context);
        this.weakCallback = new WeakReference<>(callback);
        this.recipe = recipe;
    }

    void execute(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        weakCallback.get().preExecute();
        executorService.execute(() -> {
            Context context = weakContext.get();
            AppDatabase appDatabase = AppDatabase.getAppDatabase(context);

            appDatabase.recipeDao().insertRecipe(recipe);

            handler.post(() -> {
                String successMessage = "New Recipe Added!";
                weakCallback.get().postExecute(successMessage);
            });
        });
    }

    interface AddRecipeCallback{
        void preExecute();
        void postExecute(String message);
    }
}