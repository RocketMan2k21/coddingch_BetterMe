package app.bettermetesttask.domainmovies.interactors

import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domaincore.utils.connectivity.ConnectivityManager
import app.bettermetesttask.domaincore.utils.coroutines.retry
import app.bettermetesttask.domainmovies.entries.Movie
import app.bettermetesttask.domainmovies.repository.MoviesRepositoryLocal
import app.bettermetesttask.domainmovies.repository.MovieApiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class ObserveMoviesUseCase @Inject constructor(
    private val repository: MoviesRepositoryLocal,
    private val apiRepository: MovieApiRepository,
    private val connectivityManager: ConnectivityManager
) {

    operator fun invoke(): Flow<Result<List<Movie>>> = flow {
        val isNetworkAvailable = connectivityManager.isNetworkAvailable()
        val remoteMovies = if (isNetworkAvailable) {
            retry(3, delayMillis = 2000) { apiRepository.getMovies() }
        } else {
            Result.Error(Exception("No Internet"))
        }

        if (remoteMovies is Result.Success) {
            repository.storeMovies(remoteMovies.data)
        }

        val localMovies = repository.getMovies()
        val likedMoviesFlow = repository.observeLikedMovieIds()

        emitAll(
            combine(localMovies, likedMoviesFlow) { localMoviesResult, likedMovieIds ->
                if (localMoviesResult is Result.Success) {
                    val updatedMovies = localMoviesResult.data.map {
                        it.copy(liked = likedMovieIds.contains(it.id))
                    }
                    Result.Success(updatedMovies)
                } else {
                    localMoviesResult
                }
            }
        )
    }
}