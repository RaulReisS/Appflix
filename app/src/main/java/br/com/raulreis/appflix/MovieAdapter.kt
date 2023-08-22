package br.com.raulreis.appflix

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import br.com.raulreis.appflix.model.Movie
import br.com.raulreis.appflix.util.DownloadImageTask
import com.squareup.picasso.Picasso

// Horizontal
class MovieAdapter(
    private val movies: List<Movie>,
    @LayoutRes private val layoutId: Int
    ): RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
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
            //Picasso.get().load(movie.coverUrl).into(imgCover)
            DownloadImageTask(object : DownloadImageTask.Callback {
                override fun onResult(bitmap: Bitmap) {
                    imgCover.setImageBitmap(bitmap)
                }
            }).execute(movie.coverUrl)
        }
    }
}