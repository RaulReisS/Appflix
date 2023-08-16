package br.com.raulreis.appflix

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.raulreis.appflix.model.Category
import br.com.raulreis.appflix.model.Movie

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val categories = mutableListOf<Category>()
        for (j in 0 until 15) {
            val movies = mutableListOf<Movie>()
            for(i in 0 until 15) {
                val movie = Movie(R.drawable.movie)
                movies.add(movie)
            }
            val category = Category("Cat $j", movies)
            categories.add(category)
        }


        // Vertical
        val adapter = CategoryAdapter(categories)
        val rv: RecyclerView = findViewById(R.id.rvMain)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
    }
}