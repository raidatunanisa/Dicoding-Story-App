package com.raida.intermediatesubmission.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.raida.intermediatesubmission.api.ApiConfig
import com.raida.intermediatesubmission.preference.UserPreference
import com.raida.intermediatesubmission.model.AddStoryResponse
import com.raida.intermediatesubmission.model.LoginResult
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStoryViewModel(private val pref: UserPreference) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _status = MutableLiveData<Boolean>()
    val status: LiveData<Boolean> = _status

    fun getUser(): LiveData<LoginResult> = pref.getUser().asLiveData()

    fun addStory(token: String, multipart: MultipartBody.Part, description: RequestBody) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().uploadStory(token, multipart, description)
        client.enqueue(object : Callback<AddStoryResponse> {
            override fun onResponse(
                call: Call<AddStoryResponse>,
                response: Response<AddStoryResponse>,
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {

                        _status.value = responseBody.error
                    }
                } else {
                    _status.value = true
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                _status.value = true
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    companion object {
        private const val TAG = "AddStoryActivity"
    }
}