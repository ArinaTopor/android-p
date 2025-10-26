package com.example.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.app.data.Movie
import com.example.app.data.Genre
import com.example.app.data.Poster
import com.example.app.data.Rating

@Entity(tableName = "favorite_movies")
data class FavoriteMovie(
    @PrimaryKey
    val id: Int,
    val name: String,
    val alternativeName: String?,
    val year: Int?,
    val description: String?,
    val ratingKp: Double?,
    val ratingImdb: Double?,
    val posterUrl: String?,
    val posterPreviewUrl: String?,
    val genreNames: String, // JSON или comma-separated
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toMovie(): Movie {
        return Movie(
            id = id,
            name = name,
            alternativeName = alternativeName,
            year = year,
            description = description,
            rating = Rating(
                kp = ratingKp,
                imdb = ratingImdb,
                tmdb = null,
                filmCritics = null,
                russianFilmCritics = null,
                await = null
            ),
            poster = Poster(
                url = posterUrl,
                previewUrl = posterPreviewUrl
            ),
            genres = if (genreNames.isNotEmpty()) genreNames.split(",").map { Genre(it.trim()) } else emptyList(),
            persons = emptyList(),
            similarMovies = emptyList()
        )
    }
    
    companion object {
        fun fromMovie(movie: Movie): FavoriteMovie {
            return FavoriteMovie(
                id = movie.id,
                name = movie.name,
                alternativeName = movie.alternativeName,
                year = movie.year,
                description = movie.description,
                ratingKp = movie.rating?.kp,
                ratingImdb = movie.rating?.imdb,
                posterUrl = movie.poster?.url,
                posterPreviewUrl = movie.poster?.previewUrl,
                genreNames = movie.genres.joinToString(",") { it.name }
            )
        }
    }
}
