package com.raida.intermediatesubmission.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.raida.intermediatesubmission.preference.UserPreference
import com.raida.intermediatesubmission.api.ApiConfig
import com.raida.intermediatesubmission.model.DetailStoryResponse
import com.raida.intermediatesubmission.model.LoginResult
import com.raida.intermediatesubmission.model.Story
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel(private val pref: UserPreference): ViewModel() {

    private val _detailStory = MutableLiveData<Story>()
    val detailStory: LiveData<Story> = _detailStory

    private val _status = MutableLiveData<Boolean>()
    val status: LiveData<Boolean> = _status

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getUser(): LiveData<LoginResult> = pref.getUser().asLiveData()

    fun getDetailStory(token: String, id: String){
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailStory(token, id)
        client.enqueue(object: Callback<DetailStoryResponse>{
            override fun onResponse(
                call: Call<DetailStoryResponse>,
                response: Response<DetailStoryResponse>,
            ) {
                _isLoading.value = false
                if (response.isSuccessful){
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error){
                        _detailStory.value = responseBody.story
                        _status.value = responseBody.error
                    }
                }else{
                    _status.value = true
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                _status.value = true
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    companion object {
        private const val TAG = "LoginActivity"
    }

}