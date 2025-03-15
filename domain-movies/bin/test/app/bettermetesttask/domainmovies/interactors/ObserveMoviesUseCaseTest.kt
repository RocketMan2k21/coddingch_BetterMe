package app.bettermetesttask.domainmovies.interactors

import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domaincore.utils.connectivity.ConnectivityManager
import app.bettermetesttask.domainmovies.entries.Movie
import app.bettermetesttask.domainmovies.repository.MovieApiRepository
import app.bettermetesttask.domainmovies.repository.MoviesRepositoryLocal
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.Mockito.never
import org.mockito.junit.jupiter.MockitoExtension
import org.assertj.core.api.Assertions.assertThat

@ExtendWith(MockitoExtension::class)
class ObserveMoviesUseCaseTest {

    @Mock
    private lateinit var localRepository: MoviesRepositoryLocal

    @Mock
    private lateinit var apiRepository: MovieApiRepository

    @Mock
    private lateinit var connectivityManager: ConnectivityManager

    private lateinit var useCase: ObserveMoviesUseCase

    private val testMovies = listOf(
        Movie(1, "Movie 1", "Description 1", "poster1.jpg"),
        Movie(2, "Movie 2", "Description 2", "poster2.jpg")
    )

    @BeforeEach
    fun setup() {
        useCase = ObserveMoviesUseCase(localRepository, apiRepository, connectivityManager)
        // Setup default behavior for liked movies flow
        `when`(localRepository.observeLikedMovieIds()).thenReturn(flowOf(emptyList()))
    }

    @Test
    fun `invoke should fetch from API and store locally when network is available`() = runTest {
        // Given
        `when`(connectivityManager.isNetworkAvailable()).thenReturn(true)
        `when`(apiRepository.getMovies()).thenReturn(Result.Success(testMovies))
        `when`(localRepository.getMovies()).thenReturn(flowOf(Result.Success(testMovies)))

        // When
        val result = useCase().first()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data).isEqualTo(testMovies)
        verify(apiRepository).getMovies()
        verify(localRepository).storeMovies(testMovies)
    }

    @Test
    fun `invoke should return local data when network is not available`() = runTest {
        // Given
        `when`(connectivityManager.isNetworkAvailable()).thenReturn(false)
        `when`(localRepository.getMovies()).thenReturn(flowOf(Result.Success(testMovies)))

        // When
        val result = useCase().first()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data).isEqualTo(testMovies)
        verify(apiRepository, never()).getMovies()
    }

    @Test
    fun `invoke should return error when API fails and no local data`() = runTest {
        // Given
        val error = RuntimeException("API Error")
        `when`(connectivityManager.isNetworkAvailable()).thenReturn(true)
        `when`(apiRepository.getMovies()).thenReturn(Result.Error(error))
        `when`(localRepository.getMovies()).thenReturn(flowOf(Result.Error(error)))

        // When
        val result = useCase().first()

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).error).isEqualTo(error)
    }

    @Test
    fun `invoke should return local data when API fails`() = runTest {
        // Given
        val error = RuntimeException("API Error")
        `when`(connectivityManager.isNetworkAvailable()).thenReturn(true)
        `when`(apiRepository.getMovies()).thenReturn(Result.Error(error))
        `when`(localRepository.getMovies()).thenReturn(flowOf(Result.Success(testMovies)))

        // When
        val result = useCase().first()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data).isEqualTo(testMovies)
    }

    @Test
    fun `invoke should combine movies with liked status`() = runTest {
        // Given
        val likedMovieIds = listOf(1)
        `when`(connectivityManager.isNetworkAvailable()).thenReturn(true)
        `when`(apiRepository.getMovies()).thenReturn(Result.Success(testMovies))
        `when`(localRepository.getMovies()).thenReturn(flowOf(Result.Success(testMovies)))
        `when`(localRepository.observeLikedMovieIds()).thenReturn(flowOf(likedMovieIds))

        // When
        val result = useCase().first()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val movies = (result as Result.Success).data
        assertThat(movies).hasSize(2)
        assertThat(movies.first { it.id == 1 }).isEqualTo(testMovies[0].copy(liked = true))
        assertThat(movies.first { it.id == 2 }).isEqualTo(testMovies[1])
    }
} 