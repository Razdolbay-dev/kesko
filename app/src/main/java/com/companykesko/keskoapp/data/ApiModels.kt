package com.companykesko.keskoapp.data

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("user") val user: User?,
    @SerializedName("token") val token: String?
)

data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("bgb_id") val bgbId: String?,
    @SerializedName("ip") val ip: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("comment") val comment: String?,
    @SerializedName("last_visit") val lastVisit: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("balance") val balance: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("is_active") val isActive: Int?,
    @SerializedName("is_admin") val isAdmin: Int?,
    @SerializedName("last_ip") val lastIp: String?,
    @SerializedName("user_agent") val userAgent: String?
)

data class ChannelsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("limit") val limit: Int,
    @SerializedName("offset") val offset: Int,
    @SerializedName("channels") val channels: List<Channel>
)

data class Channel(
    @SerializedName("id") val id: Int,
    @SerializedName("number") val number: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("xmltv_id") val xmltvId: String?,
    @SerializedName("logo") val logo: String?,
    @SerializedName("tv_genre_id") val tvGenreId: Int?,
    @SerializedName("link") val link: String?,
    @SerializedName("status") val status: Int?,
    @SerializedName("tv_archive_duration") val tvArchiveDuration: Int?,
    @SerializedName("tv_archive_server_id") val tvArchiveServerId: Int?,
    @SerializedName("tv_archive_uuid") val tvArchiveUuid: String?,
    @SerializedName("tv_archive_status") val tvArchiveStatus: Int?,
    @SerializedName("modified") val modified: String?,
    @SerializedName("genre_title") val genreTitle: String?,
    @SerializedName("genre_number") val genreNumber: Int?,
    @SerializedName("genre_censored") val genreCensored: Int?
)