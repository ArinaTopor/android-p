package com.example.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.data.local.FilterCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BadgeViewModel @Inject constructor(
    private val filterCache: FilterCache
) : ViewModel() {
    
    private val _hasBadge = MutableStateFlow(false)
    val hasBadge: StateFlow<Boolean> = _hasBadge.asStateFlow()
    
    init {
        viewModelScope.launch {
            filterCache.hasActiveFilters.collect { hasActiveFilters ->
                _hasBadge.value = hasActiveFilters
            }
        }
    }
}






