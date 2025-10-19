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
    val name: String,
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

object SampleMoviesRepository {
    val movies: List<Movie> = listOf(
        Movie(
            id = 666,
            name = "Человек паук",
            alternativeName = "Spider man",
            year = 2023,
            description = "string",
            rating = Rating(6.2, 8.4, 3.2, 10.0, 5.1, 6.1),
            poster = Poster(
                url = "https://avatars.mds.yandex.net/get-kinopoisk-image/1946459/428e2842-4157-45e8-a9af-1e5245e52c37/600x900",
                previewUrl = "https://avatars.mds.yandex.net/get-kinopoisk-image/1946459/428e2842-4157-45e8-a9af-1e5245e52c37/600x900"
            ),
            ageRating = 16,
            ratingMpaa = "pg13",
            movieLength = 120,
            genres = listOf(Genre("экшн"), Genre("фантастика")),
            persons = listOf(
                Person(
                    id = 6317,
                    photo = "https://st.kp.yandex.net/images/actor_iphone/iphone360_6317.jpg",
                    name = "Пол Уокер",
                    enName = "Paul Walker"
                )
            ),
            similarMovies = listOf(
                SimilarMovie(
                    id = 777,
                    name = "Интерстеллар",
                    alternativeName = "Interstellar",
                    year = 2014,
                    poster = Poster(
                        url = "https://avatars.mds.yandex.net/get-kinopoisk-image/1600647/430042eb-ee69-4818-aed0-a312400a26bf/600x900",
                        previewUrl = "https://avatars.mds.yandex.net/get-kinopoisk-image/1600647/430042eb-ee69-4818-aed0-a312400a26bf/600x900"
                    ),
                    rating = Rating(8.6, 8.7, 8.5, null, null, null)
                ),
                SimilarMovie(
                    id = 888,
                    name = "Начало",
                    alternativeName = "Inception",
                    year = 2010,
                    poster = Poster(
                        url = "https://avatars.mds.yandex.net/get-kinopoisk-image/1629390/8ab9a119-dd74-44f0-baec-0629797483d7/600x900",
                        previewUrl = "https://avatars.mds.yandex.net/get-kinopoisk-image/1629390/8ab9a119-dd74-44f0-baec-0629797483d7/600x900"
                    ),
                    rating = Rating(8.7, 8.8, 8.3, null, null, null)
                )
            )
        ),
        Movie(
            id = 777,
            name = "Интерстеллар",
            alternativeName = "Interstellar",
            year = 2014,
            description = "Путешествие за пределы нашей галактики.",
            rating = Rating(8.6, 8.7, 8.5, 8.4, 8.2, null),
            poster = Poster(
                url = "https://avatars.mds.yandex.net/get-kinopoisk-image/1600647/430042eb-ee69-4818-aed0-a312400a26bf/600x900",
                previewUrl = "https://avatars.mds.yandex.net/get-kinopoisk-image/1600647/430042eb-ee69-4818-aed0-a312400a26bf/600x900"
            ),
            ageRating = 12,
            ratingMpaa = "pg13",
            movieLength = 169,
            genres = listOf(Genre("научная фантастика")),
            similarMovies = listOf(
                SimilarMovie(
                    id = 888,
                    name = "Начало",
                    alternativeName = "Inception",
                    year = 2010,
                    poster = Poster(
                        url = "https://avatars.mds.yandex.net/get-kinopoisk-image/1629390/8ab9a119-dd74-44f0-baec-0629797483d7/600x900",
                        previewUrl = "https://avatars.mds.yandex.net/get-kinopoisk-image/1629390/8ab9a119-dd74-44f0-baec-0629797483d7/600x900"
                    ),
                    rating = Rating(8.7, 8.8, 8.3, null, null, null)
                ),
                SimilarMovie(
                    id = 666,
                    name = "Человек паук",
                    alternativeName = "Spider man",
                    year = 2023,
                    poster = Poster(
                        url = "https://avatars.mds.yandex.net/get-kinopoisk-image/1946459/428e2842-4157-45e8-a9af-1e5245e52c37/600x900",
                        previewUrl = "https://avatars.mds.yandex.net/get-kinopoisk-image/1946459/428e2842-4157-45e8-a9af-1e5245e52c37/600x900"
                    ),
                    rating = Rating(6.2, 8.4, 3.2, null, null, null)
                )
            )
        ),
        Movie(
            id = 888,
            name = "Начало",
            alternativeName = "Inception",
            year = 2010,
            description = "Кража секретов во сне.",
            rating = Rating(8.7, 8.8, 8.3, 8.0, 7.9, null),
            poster = Poster(
                url = "https://avatars.mds.yandex.net/get-kinopoisk-image/1629390/8ab9a119-dd74-44f0-baec-0629797483d7/600x900",
                previewUrl = "https://avatars.mds.yandex.net/get-kinopoisk-image/1629390/8ab9a119-dd74-44f0-baec-0629797483d7/600x900"
            ),
            ageRating = 16,
            ratingMpaa = "pg13",
            movieLength = 148,
            genres = listOf(Genre("триллер"), Genre("фантастика")),
            similarMovies = listOf(
                SimilarMovie(
                    id = 777,
                    name = "Интерстеллар",
                    alternativeName = "Interstellar",
                    year = 2014,
                    poster = Poster(
                        url = "https://avatars.mds.yandex.net/get-kinopoisk-image/1600647/430042eb-ee69-4818-aed0-a312400a26bf/600x900",
                        previewUrl = "https://avatars.mds.yandex.net/get-kinopoisk-image/1600647/430042eb-ee69-4818-aed0-a312400a26bf/600x900"
                    ),
                    rating = Rating(8.6, 8.7, 8.5, null, null, null)
                ),
                SimilarMovie(
                    id = 666,
                    name = "Человек паук",
                    alternativeName = "Spider man",
                    year = 2023,
                    poster = Poster(
                        url = "https://avatars.mds.yandex.net/get-kinopoisk-image/1946459/428e2842-4157-45e8-a9af-1e5245e52c37/600x900",
                        previewUrl = "https://avatars.mds.yandex.net/get-kinopoisk-image/1946459/428e2842-4157-45e8-a9af-1e5245e52c37/600x900"
                    ),
                    rating = Rating(6.2, 8.4, 3.2, null, null, null)
                )
            )
        )
    )
}


