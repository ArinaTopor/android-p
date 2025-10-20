package com.example.app.data.mapper

import com.example.app.data.Movie
import com.example.app.data.Person
import com.example.app.data.Poster
import com.example.app.data.Rating
import com.example.app.data.Genre
import com.example.app.data.SimilarMovie
import com.example.app.data.api.dto.MovieResponseDto
import com.example.app.data.api.dto.PersonDto
import com.example.app.data.api.dto.PosterDto
import com.example.app.data.api.dto.RatingDto
import com.example.app.data.api.dto.GenreDto
import com.example.app.data.api.dto.SimilarMovieDto

object MovieMapper {
    
    fun mapToDomain(dto: MovieResponseDto): Movie {
        return Movie(
            id = dto.id,
            name = dto.name ?: "",
            alternativeName = dto.alternativeName,
            year = dto.year,
            description = dto.description,
            rating = dto.rating?.let { mapRating(it) },
            poster = dto.poster?.let { mapPoster(it) },
            ageRating = dto.ageRating,
            ratingMpaa = dto.ratingMpaa,
            movieLength = dto.movieLength,
            genres = dto.genres?.map { mapGenre(it) } ?: emptyList(),
            persons = dto.persons?.map { mapPerson(it) } ?: emptyList(),
            similarMovies = dto.similarMovies?.map { mapSimilarMovie(it) } ?: emptyList()
        )
    }
    
    private fun mapRating(dto: RatingDto): Rating {
        return Rating(
            kp = dto.kp,
            imdb = dto.imdb,
            tmdb = dto.tmdb,
            filmCritics = dto.filmCritics,
            russianFilmCritics = dto.russianFilmCritics,
            await = dto.await
        )
    }
    
    private fun mapPoster(dto: PosterDto): Poster {
        return Poster(
            url = dto.url,
            previewUrl = dto.previewUrl
        )
    }
    
    private fun mapPerson(dto: PersonDto): Person {
        return Person(
            id = dto.id,
            photo = dto.photo,
            name = dto.name ?: "Неизвестно",
            enName = dto.enName
        )
    }
    
    private fun mapGenre(dto: GenreDto): Genre {
        return Genre(
            name = dto.name
        )
    }
    
    private fun mapSimilarMovie(dto: SimilarMovieDto): SimilarMovie {
        return SimilarMovie(
            id = dto.id,
            name = dto.name,
            alternativeName = dto.alternativeName,
            year = dto.year,
            poster = dto.poster?.let { mapPoster(it) },
            rating = dto.rating?.let { mapRating(it) }
        )
    }
}
