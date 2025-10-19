package com.example.app.data.repository

import com.example.app.data.Genre
import com.example.app.data.Movie
import com.example.app.data.Person
import com.example.app.data.Poster
import com.example.app.data.Rating
import com.example.app.data.SimilarMovie


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