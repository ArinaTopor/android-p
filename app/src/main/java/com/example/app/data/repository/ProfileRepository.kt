package com.example.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class UserProfile(
    val fullName: String = "",
    val position: String = "",
    val avatarUri: String = "",
    val resumeUrl: String = "",
    val favoritePairTime: String = ""
)

@Singleton
class ProfileRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private object Keys {
        val FULL_NAME = stringPreferencesKey("profile_full_name")
        val POSITION = stringPreferencesKey("profile_position")
        val AVATAR_URI = stringPreferencesKey("profile_avatar_uri")
        val RESUME_URL = stringPreferencesKey("profile_resume_url")
        val FAVORITE_PAIR_TIME = stringPreferencesKey("profile_favorite_pair_time")
    }

    val profile: Flow<UserProfile> = dataStore.data.map { prefs ->
        UserProfile(
            fullName = prefs[Keys.FULL_NAME] ?: "",
            position = prefs[Keys.POSITION] ?: "",
            avatarUri = prefs[Keys.AVATAR_URI] ?: "",
            resumeUrl = prefs[Keys.RESUME_URL] ?: "",
            favoritePairTime = prefs[Keys.FAVORITE_PAIR_TIME] ?: ""
        )
    }

    suspend fun updateProfile(profile: UserProfile) {
        dataStore.edit { prefs ->
            prefs[Keys.FULL_NAME] = profile.fullName
            prefs[Keys.POSITION] = profile.position
            prefs[Keys.AVATAR_URI] = profile.avatarUri
            prefs[Keys.RESUME_URL] = profile.resumeUrl
            prefs[Keys.FAVORITE_PAIR_TIME] = profile.favoritePairTime
        }
    }
}


