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

// ===== Детали канала =====

data class ChannelDetailResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("channel") val channel: ChannelDetail?
)

data class ChannelDetail(
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
    @SerializedName("genre_censored") val genreCensored: Int?,
    @SerializedName("epg") val epg: List<EpgProgram> = emptyList(),
    @SerializedName("epg_current") val epgCurrent: EpgProgram? = null
)

// ===== EPG =====

data class EpgProgram(
    @SerializedName("id") val id: Long,
    @SerializedName("ch_id") val chId: Int?,
    @SerializedName("time") val time: String?,
    @SerializedName("time_to") val timeTo: String?,
    @SerializedName("duration") val duration: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("descr") val descr: String?,
    @SerializedName("real_id") val realId: String?,
    @SerializedName("category") val category: String?,
    @SerializedName("director") val director: String?,
    @SerializedName("actor") val actor: String?,
    @SerializedName("duration_seconds") val durationSeconds: Int?,
    @SerializedName("is_current") val isCurrent: Boolean?,
    @SerializedName("is_past") val isPast: Boolean?,
    @SerializedName("is_future") val isFuture: Boolean?
)

// ===== Фильмы =====

data class MoviesResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: List<Movie>,
    @SerializedName("pagination") val pagination: Pagination?
)

data class Pagination(
    @SerializedName("page") val page: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("is_published") val isPublished: Int,
    @SerializedName("total") val total: Long,
    @SerializedName("total_pages") val totalPages: Int
)

data class Movie(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String?,
    @SerializedName("original_title") val originalTitle: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("runtime") val runtime: Int?,
    @SerializedName("popularity") val popularity: String?,
    @SerializedName("vote_average") val voteAverage: String?,
    @SerializedName("vote_count") val voteCount: Int?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("is_published") val isPublished: Boolean?,
    @SerializedName("data_status") val dataStatus: String?,
    @SerializedName("collection") val collection: MovieCollection?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

data class MovieCollection(
    @SerializedName("name") val name: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?
)

// ===== Детали фильма =====

data class MovieDetailResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: MovieDetail?
)

data class MovieDetail(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String?,
    @SerializedName("original_title") val originalTitle: String?,
    @SerializedName("original_language") val originalLanguage: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("tagline") val tagline: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("runtime") val runtime: Int?,
    @SerializedName("budget") val budget: Long?,
    @SerializedName("revenue") val revenue: Long?,
    @SerializedName("popularity") val popularity: String?,
    @SerializedName("vote_average") val voteAverage: String?,
    @SerializedName("vote_count") val voteCount: Int?,
    @SerializedName("status") val status: String?,
    @SerializedName("imdb_id") val imdbId: String?,
    @SerializedName("homepage") val homepage: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("is_published") val isPublished: Int?,
    @SerializedName("data_status") val dataStatus: String?,
    @SerializedName("genres") val genres: List<Genre> = emptyList(),
    @SerializedName("languages") val languages: List<Language> = emptyList(),
    @SerializedName("countries") val countries: List<Country> = emptyList(),
    @SerializedName("companies") val companies: List<Company> = emptyList(),
    @SerializedName("cast") val cast: List<CastMember> = emptyList(),
    @SerializedName("crew") val crew: List<CrewMember> = emptyList(),
    @SerializedName("similar") val similar: List<SimilarMovie> = emptyList()
)

data class Genre(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?
)

data class Language(
    @SerializedName("iso_code") val isoCode: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("english_name") val englishName: String?
)

data class Country(
    @SerializedName("iso_code") val isoCode: String?,
    @SerializedName("name") val name: String?
)

data class Company(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String?,
    @SerializedName("logo_path") val logoPath: String?,
    @SerializedName("origin_country") val originCountry: String?
)

data class CastMember(
    @SerializedName("person_id") val personId: Long,
    @SerializedName("name") val name: String?,
    @SerializedName("original_name") val originalName: String?,
    @SerializedName("character_name") val characterName: String?,
    @SerializedName("profile_path") val profilePath: String?,
    @SerializedName("order_position") val orderPosition: Int?,
    @SerializedName("gender") val gender: Int?
)

data class CrewMember(
    @SerializedName("person_id") val personId: Long,
    @SerializedName("name") val name: String?,
    @SerializedName("original_name") val originalName: String?,
    @SerializedName("job") val job: String?,
    @SerializedName("department") val department: String?,
    @SerializedName("profile_path") val profilePath: String?
)

data class SimilarMovie(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String?,
    @SerializedName("original_title") val originalTitle: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val voteAverage: String?,
    @SerializedName("popularity") val popularity: String?
)

// ===== Сериалы (TV) =====

data class TvShowsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: List<TvShow>,
    @SerializedName("pagination") val pagination: Pagination?
)

data class TvShow(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String?,
    @SerializedName("original_name") val originalName: String?,
    @SerializedName("original_language") val originalLanguage: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("last_air_date") val lastAirDate: String?,
    @SerializedName("number_of_seasons") val numberOfSeasons: Int?,
    @SerializedName("number_of_episodes") val numberOfEpisodes: Int?,
    @SerializedName("status") val status: String?,
    @SerializedName("popularity") val popularity: String?,
    @SerializedName("vote_average") val voteAverage: String?,
    @SerializedName("vote_count") val voteCount: Int?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("in_production") val inProduction: Boolean?,
    @SerializedName("is_published") val isPublished: Boolean?,
    @SerializedName("data_status") val dataStatus: String?,
    @SerializedName("overview") val overview: String?
)

// ===== Детали сериала =====

data class TvShowDetailResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: TvShowDetail?
)

data class TvShowDetail(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String?,
    @SerializedName("original_name") val originalName: String?,
    @SerializedName("original_language") val originalLanguage: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("tagline") val tagline: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("last_air_date") val lastAirDate: String?,
    @SerializedName("number_of_seasons") val numberOfSeasons: Int?,
    @SerializedName("number_of_episodes") val numberOfEpisodes: Int?,
    @SerializedName("episode_run_time") val episodeRunTime: Int?,
    @SerializedName("status") val status: String?,
    @SerializedName("popularity") val popularity: String?,
    @SerializedName("vote_average") val voteAverage: String?,
    @SerializedName("vote_count") val voteCount: Int?,
    @SerializedName("homepage") val homepage: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("in_production") val inProduction: Int?,
    @SerializedName("is_published") val isPublished: Int?,
    @SerializedName("data_status") val dataStatus: String?,
    @SerializedName("genres") val genres: List<Genre> = emptyList(),
    @SerializedName("languages") val languages: List<Language> = emptyList(),
    @SerializedName("countries") val countries: List<Country> = emptyList(),
    @SerializedName("companies") val companies: List<Company> = emptyList(),
    @SerializedName("seasons") val seasons: List<Season> = emptyList(),
    @SerializedName("cast") val cast: List<CastMember> = emptyList(),
    @SerializedName("crew") val crew: List<CrewMember> = emptyList(),
    @SerializedName("similar") val similar: List<SimilarTvShow> = emptyList()
)

data class Season(
    @SerializedName("id") val id: Long,
    @SerializedName("season_number") val seasonNumber: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("air_date") val airDate: String?,
    @SerializedName("episode_count") val episodeCount: Int?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("episodes") val episodes: List<Episode> = emptyList()
)

data class Episode(
    @SerializedName("id") val id: Long,
    @SerializedName("episode_number") val episodeNumber: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("air_date") val airDate: String?,
    @SerializedName("runtime") val runtime: Int?,
    @SerializedName("vote_average") val voteAverage: String?,
    @SerializedName("vote_count") val voteCount: Int?,
    @SerializedName("still_path") val stillPath: String?
)

data class SimilarTvShow(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String?,
    @SerializedName("original_name") val originalName: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val voteAverage: String?,
    @SerializedName("popularity") val popularity: String?,
    @SerializedName("number_of_seasons") val numberOfSeasons: Int?,
    @SerializedName("number_of_episodes") val numberOfEpisodes: Int?
)



