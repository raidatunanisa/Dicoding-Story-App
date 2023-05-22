package com.raida.intermediatesubmission.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.raida.intermediatesubmission.model.LoginResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {


    fun getUser(): Flow<LoginResult> {
        return dataStore.data.map { preferences ->
            LoginResult(
                preferences[NAME_KEY] ?: "",
                preferences[ID_KEY] ?: "",
                preferences[TOKEN_KEY] ?: "",
            )
        }
    }


    suspend fun saveUser(user: LoginResult) {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = user.name
            preferences[TOKEN_KEY] = user.token
            preferences[ID_KEY] = user.userId
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }


    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val NAME_KEY = stringPreferencesKey("name")
        private val ID_KEY = stringPreferencesKey("id")
        private val TOKEN_KEY = stringPreferencesKey("token")


        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}