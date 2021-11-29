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
    private int idCreator;
    private String kategori; // Food, Beverage, Side Dish
    private String textBahan;
    private String textLangkah;
    private int status; // 0 not published, 1 published

    public Recipe(String nama, int idCreator, String kategori, String textBahan, String textLangkah, int status) {
        this.nama = nama;
        this.idCreator = idCreator;
        this.kategori = kategori;
        this.textBahan = textBahan;
        this.textLangkah = textLangkah;
        this.status = status;
    }

    protected Recipe(Parcel in) {
        id = in.readInt();
        nama = in.readString();
        idCreator = in.readInt();
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

    public int getIdCreator() {
        return idCreator;
    }

    public void setIdCreator(int idCreator) {
        this.idCreator = idCreator;
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
        parcel.writeInt(idCreator);
        parcel.writeString(kategori);
        parcel.writeString(textBahan);
        parcel.writeString(textLangkah);
        parcel.writeInt(status);
    }
}
