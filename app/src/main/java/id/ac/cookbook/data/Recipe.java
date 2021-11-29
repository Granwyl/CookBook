package id.ac.cookbook.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipes")
public class Recipe implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    int id;
    private String nama;
    private String usernameCreator;

    public String getUsernameCreator() {
        return usernameCreator;
    }

    public void setUsernameCreator(String usernameCreator) {
        this.usernameCreator = usernameCreator;
    }

    private String kategori; // Food, Beverage, Side Dish
    private String textBahan;
    private String textLangkah;
    private int status; // 0 not published, 1 published

    public Recipe(String nama, String usernameCreator, String kategori, String textBahan, String textLangkah, int status) {
        this.nama = nama;
        this.usernameCreator = usernameCreator;
        this.kategori = kategori;
        this.textBahan = textBahan;
        this.textLangkah = textLangkah;
        this.status = status;
    }

    protected Recipe(Parcel in) {
        id = in.readInt();
        nama = in.readString();
        usernameCreator = in.readString();
        kategori = in.readString();
        textBahan = in.readString();
        textLangkah = in.readString();
        status = in.readInt();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getTextBahan() {
        return textBahan;
    }

    public void setTextBahan(String textBahan) {
        this.textBahan = textBahan;
    }

    public String getTextLangkah() {
        return textLangkah;
    }

    public void setTextLangkah(String textLangkah) {
        this.textLangkah = textLangkah;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(nama);
        parcel.writeString(usernameCreator);
        parcel.writeString(kategori);
        parcel.writeString(textBahan);
        parcel.writeString(textLangkah);
        parcel.writeInt(status);
    }
}
