package id.ac.cookbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import id.ac.cookbook.data.Recipe;
import id.ac.cookbook.data.User;

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