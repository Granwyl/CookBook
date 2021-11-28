package id.ac.cookbook.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import id.ac.cookbook.data.User;

@Dao
public interface UserDao {
    @Query("select * from users")
    List<User> getAllUsers();

    @Query("select * from users where LOWER(username) = LOWER(:paramUsername)")
    List<User> getUsersByUsername(String paramUsername);

    @Query("select * from users where LOWER(username) = LOWER(:paramUsername) and password = :paramPassword")
    List<User> getUsersByUsernamePassword(String paramUsername, String paramPassword);
    @Insert
    void insertUser(User newUser);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);
}
