package id.ac.cookbook.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import id.ac.cookbook.data.Recipe;
import id.ac.cookbook.data.User;

@Database(entities = {User.class, Recipe.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract RecipeDao recipeDao();
    public static AppDatabase INSTANCE;

    public static AppDatabase getAppDatabase(Context context){
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(
                    context,
                    AppDatabase.class,
                    "CookBookDB"
            ).build();
        }
        return INSTANCE;
    };
}
