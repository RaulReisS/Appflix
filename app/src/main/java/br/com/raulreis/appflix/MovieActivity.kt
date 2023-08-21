package br.com.raulreis.appflix

import android.graphics.drawable.LayerDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.raulreis.appflix.model.Movie

class MovieActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        val txvTitle: TextView = findViewById(R.id.txvMovieTitle)
        val txvDesc: TextView = findViewById(R.id.txvMovieDesc)
        val txvCast: TextView = findViewById(R.id.txvMovieCast)
        val rvSimilar : RecyclerView = findViewById(R.id.rvMovieSimilar)

        txvTitle.text = "Batman Begins"
        txvDesc.text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla nec libero est. Nullam vitae semper odio. Aenean malesuada sed neque non pellentesque. Mauris rhoncus ornare posuere. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam accumsan libero eget mi porttitor, at rhoncus augue ultrices. Donec mollis nisi a purus eleifend, quis faucibus velit congue"
        txvCast.text = getString(R.string.cast, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, Nulla nec libero est, Nullam vitae semper odio, Aenean malesuada sed neque non pellentesque, Mauris rhoncus ornare posuere")

        val movies = mutableListOf<Movie>()
        for(i in 0 until 15) {
            val movie = Movie(R.drawable.movie)
            movies.add(movie)
        }

        rvSimilar.layoutManager = GridLayoutManager(this@MovieActivity, 3)
        rvSimilar.adapter = MovieAdapter(movies, R.layout.movie_item_similar)

        val toolbar: Toolbar = findViewById(R.id.tbMovie)
        setSupportActionBar(toolbar)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

        // Busquei o desenhavel (layer-list)
        val layerDrawable: LayerDrawable = ContextCompat.getDrawable(this@MovieActivity, R.drawable.shadows) as LayerDrawable

        // Busquei a imagem do filme
        val movieCover = ContextCompat.getDrawable(this, R.drawable.movie4)

        // Atribuí a esse layer-list a nova imagem do filme
        layerDrawable.setDrawableByLayerId(R.id.coverDrawable, movieCover)

        // Set no ImageView
        val imgCover: ImageView = findViewById(R.id.imgMovie)
        imgCover.setImageDrawable(layerDrawable)

    }
}