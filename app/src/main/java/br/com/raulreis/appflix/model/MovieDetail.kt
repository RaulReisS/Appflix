package br.com.raulreis.appflix.model

data class MovieDetail(
    val movie: Movie,
    val similar: List<Movie>
    )
