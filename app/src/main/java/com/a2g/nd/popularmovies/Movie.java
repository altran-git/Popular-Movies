package com.a2g.nd.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ND on 5/6/2016.
 */
public class Movie implements Parcelable{
    String imagePath;
    String origTitle;
    String overview;
    String voteAvg;
    String releaseDate;

    public Movie(String imagePath, String origTitle, String overview, String voteAvg, String releaseDate)
    {
        this.imagePath = imagePath;
        this.origTitle = origTitle;
        this.overview = overview;
        this.voteAvg = voteAvg;
        this.releaseDate = releaseDate;
    }

    private Movie(Parcel in){
        imagePath = in.readString();
        origTitle = in.readString();
        overview = in.readString();
        voteAvg = in.readString();
        releaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(imagePath);
        parcel.writeString(origTitle);
        parcel.writeString(overview);
        parcel.writeString(voteAvg);
        parcel.writeString(releaseDate);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }

    };
}
