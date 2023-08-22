package br.com.raulreis.appflix.util

import android.os.Looper
import br.com.raulreis.appflix.model.Category
import br.com.raulreis.appflix.model.Movie
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection
import android.os.Handler

class CategoryTask(private val callback: Callback) {

    private val handler = Handler(Looper.getMainLooper())

    interface  Callback {
        fun onResult(categories: List<Category>)
        fun onFailure(message: String)
        fun onPreExecute()
    }

    fun execute(url: String) {
        callback.onPreExecute()
        // Estamos utilizando a UI-thread (1)
        val executor = Executors.newSingleThreadExecutor()

        executor.execute {
            var urlConnection: HttpsURLConnection? = null
            var buffer: BufferedInputStream? = null
            var stream: InputStream? = null
            try {
                // Estamos utilizando a nova thread [processo paralelo] (2)
                val requestURL = URL(url) // Abrir uma URL
                urlConnection =
                    requestURL.openConnection() as HttpsURLConnection // abrir a conexão
                urlConnection.readTimeout = 2000 // Tempo leitura (2s)
                urlConnection.connectTimeout = 2000 // Tempo conexão (2s)

                val statusCode: Int = urlConnection.responseCode

                if (statusCode > 400) {
                    throw IOException("Erro na comunicação com o servidor!")
                }

                // Forma 1: simples e rápida
                stream = urlConnection.inputStream // sequência de bytes
                // val jasonAsString = stream.bufferedReader().use { it.readText() }


                // Froma 2: ???
                buffer = BufferedInputStream(stream)
                val jsonAsString = toString(buffer)

                // O JSON está preparado para ser convertido em um Data Class
                val categories = toCategories(jsonAsString)

                handler.post {
                    // Aqui roda dentro de UI-Thread
                    callback.onResult(categories)
                }

            } catch (e: IOException) {
                val message = e.message ?: "Erro desconhecido"
                handler.post {
                    callback.onFailure(message)
                }
            } finally {
                urlConnection?.disconnect()
                stream?.close()
                buffer?.close()
            }
        }
    }

    private fun toCategories(jsonAsString: String) : List<Category> {
        val categories = mutableListOf<Category>()

        val jsonRoot = JSONObject(jsonAsString)
        val jsonCategories = jsonRoot.getJSONArray("category")

        for (i in 0 until jsonCategories.length()) {
            val jsonCategory = jsonCategories.getJSONObject(i)

            val title = jsonCategory.getString("title")
            val jsonMovies = jsonCategory.getJSONArray("movie")

            val movies = mutableListOf<Movie>()
            for(j in 0 until jsonMovies.length()) {
                val jsonMovie = jsonMovies.getJSONObject(j)

                val id = jsonMovie.getInt("id")
                val coverUrl = jsonMovie.getString("cover_url")

                movies.add(Movie(id, coverUrl))
            }
            categories.add(Category(title, movies))
        }
        return categories
    }

    private fun toString(stream: InputStream) : String {
        val bytes = ByteArray(1024)
        val baos = ByteArrayOutputStream()
        var read: Int
        while (true) {
            read = stream.read(bytes)
            if (read <= 0)
                break
            baos.write(bytes, 0, read)
        }
        return String(baos.toByteArray())
    }
}