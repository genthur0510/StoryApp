package com.genthur.storyapp.di

import android.content.Context
import com.genthur.storyapp.repository.Repository
import com.genthur.storyapp.data.local.datastore.UserPreference
import com.genthur.storyapp.data.local.datastore.dataStore
import com.genthur.storyapp.data.remote.api.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): Repository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return Repository.getInstance(apiService, pref)
    }
}