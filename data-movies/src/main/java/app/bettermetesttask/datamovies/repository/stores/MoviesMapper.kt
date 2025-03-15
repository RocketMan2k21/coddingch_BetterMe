package app.bettermetesttask.datamovies.repository.stores

import app.bettermetesttask.datamovies.database.entities.MovieEntity
import app.bettermetesttask.domainmovies.entries.Movie
import javax.inject.Inject

class MoviesMapper @Inject constructor() {

    var mapToLocal: (Movie) -> MovieEntity = {
        with(it) {
            MovieEntity(id, title, description, posterPath)
        }
    }

    var mapFromLocal: (MovieEntity) -> Movie = {
        with(it) {
            Movie(id, title, description, posterPath)
        }
    }
}
