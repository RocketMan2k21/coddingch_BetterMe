package app.bettermetesttask.movies.sections

import app.bettermetesttask.domainmovies.entries.Movie

sealed class MoviesState {

    data object Initial : MoviesState()

    data object Loading : MoviesState()

    data class Error(val msg : String) : MoviesState()

    data class Loaded(val movies: List<Movie>) : MoviesState()
}