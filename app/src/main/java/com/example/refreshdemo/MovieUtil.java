package com.example.refreshdemo;

public class MovieUtil {
    private String movieUrl;
    private String movieName;

    public MovieUtil(String movieName, String movieUrl) {
        this.movieName = movieName;
        this.movieUrl = movieUrl;
    }

    public String getMovieUrl() {
        return movieUrl;
    }

    public void setMovieUrl(String movieUrl) {
        this.movieUrl = movieUrl;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }


}
