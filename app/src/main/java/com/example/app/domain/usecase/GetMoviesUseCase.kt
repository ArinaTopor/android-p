package com.example.app.domain.usecase

import com.example.app.data.Movie
import com.example.app.data.repository.MovieRepository
import javax.inject.Inject

class GetMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(page: Int = 1, limit: Int = 25): Result<List<Movie>> {
        return repository.getMovies(page, limit)
    }
}
