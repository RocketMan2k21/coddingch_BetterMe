package app.bettermetesttask.datamovies.repository

import app.bettermetesttask.datamovies.database.entities.MovieEntity
import app.bettermetesttask.datamovies.repository.stores.MoviesLocalStore
import app.bettermetesttask.datamovies.repository.stores.MoviesMapper
import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domainmovies.entries.Movie
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.assertj.core.api.Assertions.assertThat
import org.mockito.Mockito

@ExtendWith(MockitoExtension::class)
class MoviesRepositoryLocalTest {

    @Mock
    private lateinit var localStore: MoviesLocalStore

    private lateinit var mapper: MoviesMapper

    private lateinit var repository: MoviesRepositoryLocalImpl

    private val movieEntity = MovieEntity(1, "Movie 1", "Description 1", "poster1.jpg")
    private val movie = Movie(1, "Movie 1", "Description 1", "poster1.jpg")

    @BeforeEach
    fun setup() {
        mapper = MoviesMapper()
        
        mapper.mapFromLocal = { movieEntity -> movie }
        mapper.mapToLocal = { movie -> movieEntity }
        
        repository = MoviesRepositoryLocalImpl(localStore, mapper)
    }

    @Test
    fun `getMovies should return success result with mapped movies`() = runTest {
        // Given
        val movieEntities = listOf(movieEntity)
        val movies = listOf(movie)
        `when`(localStore.getMovies()).thenReturn(movieEntities)

        // When
        val result = repository.getMovies().first()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data).isEqualTo(movies)
        verify(localStore).getMovies()
    }

    @Test
    fun `getMovie should return success result with mapped movie`() = runTest {
        // Given
        val movieId = 1
        `when`(localStore.getMovie(movieId)).thenReturn(movieEntity)

        // When
        val result = repository.getMovie(movieId)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data).isEqualTo(movie)
        verify(localStore).getMovie(movieId)
    }

    @Test
    fun `observeLikedMovieIds should return flow of liked movie ids`() = runTest {
        // Given
        val likedIds = listOf(1, 2, 3)
        `when`(localStore.observeLikedMoviesIds()).thenReturn(flowOf(likedIds))

        // When
        val result = repository.observeLikedMovieIds().first()

        // Then
        assertThat(result).isEqualTo(likedIds)
        verify(localStore).observeLikedMoviesIds()
    }

    @Test
    fun `addMovieToFavorites should call local store`() = runTest {
        // Given
        val movieId = 1

        // When
        repository.addMovieToFavorites(movieId)

        // Then
        verify(localStore).likeMovie(movieId)
    }

    @Test
    fun `storeMovies should map and store movies`() = runTest {
        // Given
        val movies = listOf(movie)
        val movieEntities = listOf(movieEntity)

        // When
        repository.storeMovies(movies)

        // Then
        verify(localStore).storeMovies(movieEntities)
    }

    @Test
    fun `removeMovieFromFavorites should call local store`() = runTest {
        // Given
        val movieId = 1

        // When
        repository.removeMovieFromFavorites(movieId)

        // Then
        verify(localStore).dislikeMovie(movieId)
    }

    @Test
    fun `getMovie should return error result when local store throws exception`() = runTest {
        // Given
        val movieId = 1
        val exception = RuntimeException("Database error")
        `when`(localStore.getMovie(movieId)).thenThrow(exception)

        // When
        val result = repository.getMovie(movieId)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).error).isEqualTo(exception)
    }

    @Test
    fun `getMovies should return error result when local store throws exception`() = runTest {
        // Given
        val exception = RuntimeException("Database error")
        `when`(localStore.getMovies()).thenThrow(exception)

        // When
        val result = repository.getMovies().first()

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).error).isEqualTo(exception)
    }
}