package br.com.raulreis.appflix.util

import android.os.Looper
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
import br.com.raulreis.appflix.model.MovieDetail

class MovieTask(private val callback: Callback) {

    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()

    interface  Callback {
        fun onResult(movieDetail: MovieDetail)
        fun onFailure(message: String)
        fun onPreExecute()
    }

    fun execute(url: String) {
        callback.onPreExecute()
        // Estamos utilizando a UI-thread (1)


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

                if (statusCode == 400) {
                    stream = urlConnection.errorStream
                    buffer = BufferedInputStream(stream)
                    val jsonAsString = toString(buffer)

                    val json = JSONObject(jsonAsString)
                    val message = json.getString("message")
                    throw IOException(message)
                }
                else if (statusCode > 400) {
                    throw IOException("Erro na comunicação com o servidor!")
                }

                stream = urlConnection.inputStream // sequência de bytes

                buffer = BufferedInputStream(stream)
                val jsonAsString = toString(buffer)

                val movieDetail = toMovieDetail(jsonAsString)

                handler.post {
                    callback.onResult(movieDetail)
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

    private fun toMovieDetail(jsonAsString: String) : MovieDetail {
        val json = JSONObject(jsonAsString)

        val id = json.getInt("id")
        val title = json.getString("title")
        val desc = json.getString("desc")
        val cast = json.getString("cast")
        val coverUrl = json.getString("cover_url")
        val jsonMovies = json.getJSONArray("movie")

        val similar = mutableListOf<Movie>()
        for(i in 0 until jsonMovies.length()) {
            val jsonMovie = jsonMovies.getJSONObject(i)

            val similarId = jsonMovie.getInt("id")
            val similarConverUrl = jsonMovie.getString("cover_url")

            val m = Movie(similarId, similarConverUrl)
            similar.add(m)
        }

        val movie = Movie(id, coverUrl, title, desc, cast)
        return MovieDetail(movie, similar)
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