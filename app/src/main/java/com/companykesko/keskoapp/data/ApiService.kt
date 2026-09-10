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
        @Query("title") title: String? = null,
        @Query("original_title") originalTitle: String? = null,
        @Query("genre_id") genreId: Int? = null,
        @Query("year") year: Int? = null,
        @Query("year_from") yearFrom: Int? = null,
        @Query("year_to") yearTo: Int? = null,
        @Query("vote_min") voteMin: Float? = null,
        @Query("vote_max") voteMax: Float? = null,
        @Query("is_published") isPublished: Int? = null,
        @Query("data_status") dataStatus: String? = null,
        @Query("original_language") originalLanguage: String? = null,
        @Query("sort") sort: String = "popularity DESC"
    ): MoviesResponse

    @GET("api/content/movies/{id}")
    suspend fun getMovieDetail(@Path("id") id: Long): MovieDetailResponse

    @GET("api/content/tv")
    suspend fun getTvShows(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 25,
        @Query("name") name: String? = null,
        @Query("original_name") originalName: String? = null,
        @Query("genre_id") genreId: Int? = null,
        @Query("year") year: Int? = null,
        @Query("year_from") yearFrom: Int? = null,
        @Query("year_to") yearTo: Int? = null,
        @Query("vote_min") voteMin: Float? = null,
        @Query("vote_max") voteMax: Float? = null,
        @Query("status") status: String? = null,
        @Query("is_published") isPublished: Int? = null,
        @Query("data_status") dataStatus: String? = null,
        @Query("original_language") originalLanguage: String? = null,
        @Query("has_seasons") hasSeasons: Boolean? = null,
        @Query("sort") sort: String = "popularity DESC"
    ): TvShowsResponse

    @GET("api/content/tv/{id}")
    suspend fun getTvShowDetail(@Path("id") id: Long): TvShowDetailResponse

    @GET("api/content/genres")
    suspend fun getGenres(): List<Genre>
}