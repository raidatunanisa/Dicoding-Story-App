package com.raida.intermediatesubmission.api

import com.raida.intermediatesubmission.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("register")
    @FormUrlEncoded
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @POST("login")
    @FormUrlEncoded
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") bearer: String,
        @Query("page") page: Int,
        @Query("size") size: Int): StoriesResponse

    @GET("stories?location=1")
    fun getStoriesWithLocation(@Header("Authorization") bearer: String): Call<StoriesResponse>

    @GET("stories/{id}")
    fun getDetailStory(
        @Header("Authorization") bearer : String,
        @Path("id") id: String
    ): Call<DetailStoryResponse>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") bearer : String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<AddStoryResponse>
}