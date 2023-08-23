package br.com.raulreis.appflix

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.raulreis.appflix.model.Category
import br.com.raulreis.appflix.util.CategoryTask

class MainActivity : AppCompatActivity(), CategoryTask.Callback {

    private lateinit var progressBar: ProgressBar
    private val categories = mutableListOf<Category>()
    private lateinit var adapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progressMain)

        // Vertical
        adapter = CategoryAdapter(categories) {id ->
            val intent = Intent(this@MainActivity, MovieActivity::class.java)
            intent.putExtra("id",id)
            startActivity(intent)
        }
        val rv: RecyclerView = findViewById(R.id.rvMain)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        CategoryTask(this).execute("https://api.tiagoaguiar.co/netflixapp/home?apiKey=fc2f13f2-115a-4004-adc0-21079245fded")
    }

    override fun onResult(categories: List<Category>) {
        // Aqui será quando o CategoryTask chamará de volta
        // (callback) - listener
        this.categories.clear()
        this.categories.addAll(categories)
        adapter.notifyDataSetChanged() // Força o adapter chamar o novo onBindViewHolder
        progressBar.visibility = View.GONE
    }

    override fun onFailure(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
        progressBar.visibility = View.GONE
    }

    override fun onPreExecute() {
        progressBar.visibility = View.VISIBLE
    }
}