package com.genthur.storyapp.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genthur.storyapp.repository.Repository
import com.genthur.storyapp.util.User
import com.genthur.storyapp.data.remote.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: Repository): ViewModel() {

    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    suspend fun login(email: String, password: String) {
        _isLoading.value = true
        try {
            val response = repository.login(email, password)
            _loginResponse.postValue(response)
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Login error: ${e.message}")
            _loginResponse.postValue(LoginResponse(error = true, message = "Login gagal"))
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun saveSession(user: User) {
        repository.saveSession(user)
    }

    fun loginSession() {
        viewModelScope.launch {
            repository.isLoggedIn()
        }
    }

    fun getSession(): LiveData<User> {
        return repository.getSession()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}