package com.companykesko.keskoapp.data

/**
 * Набор фильтров для запроса фильмов.
 * null = не применять этот фильтр.
 */
data class MovieFilters(
    val title: String? = null,
    val originalTitle: String? = null,
    val genreId: Int? = null,
    val year: Int? = null,
    val yearFrom: Int? = null,
    val yearTo: Int? = null,
    val voteMin: Float? = null,
    val voteMax: Float? = null,
    val originalLanguage: String? = null,
    val isPublished: Int? = 1,
    val dataStatus: String? = null
)

enum class MovieSort(val apiValue: String, val label: String) {
    POPULARITY_DESC("popularity DESC", "По популярности"),
    POPULARITY_ASC("popularity ASC", "По популярности ↑"),
    RATING_DESC("vote_average DESC", "По рейтингу (высокие)"),
    RATING_ASC("vote_average ASC", "По рейтингу (низкие)"),
    DATE_DESC("release_date DESC", "Сначала новые"),
    DATE_ASC("release_date ASC", "Сначала старые"),
    TITLE_ASC("title ASC", "Название: А → Я"),
    TITLE_DESC("title DESC", "Название: Я → А")
}