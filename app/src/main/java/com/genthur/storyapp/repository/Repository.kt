package com.genthur.storyapp.repository

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.genthur.storyapp.util.User
import com.genthur.storyapp.data.local.datastore.UserPreference
import com.genthur.storyapp.data.remote.response.FileUploadResponse
import com.genthur.storyapp.data.remote.response.LoginResponse
import com.genthur.storyapp.data.remote.response.RegisterResponse
import com.genthur.storyapp.data.remote.response.StoryResponse
import com.genthur.storyapp.data.remote.api.ApiService
import com.genthur.storyapp.data.remote.response.ListStoryItem
import com.genthur.storyapp.util.StoryPagingSource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException

class Repository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
){

    private val _addStories = MutableLiveData<FileUploadResponse>()
    val addStories: LiveData<FileUploadResponse> = _addStories

    fun getSession(): LiveData<User> {
        return userPreference.getSession().asLiveData()
    }

    fun addStory(token: String, image: MultipartBody.Part, description: RequestBody, location: Location? = null) {
        val client = apiService.uploadStories(token, image, description, location?.latitude, location?.longitude)

        client.enqueue(object : Callback<FileUploadResponse> {
            override fun onResponse(call: Call<FileUploadResponse>, response: Response<FileUploadResponse>) {
                try {
                    if (response.isSuccessful && response.body() != null) {
                        _addStories.value = response.body()
                    } else {
                        val jsonObject = response.errorBody()?.string()?.let { JSONObject(it) }
                        val error = jsonObject?.getBoolean("error")
                        val message = jsonObject?.getString("message")
                        _addStories.value = FileUploadResponse(error, message)
                        Log.e("AddStory", "onResponse: ${response.message()}, ${response.code()} $message")
                    }
                } catch (e: HttpException) {
                    Log.e("AddStory", "onResponse: ${e.message()}")
                }
            }

            override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
                when (t) {
                    is UnknownHostException -> {
                        Log.e("UnknownHostException", "onFailure: ${t.message.toString()}")
                    }
                    else -> {
                        Log.e("postRegister", "onFailure: ${t.message.toString()}")
                    }
                }
            }
        })
    }

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = { StoryPagingSource(apiService, token) }
        ).liveData
    }

    suspend fun saveSession(user: User) {
        return userPreference.saveSession(user)
    }

    suspend fun isLoggedIn() {
        return userPreference.isLoggedIn()
    }

    suspend fun register(name: String,  email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    suspend fun getStoriesLocation(token: String): StoryResponse {
        return apiService.getStoriesLocation(token)
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: Repository? = null

        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService, userPreference)
            }.also { instance = it }
    }
}