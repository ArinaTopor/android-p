package com.example.app.viewmodels

import androidx.lifecycle.ViewModel
import com.example.app.data.Movie
import com.example.app.data.SampleMoviesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _movies = MutableStateFlow(SampleMoviesRepository.movies)
    val movies: StateFlow<List<Movie>> = _movies.asStateFlow()

    fun getMovieById(id: Int): Movie? = _movies.value.find { it.id == id }
}
