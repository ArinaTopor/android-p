package com.example.app.data

data class Rating(
    val kp: Double?,
    val imdb: Double?,
    val tmdb: Double?,
    val filmCritics: Double?,
    val russianFilmCritics: Double?,
    val await: Double?
)

data class Poster(
    val url: String?,
    val previewUrl: String?
)

data class Person(
    val id: Int,
    val photo: String?,
    val name: String?,
    val enName: String?
)

data class Genre(
    val name: String
)

data class SimilarMovie(
    val id: Int,
    val name: String,
    val alternativeName: String? = null,
    val year: Int? = null,
    val poster: Poster? = null,
    val rating: Rating? = null
)

data class Movie(
    val id: Int,
    val name: String,
    val alternativeName: String?,
    val year: Int?,
    val description: String?,
    val rating: Rating?,
    val poster: Poster?,
    val ageRating: Int? = null,
    val ratingMpaa: String? = null,
    val movieLength: Int? = null,
    val genres: List<Genre> = emptyList(),
    val persons: List<Person> = emptyList(),
    val similarMovies: List<SimilarMovie> = emptyList()
)


