package com.raida.intermediatesubmission.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.raida.intermediatesubmission.api.ApiConfig
import com.raida.intermediatesubmission.model.ListStoryItem
import com.raida.intermediatesubmission.model.LoginResult
import com.raida.intermediatesubmission.model.StoriesResponse
import com.raida.intermediatesubmission.preference.UserPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val pref: UserPreference): ViewModel() {
    private val _listStories = MutableLiveData<List<ListStoryItem>>()
    val listStories : LiveData<List<ListStoryItem>> = _listStories

    private val _status = MutableLiveData<Boolean>()
    val status: LiveData<Boolean> = _status

    fun getUser(): LiveData<LoginResult> = pref.getUser().asLiveData()

    fun getStoriesWithLocation(token: String){
        val client = ApiConfig.getApiService().getStoriesWithLocation(token)
        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>,
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null){
                        _listStories.value = responseBody.listStory
                        _status.value = responseBody.error
                    }
                }else{
                    _status.value = true
                    Log.e(TAG, "onFailure: ${response.message()}")
                }


            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    companion object{
        private const val TAG = "MapsActivity"
    }
}