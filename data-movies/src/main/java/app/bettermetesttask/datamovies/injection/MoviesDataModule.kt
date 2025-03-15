package app.bettermetesttask.datamovies.injection

import android.content.Context
import androidx.room.Room
import app.bettermetesttask.datamovies.database.DB_NAME
import app.bettermetesttask.datamovies.database.MoviesDatabase
import app.bettermetesttask.datamovies.repository.MoviesRepositoryLocalImpl
import app.bettermetesttask.datamovies.repository.MoviesRepositoryRemoteImpl
import app.bettermetesttask.datamovies.repository.stores.MoviesRestStore
import app.bettermetesttask.domainmovies.repository.MoviesRepositoryLocal
import app.bettermetesttask.domainmovies.repository.MovieApiRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class MoviesDataModule {

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(context: Context): MoviesDatabase {
            return Room.databaseBuilder(context.applicationContext, MoviesDatabase::class.java, DB_NAME)
                .build()
        }

        @Provides
        @Singleton
        fun bindRestStore(): MoviesRestStore {
            return MoviesRestStore()
        }
    }

    @Binds
    abstract fun bindMoviesLocalRepository(repositoryImpl: MoviesRepositoryLocalImpl): MoviesRepositoryLocal

    @Binds
    abstract fun bindMoviesApiRepository(apiRepository: MoviesRepositoryRemoteImpl): MovieApiRepository

}