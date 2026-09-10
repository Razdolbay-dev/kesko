package com.companykesko.keskoapp.ui.tvshows

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.companykesko.keskoapp.data.TvShowDetail
import com.companykesko.keskoapp.ui.movies.CastItem

@Composable
fun TvShowDetailContent(
    show: TvShowDetail,
    listState: LazyListState,
    onSimilarClick: (Long) -> Unit
) {
    var selectedSeasonIndex by remember(show.id) { mutableStateOf(0) }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        // ===== Backdrop =====
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                if (!show.backdropPath.isNullOrBlank()) {
                    AsyncImage(
                        model = "http://inpulse.pit.su/api/image/w780${show.backdropPath}",
                        contentDescription = show.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.6f)
                                    )
                                )
                            )
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
            }
        }

        // ===== Постер + основная инфа =====
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                if (!show.posterPath.isNullOrBlank()) {
                    AsyncImage(
                        model = "http://inpulse.pit.su/api/image/w300${show.posterPath}",
                        contentDescription = show.name,
                        modifier = Modifier
                            .size(width = 110.dp, height = 165.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(12.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = show.name ?: "Без названия",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    show.originalName?.takeIf { it != show.name }?.let {
                        Text(
                            text = it,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    show.tagline?.takeIf { it.isNotBlank() }?.let {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = it,
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    val years = buildString {
                        val first = show.firstAirDate?.take(4)
                        val last = show.lastAirDate?.take(4)
                        if (first != null) {
                            append(first)
                            if (last != null && last != first) append(" – $last")
                        }
                    }
                    if (years.isNotBlank()) {
                        Text(
                            text = years,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        show.numberOfSeasons?.let {
                            Text("$it сез.", fontSize = 13.sp)
                            Text(
                                "  •  ",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        show.numberOfEpisodes?.let {
                            Text("$it эп.", fontSize = 13.sp)
                            Text(
                                "  •  ",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        show.voteAverage?.let { rating ->
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(2.dp))
                            Text(rating, fontSize = 13.sp)
                        }
                    }

                    show.status?.let {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = it,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // ===== Жанры =====
        if (show.genres.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    show.genres.forEach { genre ->
                        genre.name?.let {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = it,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(
                                        horizontal = 10.dp,
                                        vertical = 4.dp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // ===== Описание =====
        show.overview?.takeIf { it.isNotBlank() }?.let { overview ->
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Описание",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = overview,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // ===== Сезоны =====
        if (show.seasons.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = "Сезоны",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(Modifier.height(8.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(show.seasons, key = { _, s -> s.id }) { index, season ->
                            val selected = index == selectedSeasonIndex
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = if (selected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (selected)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .clickable { selectedSeasonIndex = index }
                            ) {
                                Text(
                                    text = season.name ?: "Сезон ${season.seasonNumber}",
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(
                                        horizontal = 14.dp,
                                        vertical = 8.dp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // ===== Серии выбранного сезона =====
        val currentSeason = show.seasons.getOrNull(selectedSeasonIndex)
        if (currentSeason != null && currentSeason.episodes.isNotEmpty()) {
            item {
                Text(
                    text = "Серии",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            items(currentSeason.episodes, key = { it.id }) { episode ->
                EpisodeItem(episode = episode, onClick = {
                    // TODO: плеер
                })
            }
        } else if (currentSeason != null) {
            item {
                Text(
                    text = "В этом сезоне пока нет серий",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // ===== Актёры =====
        val uniqueCast = show.cast
            .distinctBy { it.personId }
            .sortedBy { it.orderPosition ?: Int.MAX_VALUE }
            .take(15)

        if (uniqueCast.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = "В ролях",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(Modifier.height(8.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uniqueCast, key = { it.personId }) { person ->
                            CastItem(person = person)
                        }
                    }
                }
            }
        }

        // ===== Похожие =====
        if (show.similar.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = "Похожие",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(Modifier.height(8.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(show.similar, key = { it.id }) { sim ->
                            SimilarTvShowItem(
                                show = sim,
                                onClick = { onSimilarClick(sim.id) }
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}