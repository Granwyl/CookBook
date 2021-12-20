package id.ac.cookbook.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Rate implements Parcelable {
    private int id;
    private int star;
    private String username;
    private String review;

    public Rate(int id, int star, String username, String review) {
        this.id = id;
        this.star = star;
        this.username = username;
        this.review = review;
    }

    protected Rate(Parcel in) {
        id = in.readInt();
        star = in.readInt();
        username = in.readString();
        review = in.readString();
    }

    public static final Creator<Rate> CREATOR = new Creator<Rate>() {
        @Override
        public Rate createFromParcel(Parcel in) {
            return new Rate(in);
        }

        @Override
        public Rate[] newArray(int size) {
            return new Rate[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(star);
        parcel.writeString(username);
        parcel.writeString(review);
    }
}
