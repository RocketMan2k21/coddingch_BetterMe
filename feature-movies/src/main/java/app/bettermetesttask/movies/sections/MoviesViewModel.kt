package app.bettermetesttask.movies.sections

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domainmovies.entries.Movie
import app.bettermetesttask.domainmovies.interactors.AddMovieToFavoritesUseCase
import app.bettermetesttask.domainmovies.interactors.ObserveMoviesUseCase
import app.bettermetesttask.domainmovies.interactors.RemoveMovieFromFavoritesUseCase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MoviesViewModel @Inject constructor(
    private val observeMoviesUseCase: ObserveMoviesUseCase,
    private val likeMovieUseCase: AddMovieToFavoritesUseCase,
    private val dislikeMovieUseCase: RemoveMovieFromFavoritesUseCase,
    private val adapter: MoviesAdapter
) : ViewModel() {

    private val moviesMutableFlow: MutableStateFlow<MoviesState> =
        MutableStateFlow(MoviesState.Initial)

    var selectedMovie: MutableState<Movie?> = mutableStateOf(null)
        private set

    val moviesStateFlow: StateFlow<MoviesState>
        get() = moviesMutableFlow.asStateFlow()

    init {
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launch {
            moviesMutableFlow.emit(MoviesState.Loading)
            observeMoviesUseCase()
                .collect { result ->
                    if (result is Result.Success) {
                        moviesMutableFlow.emit(MoviesState.Loaded(result.data))
                        adapter.submitList(result.data)
                    }
                    if (result is Result.Error) {
                        moviesMutableFlow.emit(
                            MoviesState.Error(
                                result.error.message
                                    ?: "Cannot load movie list, please try again later"
                            )
                        )
                    }
                }
        }
    }

    fun likeMovie(movie: Movie) {
        try {
            viewModelScope.launch {
                if (!movie.liked) {
                    likeMovieUseCase(movie.id)
                } else {
                    dislikeMovieUseCase(movie.id)
                }
            }
        } catch (e : Exception) {
            Timber.e("Error while assigning a like: ${e.message}")
        }
    }

    // Passing null value ensures there is no bottom sheet opened
    fun onMovieDetailsClick(movie: Movie?) {
        movie?.let {
            selectedMovie.value = it
        } ?: run {
            selectedMovie.value = null
        }
    }
}