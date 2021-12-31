package id.ac.cookbook.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Logged.class}, version = 1)
public abstract class AppDatabaseLogged extends RoomDatabase {
    public abstract LoggedDao loggedDao();
    public static AppDatabaseLogged INSTANCE;

    public static AppDatabaseLogged getAppDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context, AppDatabaseLogged.class, "dbcbLogged").build();
        }
        return INSTANCE;
    }
}
