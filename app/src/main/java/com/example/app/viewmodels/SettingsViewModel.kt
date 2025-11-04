package com.example.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.data.local.FilterCache
import com.example.app.data.repository.FilterRepository
import com.example.app.data.repository.FilterSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val filterRepository: FilterRepository,
    private val filterCache: FilterCache
) : ViewModel() {

    init {
        viewModelScope.launch {
            val settings = filterRepository.filterSettings.first()
            val hasActiveFilters = settings.genre.isNotEmpty() || 
                settings.minRating > 0f || 
                settings.query.isNotEmpty()
            filterCache.setHasActiveFilters(hasActiveFilters)
        }
    }

    val filterSettings: kotlinx.coroutines.flow.Flow<FilterSettings> = filterRepository.filterSettings
    val hasActiveFilters: kotlinx.coroutines.flow.StateFlow<Boolean> = filterCache.hasActiveFilters

    fun applyFilters(genre: String, minRating: Float, query: String) {
        viewModelScope.launch {
            val hasActiveFilters = genre.isNotEmpty() || minRating > 0f || query.isNotEmpty()
            filterCache.setHasActiveFilters(hasActiveFilters)
            filterRepository.updateFilterSettings(
                com.example.app.data.repository.FilterSettings(genre, minRating, query)
            )
        }
    }

    fun clearFilters() {
        viewModelScope.launch {
            filterCache.clear()
            filterRepository.clearFilters()
        }
    }
}
