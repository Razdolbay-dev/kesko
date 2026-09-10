package com.companykesko.keskoapp.ui.channels

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.companykesko.keskoapp.data.ChannelDetail
import com.companykesko.keskoapp.ui.ChannelDetailState
import com.companykesko.keskoapp.ui.ChannelDetailViewModel
import com.companykesko.keskoapp.ui.common.InfoRow
import com.companykesko.keskoapp.ui.common.formatEpgTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelDetailScreen(
    channelId: Int,
    channelIndex: Int,
    onBack: () -> Unit,
    onPlay: (
        url: String,
        title: String,
        subtitle: String?,
        epgCurrent: String?,
        epgNext: String?
    ) -> Unit,
    viewModel: ChannelDetailViewModel = viewModel(key = "channelDetail_$channelId")
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(channelId) {
        viewModel.load(channelId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("О канале", fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (val s = state) {
                is ChannelDetailState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ChannelDetailState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Ошибка: ${s.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is ChannelDetailState.Success -> {
                    ChannelDetailContent(
                        channel = s.channel,
                        onPlay = onPlay
                    )
                }
            }
        }
    }
}

@Composable
private fun ChannelDetailContent(
    channel: ChannelDetail,
    onPlay: (
        url: String,
        title: String,
        subtitle: String?,
        epgCurrent: String?,
        epgNext: String?
    ) -> Unit
) {
    val epgCurrentName = channel.epgCurrent?.name
    val epgNextName = channel.epg
        .firstOrNull { it.isFuture == true }
        ?.name

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Логотип + название
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (!channel.logo.isNullOrBlank()) {
                        AsyncImage(
                            model = "http://sp.pit.su/stalker_portal/misc/logos/original/${channel.logo}",
                            contentDescription = channel.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Icon(
                            Icons.Default.Tv,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = channel.name ?: "Без названия",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Канал №${channel.number}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    channel.genreTitle?.let {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = it,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Кнопка Смотреть
        item {
            Button(
                onClick = {
                    channel.link?.takeIf { it.isNotBlank() }?.let { link ->
                        onPlay(
                            link,
                            channel.name ?: "Канал",
                            "Канал №${channel.number}" +
                                    (channel.genreTitle?.let { " · $it" } ?: ""),
                            epgCurrentName,
                            epgNextName
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !channel.link.isNullOrBlank()
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Смотреть")
            }
        }

        // Сейчас в эфире
        channel.epgCurrent?.let { current ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE53935))
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Сейчас в эфире",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = current.name ?: "Без названия",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${formatEpgTime(current.time)} – ${formatEpgTime(current.timeTo)}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // Программа передач
        if (channel.epg.isNotEmpty()) {
            item {
                Text(
                    text = "Программа передач",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            val upcoming = channel.epg.filter { it.isCurrent == true || it.isFuture == true }
            val past = channel.epg.filter { it.isPast == true }

            items(upcoming, key = { it.id }) { program ->
                EpgProgramItem(program = program)
            }

            if (past.isNotEmpty()) {
                item {
                    Text(
                        text = "Ранее",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
                items(past, key = { it.id }) { program ->
                    EpgProgramItem(program = program)
                }
            }
        }

        // Информация
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = "Информация",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))

                InfoRow(label = "ID", value = channel.id.toString())
                InfoRow(label = "Номер", value = channel.number.toString())
                channel.xmltvId?.takeIf { it.isNotBlank() }?.let {
                    InfoRow(label = "XMLTV ID", value = it)
                }
                channel.tvGenreId?.let {
                    InfoRow(label = "Жанр ID", value = it.toString())
                }
                channel.status?.let {
                    InfoRow(
                        label = "Статус",
                        value = if (it == 1) "Активен" else "Неактивен"
                    )
                }
                channel.tvArchiveDuration?.let {
                    if (it > 0) {
                        InfoRow(label = "Архив", value = "$it ч")
                    }
                }
                channel.modified?.take(10)?.let {
                    InfoRow(label = "Обновлён", value = it)
                }
            }
        }

        // Ссылка на поток
        channel.link?.takeIf { it.isNotBlank() }?.let { link ->
            item {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = "Поток",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = link,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}