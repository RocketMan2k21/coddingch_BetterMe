package app.bettermetesttask.datamovies.repository

import app.bettermetesttask.datamovies.repository.stores.MoviesRestStore
import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domainmovies.entries.Movie
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.assertj.core.api.Assertions.assertThat

@ExtendWith(MockitoExtension::class)
class MoviesRepositoryRemoteTest {

    @Mock
    private lateinit var restStore: MoviesRestStore

    private lateinit var repository: MoviesRepositoryRemoteImpl

    @BeforeEach
    fun setup() {
        repository = MoviesRepositoryRemoteImpl(restStore)
    }

    @Test
    fun `getMovies should return success result when rest store returns movies`() = runTest {
        // Given
        val movies = listOf(
            Movie(1, "Movie 1", "Description 1", "poster1.jpg"),
            Movie(2, "Movie 2", "Description 2", "poster2.jpg")
        )
        `when`(restStore.getMovies()).thenReturn(movies)

        // When
        val result = repository.getMovies()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data).isEqualTo(movies)
        verify(restStore).getMovies()
    }

    @Test
    fun `getMovies should return error result when rest store throws exception`() = runTest {
        // Given
        val exception = IllegalStateException("Network error")
        `when`(restStore.getMovies()).thenThrow(exception)

        // When
        val result = repository.getMovies()

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).error).isEqualTo(exception)
        verify(restStore).getMovies()
    }
} 