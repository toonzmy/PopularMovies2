package com.android.example.popularmovies2;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class MovieNetworkUtils {

    public static final String MOVIE_BASE_URL = "https://api.themoviedb.org/";
    public static final String API_KEY = "api_key";
    public static final String API_VALUE = "<REPLACE WITH YOUR OWN API KEY>";
    private static final String API_VERSION = "3";
    private static final String MOVIE = "movie";
    private static final String VIDEOS = "videos";
    private static final String REVIEWS = "reviews";

    public static URL buildMovieUrl(String category){
        Uri buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(API_VERSION)
                .appendPath(MOVIE)
                .appendPath(category)
                .appendQueryParameter(API_KEY, API_VALUE)
                .build();
        URL url = null;
        try{
            url = new URL(buildUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }

        Log.v("MOVIE", "Built Movie URI " + url);
        return url;
    }


    public static URL buildTrailerUrl(String id){
        Uri buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(API_VERSION)
                .appendPath(MOVIE)
                .appendPath(id)
                .appendPath(VIDEOS)
                .appendQueryParameter(API_KEY, API_VALUE)
                .build();
        URL url = null;
        try{
            url = new URL(buildUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }

        Log.v("MOVIE", "Built Trailer URI " + url);
        return url;
    }

    public static URL buildReviewUrl(String id){
        Uri buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(API_VERSION)
                .appendPath(MOVIE)
                .appendPath(id)
                .appendPath(REVIEWS)
                .appendQueryParameter(API_KEY, API_VALUE)
                .build();
        URL url = null;
        try{
            url = new URL(buildUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }

        Log.v("MOVIE", "Built Review URI " + url);
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if(hasInput){
                return scanner.next();
            } else {
                return  null;
            }
        }finally {
            urlConnection.disconnect();
        }
    }

    public static String getResponseFromAssetFolder(Context context, String filename) throws IOException {
        try {
            InputStream in = context.getAssets().open(filename);

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if(hasInput){
                return scanner.next();
            } else {
                return  null;
            }
        }finally {
            //urlConnection.disconnect();
        }
    }

    //From StackOverflow post -> https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }
}
