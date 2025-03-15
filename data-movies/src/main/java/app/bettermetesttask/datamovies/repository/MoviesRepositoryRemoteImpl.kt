package app.bettermetesttask.datamovies.repository

import app.bettermetesttask.datamovies.repository.stores.MoviesRestStore
import app.bettermetesttask.domainmovies.entries.Movie
import app.bettermetesttask.domainmovies.repository.MovieApiRepository
import javax.inject.Inject
import app.bettermetesttask.domaincore.utils.Result

class MoviesRepositoryRemoteImpl @Inject constructor(
    private val restStore: MoviesRestStore,
) : MovieApiRepository {
    override suspend fun getMovies(): Result<List<Movie>> {
        return Result.of { restStore.getMovies() }
    }
}