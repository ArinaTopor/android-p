package com.example.app.di

import android.content.Context
import androidx.room.Room
import com.example.app.data.local.FavoriteMovieDao
import com.example.app.data.local.MovieDatabase
import com.example.app.data.local.FilterCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    
    @Provides
    @Singleton
    fun provideMovieDatabase(@ApplicationContext context: Context): MovieDatabase {
        return Room.databaseBuilder(
            context,
            MovieDatabase::class.java,
            "movie_database"
        ).build()
    }
    
    @Provides
    @Singleton
    fun provideFavoriteMovieDao(database: MovieDatabase): FavoriteMovieDao {
        return database.favoriteMovieDao()
    }
    
    @Provides
    @Singleton
    fun provideFilterCache(): FilterCache {
        return FilterCache()
    }
}




