package com.genthur.storyapp.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.genthur.storyapp.repository.Repository
import com.genthur.storyapp.data.remote.response.RegisterResponse

class RegisterViewModel(private val repository: Repository): ViewModel() {
    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse> = _registerResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    suspend fun registerUser(name: String, email: String, password: String) {
        _isLoading.value = true
        try {
            val response = repository.register(name, email, password)
            _registerResponse.value = response
        } catch (e: Exception) {
            Log.e("RegisterViewModel", "Register error: ${e.message}")
            _registerResponse.value = RegisterResponse(error = true, message = "Registrasi gagal")
        } finally {
            _isLoading.value = false
        }
    }
}