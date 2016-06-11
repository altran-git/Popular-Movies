package com.a2g.nd.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ND on 5/6/2016.
 */
public class Movie implements Parcelable{
    String imagePath;
    String origTitle;
    String overview;
    String voteAvg;
    String releaseDate;
    int id;

    List<String> trailers;
    List<String> reviews;
    List<String> reviewers;

    public Movie(String imagePath, String origTitle, String overview, String voteAvg, String releaseDate, int id)
    {
        this.imagePath = imagePath;
        this.origTitle = origTitle;
        this.overview = overview;
        this.voteAvg = voteAvg;
        this.releaseDate = releaseDate;
        this.id = id;
        this.trailers = new ArrayList<String>();
        this.reviews = new ArrayList<String>();
        this.reviewers = new ArrayList<String>();
    }

    private Movie(Parcel in){
        imagePath = in.readString();
        origTitle = in.readString();
        overview = in.readString();
        voteAvg = in.readString();
        releaseDate = in.readString();
        id = in.readInt();
        trailers = in.readArrayList(String.class.getClassLoader());
        reviews = in.readArrayList(String.class.getClassLoader());
        reviewers = in.readArrayList(String.class.getClassLoader());
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
        parcel.writeInt(id);
        parcel.writeStringList(trailers);
        parcel.writeStringList(reviews);
        parcel.writeStringList(reviewers);
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
