package com.android.example.popularmovies2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.popularmovies2.database.AppDatabase;
import com.android.example.popularmovies2.database.Movie;
import com.android.example.popularmovies2.databinding.ActivityMainBinding;

import java.net.URL;

import static com.android.example.popularmovies2.MovieNetworkUtils.isOnline;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieOnClickListener {

    private TextView mErrorMessageTextView;
    private RecyclerView mMoviesRecyclerView;
    private MovieAdapter mMoviesAdapter;
    private Movie[] mMovieList;

    private ActivityMainBinding mActivityMainBinding;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        mActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mMoviesRecyclerView = mActivityMainBinding.rvMovies;
        mErrorMessageTextView = mActivityMainBinding.tvErrorMessageDisplay;

        RecyclerView.LayoutManager moviesLayoutManager = new GridLayoutManager(this, 2);
        mMoviesRecyclerView.setLayoutManager(moviesLayoutManager);

        mMoviesAdapter = new MovieAdapter(mMovieList, this);
        mMoviesRecyclerView.setAdapter(mMoviesAdapter);

        mMoviesRecyclerView.setHasFixedSize(true);

        mDb = AppDatabase.getInstance(getApplicationContext());

        loadMoviesInBackground("popular");
     }

    private void showMoviesResults(){
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mMoviesRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(){
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mMoviesRecyclerView.setVisibility(View.INVISIBLE);
    }



    private void loadMoviesInBackground(String category) {
        showMoviesResults();
        new MovieAsyncTask().execute(category);
    }

    @Override
    public void onMovieClick(int movieIndex) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        String imageUrl = mMovieList[movieIndex].getImageUrl();
        String title = mMovieList[movieIndex].getTitle();
        String overview = mMovieList[movieIndex].getOverview();
        String popularity = mMovieList[movieIndex].getPopularity();
        String viewCount = mMovieList[movieIndex].getViewCount();
        String releaseDate = mMovieList[movieIndex].getReleaseDate();
        intent.putExtra(Intent.EXTRA_TEXT, movieIndex);
        intent.putExtra("key", mMovieList[movieIndex].getMovieId());
        intent.putExtra("movie_image", imageUrl);
        intent.putExtra("movie_title",title);
        intent.putExtra("movie_overview", overview);
        intent.putExtra("movie_popularity", popularity);
        intent.putExtra("movie_view_count", viewCount);
        intent.putExtra("movie_release_date", releaseDate);
        startActivity(intent);
    }

    //Param, Progress, Result
    public class MovieAsyncTask extends AsyncTask<String, Void, Movie[]>{

        @Override
        protected Movie[] doInBackground(String... params) {

            if(params.length == 0){
                return null;
            }
            String category = params[0];

            URL movieRequestUrl = MovieNetworkUtils.buildMovieUrl(category);

            try {
                if(isOnline()){
                    String jsonMovieResponse = MovieNetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                    //String jsonMovieResponse = MovieNetworkUtils.getResponseFromAssetFolder(MainActivity.this,"movies.json");
                    mMovieList = MovieJsonUtils.getMovieListFromJson(jsonMovieResponse);
                }
                return mMovieList;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(Movie[] movies) {
            if(movies != null){
                showMoviesResults();
                mMoviesAdapter.setMoviesData(movies);
            }else {
                showErrorMessage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int menuItemSelected = item.getItemId();
        if(menuItemSelected == R.id.action_most_popular){
            loadMoviesInBackground("popular");
            return true;
        }else if(menuItemSelected == R.id.action_top_rated){
            loadMoviesInBackground("top_rated");
            return true;
        }else if(menuItemSelected == R.id.action_favorite) {
            //LiveData<Movie[]> movies = mDb.favMovieDao().loadAllFabMovies();
            MovieViewModel viewModel = ViewModelProviders.of(MainActivity.this).get(MovieViewModel.class);
            viewModel.getMovies().observe(this, new Observer<Movie[]>() {
                @Override
                public void onChanged(Movie[] moviesEntries) {
                    mMoviesAdapter.setMoviesData(moviesEntries);
                }
            });

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
