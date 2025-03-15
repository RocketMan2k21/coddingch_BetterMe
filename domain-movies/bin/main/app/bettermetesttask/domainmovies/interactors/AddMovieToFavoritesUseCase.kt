package app.bettermetesttask.domainmovies.interactors

import app.bettermetesttask.domainmovies.repository.MoviesRepositoryLocal
import javax.inject.Inject

class AddMovieToFavoritesUseCase @Inject constructor(
    private val repository: MoviesRepositoryLocal
) {

    suspend operator fun invoke(id: Int) {
        repository.addMovieToFavorites(id)
    }
}