package com.companykesko.keskoapp.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("api/users/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("api/itv/channels/")
    suspend fun getChannels(): ChannelsResponse
}