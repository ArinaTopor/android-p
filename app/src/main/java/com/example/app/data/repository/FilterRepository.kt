package com.example.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class FilterSettings(
    val genre: String = "",
    val minRating: Float = 0f,
    val query: String = ""
)

@Singleton
class FilterRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    
    private object PreferencesKeys {
        val GENRE = stringPreferencesKey("genre")
        val MIN_RATING = floatPreferencesKey("min_rating")
        val QUERY = stringPreferencesKey("query")
    }
    
    val filterSettings: Flow<FilterSettings> = dataStore.data.map { preferences ->
        FilterSettings(
            genre = preferences[PreferencesKeys.GENRE] ?: "",
            minRating = preferences[PreferencesKeys.MIN_RATING] ?: 0f,
            query = preferences[PreferencesKeys.QUERY] ?: ""
        )
    }
    
    suspend fun updateFilterSettings(settings: FilterSettings) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.GENRE] = settings.genre
            preferences[PreferencesKeys.MIN_RATING] = settings.minRating
            preferences[PreferencesKeys.QUERY] = settings.query
        }
    }
    
    suspend fun clearFilters() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.GENRE)
            preferences.remove(PreferencesKeys.MIN_RATING)
            preferences.remove(PreferencesKeys.QUERY)
        }
    }
    
    fun hasActiveFilters(): Boolean {
        // Синхронная проверка невозможна через Flow, поэтому используем кэш
        return false // Будет устанавливаться через FilterCache
    }
}







