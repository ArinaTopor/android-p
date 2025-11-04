package com.example.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.data.Movie
import com.example.app.data.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<Movie>>(emptyList())
    val favorites: StateFlow<List<Movie>> = _favorites.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            favoriteRepository.getAllFavorites().collect { movies ->
                _favorites.value = movies
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(movie: Movie, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isFavorite = favoriteRepository.toggleFavorite(movie)
            onComplete(isFavorite)
        }
    }

    fun isFavorite(movieId: Int, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isFavorite = favoriteRepository.isFavorite(movieId)
            onComplete(isFavorite)
        }
    }
}
