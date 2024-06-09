package com.genthur.storyapp.ui.addstory

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genthur.storyapp.repository.Repository
import com.genthur.storyapp.util.User
import com.genthur.storyapp.data.remote.response.FileUploadResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repository: Repository): ViewModel() {

    val addStoryResponse: LiveData<FileUploadResponse> = repository.addStories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun addStory(token: String, image: MultipartBody.Part, description: RequestBody, location: Location? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.addStory(token, image, description, location)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getSession(): LiveData<User> {
        return repository.getSession()
    }

}