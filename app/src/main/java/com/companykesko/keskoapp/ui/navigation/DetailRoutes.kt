package com.companykesko.keskoapp.ui.navigation

enum class DetailType { MOVIE, TV, CHANNEL, PLAYER }

data class DetailRoute(
    val type: DetailType,
    val id: Long,
    val streamUrl: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val channelIndex: Int = -1,
    val epgCurrentName: String? = null,
    val epgNextName: String? = null
)