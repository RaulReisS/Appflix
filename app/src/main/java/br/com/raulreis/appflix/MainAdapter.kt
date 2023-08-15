package br.com.raulreis.appflix

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import br.com.raulreis.appflix.model.Movie

class MainAdapter(private val movies: List<Movie>): RecyclerView.Adapter<MainAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
    }

    inner class MovieViewHolder(itemView: View) :  RecyclerView.ViewHolder(itemView) {
        fun bind (movie: Movie) {
            val imgCover: ImageView = itemView.findViewById(R.id.imgCover)
            imgCover.setImageResource(movie.converUrl)
        }
    }
}