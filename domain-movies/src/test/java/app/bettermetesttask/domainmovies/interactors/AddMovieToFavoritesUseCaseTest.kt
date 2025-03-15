package app.bettermetesttask.domainmovies.interactors

import app.bettermetesttask.domainmovies.repository.MoviesRepositoryLocal
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class AddMovieToFavoritesUseCaseTest {

    @Mock
    private lateinit var repository: MoviesRepositoryLocal

    private lateinit var useCase: AddMovieToFavoritesUseCase

    @BeforeEach
    fun setup() {
        useCase = AddMovieToFavoritesUseCase(repository)
    }

    @Test
    fun `invoke should add movie to favorites`() = runTest {
        // Given
        val movieId = 1

        // When
        useCase(movieId)

        // Then
        verify(repository).addMovieToFavorites(movieId)
    }
} 