package com.android.example.popularmovies2;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.android.example.popularmovies2.database.AppDatabase;
import com.android.example.popularmovies2.database.Movie;
import com.android.example.popularmovies2.database.Review;
import com.android.example.popularmovies2.database.Trailer;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.net.URL;

import static com.android.example.popularmovies2.MovieNetworkUtils.isOnline;

public class DetailActivity extends AppCompatActivity {

    public static final String TRAILER_BASE_URL = "http://youtube.com/watch?v=";
    private AppDatabase mDb;
    private Trailer[] mMovieTrailers;
    private Review[] mMovieReviews;
    String movieId;
    String imageUrl, title, overview, popularity, viewCount, releaseDate;
    private LinearLayout mMovieListLinearLayout;
    private LinearLayout mReviewListLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mMovieListLinearLayout = findViewById(R.id.trailer_list);
        mReviewListLinearLayout = findViewById(R.id.reviews_list);

        ImageView detailImageView = findViewById(R.id.iv_movie_detail);
        TextView titleTextView = findViewById(R.id.tv_detail_title);
        TextView overviewTextView = findViewById(R.id.tv_detail_overview);
        TextView popularityTextView = findViewById(R.id.tv_detail_popularity);
        TextView viewCountTextView = findViewById(R.id.tv_detail_view_count);
        TextView releaseDateTextView = findViewById(R.id.tv_detail_release_date);
        Button favButton = findViewById(R.id.fav_button);
        Intent intent = getIntent();
        if(intent.hasExtra(Intent.EXTRA_TEXT)){
            movieId = intent.getStringExtra("key");
            int mId = Integer.parseInt(movieId);
            imageUrl = intent.getStringExtra("movie_image");
            title = intent.getStringExtra("movie_title");
            overview = intent.getStringExtra("movie_overview");
            popularity = intent.getStringExtra("movie_popularity");
            viewCount = intent.getStringExtra("movie_view_count");
            releaseDate = intent.getStringExtra("movie_release_date");

            titleTextView.setText(title);
            popularityTextView.setText("Popularity: "+ popularity);
            viewCountTextView.setText("View Count: "+ viewCount);
            releaseDateTextView.setText(releaseDate);
            overviewTextView.setText(overview);
            Picasso.get()
                    .load(imageUrl).fit().centerInside()
                    .into(detailImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.v("Movie", "Success");
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.v("Movie", e.getMessage());
                        }
                    });

            mDb = AppDatabase.getInstance(getApplicationContext());

            new MovieTrailersAsyncTask().execute(movieId);
            new MovieReviewsAsyncTask().execute(movieId);
        }
    }

    public void onMakeAdFavButtonClicked(View view) {
        final Movie favMovie = new Movie(movieId, imageUrl, title, overview,popularity,viewCount, releaseDate);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mDb.favMovieDao().insertFabMovie(favMovie);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DetailActivity.this, "Movie added to favorite collection", Toast.LENGTH_SHORT).show();
                        }
                    });
                    finish();
                }
                catch(SQLiteConstraintException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DetailActivity.this, "Movie already in your favorite collection", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //do nothing
                }
            }
        });

    }

    private class MovieTrailersAsyncTask  extends AsyncTask<String, Void, Trailer[]> {
        @Override
        protected Trailer[] doInBackground(String... strings) {
            URL trailerRequestUrl = MovieNetworkUtils.buildTrailerUrl(strings[0]);
            try {
                if(isOnline()){
                    String jsonTrailerResponse = MovieNetworkUtils.getResponseFromHttpUrl(trailerRequestUrl);
                    //String jsonTrailerResponse = MovieNetworkUtils.getResponseFromAssetFolder(DetailActivity.this,"videos.json");
                    mMovieTrailers = MovieJsonUtils.getMovieTrailerListFromJson(jsonTrailerResponse);
                }
                return mMovieTrailers;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Trailer[] movieTrailers) {
            if(movieTrailers != null){
                loadTrailerUI();
            }else {
                //Nothing
            }
        }
    }

    private void loadTrailerUI() {
        if(mMovieTrailers.length == 0){
            TextView noTrailers = new TextView(this);
            noTrailers.setText(R.string.no_trailers);
            noTrailers.setPadding(0, 0, 0, 50);
            noTrailers.setTextSize(15);
            mMovieListLinearLayout.addView(noTrailers);
        }else{
            for(int i = 0; i< mMovieTrailers.length; i++){
                Button trailerButton = new Button(this);
                trailerButton.setText(mMovieTrailers[i].getTitle());
                trailerButton.setPadding(0, 30, 0, 30);
                trailerButton.setTextSize(15);
                trailerButton.setBackgroundColor(0x00000000);
                trailerButton.setTextColor(0xFFFF0000);
                final String trailerUrl = TRAILER_BASE_URL + mMovieTrailers[i].getKey();
                trailerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri youtubeLink = Uri.parse(trailerUrl);
                        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, youtubeLink);
                        if (youtubeIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(youtubeIntent);
                        }
                    }
                });
                mMovieListLinearLayout.addView(trailerButton);
            }
        }
    }

    private class MovieReviewsAsyncTask  extends AsyncTask<String, Void, Review[]> {
        @Override
        protected Review[] doInBackground(String... strings) {
            URL reviewRequestUrl = MovieNetworkUtils.buildReviewUrl(strings[0]);
            try {
                if(isOnline()){
                    String jsonReviewResponse = MovieNetworkUtils.getResponseFromHttpUrl(reviewRequestUrl);
                    //String jsonReviewResponse = MovieNetworkUtils.getResponseFromAssetFolder(DetailActivity.this,"reviews.json");
                    mMovieReviews = MovieJsonUtils.getMovieReviewListFromJson(jsonReviewResponse);
                }
                return mMovieReviews;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Review[] movieReviews) {
            if(movieReviews != null){
                loadReviewUI();
            }else {
                //Nothing
            }
        }
    }

    private void loadReviewUI() {
        if(mMovieReviews.length == 0){
            findViewById(R.id.author_text).setVisibility(View.GONE);
            findViewById(R.id.content_text).setVisibility(View.GONE);

            TextView noReviews = new TextView(this);
            noReviews.setText("No reviews available for this movie.");
            noReviews.setPadding(0, 0, 0, 50);
            noReviews.setTextSize(15);
            mReviewListLinearLayout.addView(noReviews);
        }else{
            TextView author = findViewById(R.id.author_text);
            TextView content = findViewById(R.id.content_text);
            author.setText(mMovieReviews[0].getAuthor());

            for(int i = 0; i< mMovieReviews.length; i++){
                //Button trailerItem = new Button(this);
                TextView reviewItem = new TextView(this);
                String movieContent = mMovieReviews[i].getContent();
                reviewItem.setText(mMovieReviews[i].getAuthor() + " -> " + movieContent.substring(0, Math.min(movieContent.length(), 200)));
                reviewItem.setPadding(0, 30, 0, 30);
                reviewItem.setTextSize(15);
                final String url = mMovieReviews[i].getUrl();
                Button moreReviewButton= new Button(this);
                moreReviewButton.setText("Click for complete review ...");
                moreReviewButton.setTextSize(15);
                moreReviewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri reviewUrl = Uri.parse(url);
                        Intent moreReviewIntent = new Intent(Intent.ACTION_VIEW, reviewUrl);
                        if (moreReviewIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(moreReviewIntent);
                        }
                    }
                });
                mReviewListLinearLayout.addView(reviewItem);
                mReviewListLinearLayout.addView(moreReviewButton);
            }
        }
    }

}