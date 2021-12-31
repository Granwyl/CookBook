package id.ac.cookbook.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "logged")
public class Logged {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int idUser;

    public Logged(int idUser) {
        this.idUser = idUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
}
