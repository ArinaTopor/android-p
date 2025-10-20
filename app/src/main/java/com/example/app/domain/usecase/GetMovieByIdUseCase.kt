package com.example.app.domain.usecase

import com.example.app.data.Movie
import com.example.app.data.repository.MovieRepository
import javax.inject.Inject

class GetMovieByIdUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(id: Int): Result<Movie> {
        return repository.getMovieById(id)
    }
}
