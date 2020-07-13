package com.android.example.popularmovies2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.popularmovies2.database.Movie;
import com.squareup.picasso.Picasso;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    final private MovieOnClickListener mMovieOnClickListener;

    private Movie[] mMovieList;

    public MovieAdapter(Movie[] movies, MovieOnClickListener listener) {
        mMovieList = movies;
        mMovieOnClickListener = listener;
    }

    public interface MovieOnClickListener {
        void onMovieClick(int clickedMovieIndex);
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item, parent,false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        holder.bind(holder, position);
    }

    @Override
    public int getItemCount() {
        if (mMovieList == null) return 0;
        return mMovieList.length;
    }

    public void setMoviesData(Movie[] movies){
        mMovieList = movies;
        notifyDataSetChanged();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final ImageView mMovieImageView;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            TextView titleTextView = itemView.findViewById(R.id.tv_detail_title);
            mMovieImageView = itemView.findViewById(R.id.iv_movie);
            itemView.setOnClickListener(this);
        }

        private void bind(MovieViewHolder holder, int position){
            String imageUrl = mMovieList[position].getImageUrl();
            Picasso.get()
                    .load(imageUrl).fit().centerInside()
                    .into(holder.mMovieImageView);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mMovieOnClickListener.onMovieClick(adapterPosition);
        }
    }

}
