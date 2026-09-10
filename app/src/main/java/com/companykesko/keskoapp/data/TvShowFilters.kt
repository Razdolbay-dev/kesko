package com.companykesko.keskoapp.data

data class TvShowFilters(
    val name: String? = null,
    val originalName: String? = null,
    val genreId: Int? = null,
    val year: Int? = null,
    val yearFrom: Int? = null,
    val yearTo: Int? = null,
    val voteMin: Float? = null,
    val voteMax: Float? = null,
    val status: String? = null,
    val originalLanguage: String? = null,
    val hasSeasons: Boolean? = null,
    val isPublished: Int? = 1,
    val dataStatus: String? = null
)

enum class TvShowSort(val apiValue: String, val label: String) {
    POPULARITY_DESC("popularity DESC", "По популярности"),
    POPULARITY_ASC("popularity ASC", "По популярности ↑"),
    RATING_DESC("vote_average DESC", "По рейтингу (высокие)"),
    RATING_ASC("vote_average ASC", "По рейтингу (низкие)"),
    DATE_DESC("first_air_date DESC", "Сначала новые"),
    DATE_ASC("first_air_date ASC", "Сначала старые"),
    NAME_ASC("name ASC", "Название: А → Я"),
    NAME_DESC("name DESC", "Название: Я → А")
}