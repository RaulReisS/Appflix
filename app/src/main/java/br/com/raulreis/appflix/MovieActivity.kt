package br.com.raulreis.appflix

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.raulreis.appflix.model.Movie
import br.com.raulreis.appflix.model.MovieDetail
import br.com.raulreis.appflix.util.DownloadImageTask
import br.com.raulreis.appflix.util.MovieTask

class MovieActivity : AppCompatActivity(), MovieTask.Callback {

    private lateinit var txvTitle: TextView
    private lateinit var txvDesc: TextView
    private lateinit var txvCast: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: MovieAdapter
    private val movies = mutableListOf<Movie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        txvTitle = findViewById(R.id.txvMovieTitle)
        txvDesc = findViewById(R.id.txvMovieDesc)
        txvCast = findViewById(R.id.txvMovieCast)
        progressBar = findViewById(R.id.progressMovie)
        val rvSimilar: RecyclerView = findViewById(R.id.rvMovieSimilar)

        val id = intent?.getIntExtra("id", 0) ?: throw IllegalStateException("ID não encontrado")

        val url = "https://api.tiagoaguiar.co/netflixapp/movie/$id?apiKey=fc2f13f2-115a-4004-adc0-21079245fded"

        MovieTask(this).execute(url)

        adapter = MovieAdapter(movies, R.layout.movie_item_similar)
        rvSimilar.layoutManager = GridLayoutManager(this@MovieActivity, 3)
        rvSimilar.adapter = adapter

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResult(movieDetail: MovieDetail) {
        progressBar.visibility = View.GONE

        txvTitle.text =movieDetail.movie.title
        txvDesc.text = movieDetail.movie.desc
        txvCast.text = getString(R.string.cast, movieDetail.movie.cast)

        movies.clear()
        movies.addAll(movieDetail.similar)
        adapter.notifyDataSetChanged()

        DownloadImageTask(object : DownloadImageTask.Callback {
            override fun onResult(bitmap: Bitmap) {
                val layerDrawable: LayerDrawable = ContextCompat.getDrawable(this@MovieActivity, R.drawable.shadows) as LayerDrawable
                val movieCover = BitmapDrawable(resources, bitmap)
                layerDrawable.setDrawableByLayerId(R.id.coverDrawable, movieCover)
                val imgCover: ImageView = findViewById(R.id.imgMovie)
                imgCover.setImageDrawable(layerDrawable)
            }
        }).execute(movieDetail.movie.coverUrl)
    }

    override fun onFailure(message: String) {
        progressBar.visibility = View.GONE
        Toast.makeText(this@MovieActivity, message, Toast.LENGTH_SHORT).show()
    }

    override fun onPreExecute() {
        progressBar.visibility = View.VISIBLE
    }
}