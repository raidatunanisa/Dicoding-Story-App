package com.raida.intermediatesubmission.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.raida.intermediatesubmission.preference.UserPreference
import com.raida.intermediatesubmission.api.ApiConfig
import com.raida.intermediatesubmission.model.LoginResponse
import com.raida.intermediatesubmission.model.LoginResult
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreference): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _status = MutableLiveData<Boolean>()
    val status: LiveData<Boolean> = _status


    private fun saveUser(user: LoginResult){
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }

    fun login(email: String, password: String){
        _isLoading.value = true
        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful){
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error){
                        _status.value = responseBody.error
                        saveUser(responseBody.loginResult)
                    }
                }else{
                    Log.e(TAG, "onFailure: ${response.message()}")
                    _status.value = true
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
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