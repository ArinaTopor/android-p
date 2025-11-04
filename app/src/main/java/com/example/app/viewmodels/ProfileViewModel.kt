package com.example.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.data.repository.ProfileRepository
import com.example.app.data.repository.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    val profile: StateFlow<UserProfile> = profileRepository.profile
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserProfile())

    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch {
            profileRepository.updateProfile(profile)
        }
    }
}


