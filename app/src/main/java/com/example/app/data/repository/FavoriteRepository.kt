package com.example.app.data.repository

import com.example.app.data.Movie
import com.example.app.data.local.FavoriteMovie
import com.example.app.data.local.FavoriteMovieDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepository @Inject constructor(
    private val favoriteMovieDao: FavoriteMovieDao
) {
    
    fun getAllFavorites(): Flow<List<Movie>> {
        return favoriteMovieDao.getAllFavorites().map { favorites ->
            favorites.map { it.toMovie() }
        }
    }
    
    suspend fun isFavorite(movieId: Int): Boolean {
        return favoriteMovieDao.isFavorite(movieId)
    }
    
    suspend fun addToFavorites(movie: Movie) {
        favoriteMovieDao.insertFavorite(FavoriteMovie.fromMovie(movie))
    }
    
    suspend fun removeFromFavorites(movieId: Int) {
        favoriteMovieDao.deleteFavoriteById(movieId)
    }
    
    suspend fun toggleFavorite(movie: Movie): Boolean {
        val isCurrentlyFavorite = isFavorite(movie.id)
        if (isCurrentlyFavorite) {
            removeFromFavorites(movie.id)
            return false
        } else {
            addToFavorites(movie)
            return true
        }
    }
}






