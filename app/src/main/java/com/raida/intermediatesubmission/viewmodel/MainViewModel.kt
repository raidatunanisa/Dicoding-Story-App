package com.raida.intermediatesubmission.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.raida.intermediatesubmission.model.ListStoryItem
import com.raida.intermediatesubmission.model.LoginResult
import com.raida.intermediatesubmission.preference.UserPreference
import com.raida.intermediatesubmission.repository.StoryRepository
import kotlinx.coroutines.launch

class MainViewModel(private val storyRepository: StoryRepository, private val pref: UserPreference): ViewModel() {

    fun listStories(token: String) : LiveData<PagingData<ListStoryItem>> = storyRepository.getStory(token)
        .cachedIn(viewModelScope)

    fun getUser(): LiveData<LoginResult> = pref.getUser().asLiveData()

        fun logout() = viewModelScope.launch {
        pref.logout()
    }
}

class MainViewModelFactory(
    private val dataStore: UserPreference,
    private val storyRepository: StoryRepository
    ) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(storyRepository, dataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}