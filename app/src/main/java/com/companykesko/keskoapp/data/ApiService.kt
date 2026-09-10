package com.companykesko.keskoapp.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("api/users/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("api/itv/channels/")
    suspend fun getChannels(): ChannelsResponse

    @GET("api/itv/channels/{id}")
    suspend fun getChannelDetail(@Path("id") id: Int): ChannelDetailResponse

    @GET("api/content/movies")
    suspend fun getMovies(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 25,
        @Query("genre_id") genreId: Int? = null,
        @Query("is_published") isPublished: Int = 1
    ): MoviesResponse

    @GET("api/content/movies/{id}")
    suspend fun getMovieDetail(@Path("id") id: Long): MovieDetailResponse

    @GET("api/content/tv")
    suspend fun getTvShows(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 25,
        @Query("genre_id") genreId: Int? = null,
        @Query("is_published") isPublished: Int = 1
    ): TvShowsResponse

    @GET("api/content/tv/{id}")
    suspend fun getTvShowDetail(@Path("id") id: Long): TvShowDetailResponse
}