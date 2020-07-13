package com.android.example.popularmovies2;

import com.android.example.popularmovies2.database.Movie;
import com.android.example.popularmovies2.database.Review;
import com.android.example.popularmovies2.database.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieJsonUtils {

    private static final String MOVIE_RESULTS = "results";

    public static Movie[] getMovieListFromJson(String urlStr) throws JSONException {

        final String MOVIE_BASE_URL = "https://image.tmdb.org/t/p/";
        final String MOVIE_POSTER_SIZE = "w342";

        JSONObject movieJson = new JSONObject(urlStr);

        JSONArray movieArray = movieJson.getJSONArray(MOVIE_RESULTS);

        Movie[] movieList = new Movie[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++) {

            JSONObject movie = movieArray.getJSONObject(i);
            String baseURL = MOVIE_BASE_URL + MOVIE_POSTER_SIZE;
            String imagePath = movie.optString("poster_path", "backdrop_path");
            String imageUrl = baseURL + imagePath;
            String title = movie.optString("title");
            String overview = movie.optString("overview");
            String popularity = movie.optString("popularity");
            String voteCount = movie.optString("vote_count");
            String releaseDate = movie.optString("release_date");
            String movieId = movie.optString("id");
            movieList[i] = new Movie(movieId, imageUrl, title, overview, popularity, voteCount, releaseDate);
            //movieList[i].setId(key);
        }

        return movieList;
    }

    public static Trailer[] getMovieTrailerListFromJson(String urlStr) throws JSONException {

        JSONObject trailerJson = new JSONObject(urlStr);
        JSONArray trailerArray = trailerJson.getJSONArray(MOVIE_RESULTS);
        Trailer[] movieTrailers = new Trailer[trailerArray.length()];

        for (int i = 0; i < trailerArray.length(); i++) {
            JSONObject trailer = trailerArray.getJSONObject(i);
            String key = trailer.optString("key");
            String site = trailer.optString("site");
            String title = trailer.optString("name");

            movieTrailers[i] = new Trailer(key, site, title);
        }

        return movieTrailers;
    }

    public static Review[] getMovieReviewListFromJson(String urlStr) throws JSONException {

        JSONObject reviewJson = new JSONObject(urlStr);
        JSONArray reviewArray = reviewJson.getJSONArray(MOVIE_RESULTS);
        Review[] movieReviews = new Review[reviewArray.length()];

        for (int i = 0; i < reviewArray.length(); i++) {
            JSONObject trailer = reviewArray.getJSONObject(i);
            String author = trailer.optString("author");
            String content = trailer.optString("content");
            String url = trailer.optString("url");

            movieReviews[i] = new Review(author, content, url);
        }

        return movieReviews;
    }
}