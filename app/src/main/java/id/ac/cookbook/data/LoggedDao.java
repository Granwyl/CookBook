package id.ac.cookbook.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LoggedDao {
    @Query("select * from logged")
    List<Logged> getAllLogged();

    @Query("delete from logged")
    void clearLogged();

    @Insert
    void insertLogged(Logged newLogged);
}
