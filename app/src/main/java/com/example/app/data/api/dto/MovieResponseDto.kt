package com.example.app.data.api.dto

import com.google.gson.annotations.SerializedName

data class MovieResponseDto(
    val id: Int,
    val name: String?,
    val alternativeName: String?,
    val year: Int?,
    val description: String?,
    val rating: RatingDto?,
    val poster: PosterDto?,
    val ageRating: Int?,
    @SerializedName("ratingMpaa")
    val ratingMpaa: String?,
    val movieLength: Int?,
    val genres: List<GenreDto>?,
    val persons: List<PersonDto>?,
    val similarMovies: List<SimilarMovieDto>?
)

data class RatingDto(
    val kp: Double?,
    val imdb: Double?,
    val tmdb: Double?,
    val filmCritics: Double?,
    val russianFilmCritics: Double?,
    val await: Double?
)

data class PosterDto(
    val url: String?,
    val previewUrl: String?
)

data class PersonDto(
    val id: Int,
    val photo: String?,
    val name: String?,
    val enName: String?
)

data class GenreDto(
    val name: String
)

data class SimilarMovieDto(
    val id: Int,
    val name: String,
    val alternativeName: String?,
    val year: Int?,
    val poster: PosterDto?,
    val rating: RatingDto?
)

data class MoviesListResponseDto(
    val docs: List<MovieResponseDto>,
    val total: Int,
    val limit: Int,
    val page: Int,
    val pages: Int
)
