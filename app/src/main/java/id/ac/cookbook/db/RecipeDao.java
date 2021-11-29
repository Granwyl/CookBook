package id.ac.cookbook.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import id.ac.cookbook.data.Recipe;

@Dao
public interface RecipeDao {
    @Query("select * from recipes where status = 1")
    List<Recipe> getAllPublishedRecipe();

    @Query("select * from recipes where usernameCreator = :paramCreator")
    List<Recipe> getMyRecipe(String paramCreator);

    @Query("select * from recipes where id = :paramID")
    List<Recipe> getRecipe(int paramID);

    @Insert
    void insertRecipe(Recipe newRecipe);

    @Update
    void updateRecipe(Recipe recipe);

    @Delete
    void deleteRecipe(Recipe recipe);
}
