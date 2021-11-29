package id.ac.cookbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import id.ac.cookbook.data.Recipe;
import id.ac.cookbook.data.RecipeAdapter;
import id.ac.cookbook.data.User;
import id.ac.cookbook.db.AppDatabase;

public class MyRecipeActivity extends AppCompatActivity {
    User user;
    RecyclerView rvData;
    RecipeAdapter recipeAdapter;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipe);

        rvData = findViewById(R.id.rvMyRecipe);
        context = this;

        if (getIntent().hasExtra("user")){
            user = getIntent().getParcelableExtra("user");
            new LoadMyRecipeAsync(user.getUsername(),
                    context,
                    new LoadMyRecipeAsync.LoadMyRecipeCallback() {
                @Override
                public void preExecute() {

                }

                @Override
                public void postExecute(List<Recipe> listRecipe) {
                    new LoadMyRecipeCreatorAsync(listRecipe,
                            context,
                            new LoadMyRecipeCreatorAsync.LoadMyRecipeCreatorCallback() {
                        @Override
                        public void preExecute() {

                        }

                        @Override
                        public void postExecute(List<Recipe> listRecipe, ArrayList<User> listUser) {
                            setUpRecyclerView(listRecipe, listUser);
                        }
                    }).execute();
                }
            }).execute();
        }
    }

    void setUpRecyclerView(List<Recipe> listRecipe, ArrayList<User> listUser){
        rvData.setLayoutManager(new LinearLayoutManager(this));
        rvData.setHasFixedSize(true);

        ArrayList<Recipe> list = new ArrayList<>();
        list.addAll(listRecipe);

        recipeAdapter = new RecipeAdapter(list, listUser);
        recipeAdapter.setOnItemClickCallback(new RecipeAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(Recipe recipe) {

            }
        });
        rvData.setAdapter(recipeAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_user, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.option_exit){
            Intent toHome = new Intent(MyRecipeActivity.this, MainActivity.class);
            startActivity(toHome);
        }else if (item.getItemId() == R.id.option_add){
            Intent toAdd = new Intent(MyRecipeActivity.this, AddRecipeActivity.class);
            toAdd.putExtra("user", user);
            startActivity(toAdd);
        }else if (item.getItemId() == R.id.option_home){
            Intent toHome = new Intent(MyRecipeActivity.this, MainActivity.class);
            toHome.putExtra("user", user);
            startActivity(toHome);
        }
        return super.onOptionsItemSelected(item);
    }
}

class LoadMyRecipeAsync{
    private final WeakReference<Context> weakContext;
    private final WeakReference<LoadMyRecipeCallback> weakCallback;
    private String idCreator;

    LoadMyRecipeAsync(String idCreator,
                      Context context,
                      LoadMyRecipeCallback callback){
        this.weakContext = new WeakReference<>(context);
        this.weakCallback = new WeakReference<>(callback);
        this.idCreator = idCreator;
    }

    void execute(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        weakCallback.get().preExecute();
        executorService.execute(() -> {
            Context context = weakContext.get();
            AppDatabase appDatabase = AppDatabase.getAppDatabase(context);

            List<Recipe> resultListRecipe;

            resultListRecipe = appDatabase.recipeDao().getMyRecipe(idCreator);


            handler.post(() -> {
                weakCallback.get().postExecute(resultListRecipe);
            });
        });
    }

    interface LoadMyRecipeCallback{
        void preExecute();
        void postExecute(List<Recipe> listRecipe);
    }
}

class LoadMyRecipeCreatorAsync{
    private final WeakReference<Context> weakContext;
    private final WeakReference<LoadMyRecipeCreatorCallback> weakCallback;
    private List<Recipe> listRecipe;

    LoadMyRecipeCreatorAsync(List<Recipe> listRecipe,
                             Context context,
                             LoadMyRecipeCreatorCallback callback){
        this.weakContext = new WeakReference<>(context);
        this.weakCallback = new WeakReference<>(callback);
        this.listRecipe = listRecipe;
    }

    void execute(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        weakCallback.get().preExecute();
        executorService.execute(() -> {
            Context context = weakContext.get();
            AppDatabase appDatabase = AppDatabase.getAppDatabase(context);

            List<User> resultListUser;
            ArrayList<User> listUser = new ArrayList<>();

            for (int i = 0; i < listRecipe.size(); i++){
                resultListUser = appDatabase.userDao().getUsersByUsername(listRecipe.get(i).getUsernameCreator());
                if (resultListUser.size() > 0){
                    listUser.add(resultListUser.get(0));
                }
            }

            handler.post(() -> {
                weakCallback.get().postExecute(listRecipe ,listUser);
            });
        });
    }

    interface LoadMyRecipeCreatorCallback{
        void preExecute();
        void postExecute(List<Recipe> listRecipe, ArrayList<User> listUser);
    }
}