package com.example.app.data.local

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Singleton

@Singleton
class FilterCache {
    private val _hasActiveFilters = MutableStateFlow(false)
    val hasActiveFilters: StateFlow<Boolean> = _hasActiveFilters.asStateFlow()
    
    fun setHasActiveFilters(hasFilters: Boolean) {
        _hasActiveFilters.value = hasFilters
    }
    
    fun clear() {
        _hasActiveFilters.value = false
    }
}






