package com.genthur.storyapp.ui.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genthur.storyapp.data.remote.response.ListStoryItem
import com.genthur.storyapp.repository.Repository
import com.genthur.storyapp.util.User
import kotlinx.coroutines.launch

class MapViewModel(private val repository: Repository) : ViewModel() {
    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> = _listStory

    fun getStoriesLocation(token: String) {
        viewModelScope.launch {
            try {
                val storyResponse = repository.getStoriesLocation(token)
                val filteredListStory = storyResponse.listStory?.filterNotNull() ?: emptyList()
                _listStory.postValue(filteredListStory)
            } catch (e: Exception) {
                Log.e("MapViewModel", "getStoriesLocation: Error getting stories ${e.message}")
            }
        }
    }

    fun getSession(): LiveData<User> {
        return repository.getSession()
    }
}