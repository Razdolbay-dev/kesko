package com.companykesko.keskoapp.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.keepScreenOn
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

data class PlayerChannelItem(
    val id: Int,
    val number: Int,
    val name: String,
    val logo: String?,
    val streamUrl: String
)

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    streamUrl: String,
    title: String,
    subtitle: String? = null,
    epgCurrent: String? = null,
    epgNext: String? = null,
    currentChannelId: Int = -1,
    allChannels: List<PlayerChannelItem> = emptyList(),
    onSelectChannel: ((Int) -> Unit)? = null,
    onPrevChannel: (() -> Unit)? = null,
    onNextChannel: (() -> Unit)? = null,
    onChannelListClick: (() -> Unit)? = null,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = context as? Activity

    // Стейт открытия сайдбара
    var sidebarOpen by remember { mutableStateOf(false) }

    // Стейт списка каналов — переживёт закрытие/открытие сайдбара
    val sidebarListState = rememberLazyListState()

    // Показываем ли уже сайдбар хоть раз
    var sidebarWasOpened by remember { mutableStateOf(false) }

// Автопрокрутка к текущему каналу при первом открытии
    LaunchedEffect(sidebarOpen, currentChannelId, allChannels) {
        if (sidebarOpen && !sidebarWasOpened && allChannels.isNotEmpty()) {
            val idx = allChannels.indexOfFirst { it.id == currentChannelId }
            if (idx >= 0) {
                sidebarListState.scrollToItem(idx)
            }
            sidebarWasOpened = true
        }
    }

    // ===== Форсируем ландшафт =====
    DisposableEffect(activity) {
        val previous = activity?.requestedOrientation
        activity?.requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        onDispose {
            activity?.requestedOrientation =
                previous ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    // ===== Скрываем системные панели =====
    DisposableEffect(activity) {
        val window = activity?.window
        val controller = window?.let {
            WindowCompat.getInsetsController(it, it.decorView)
        }
        controller?.hide(WindowInsetsCompat.Type.systemBars())
        controller?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        onDispose {
            controller?.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    // ===== ExoPlayer (один на весь экран) =====
    val exoPlayer = remember {
        val renderersFactory = DefaultRenderersFactory(context)
            .setExtensionRendererMode(
                DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
            )
        ExoPlayer.Builder(context, renderersFactory).build().apply {
            playWhenReady = true
            volume = 1.0f
        }
    }

    // ===== Меняем источник при смене URL =====
    LaunchedEffect(streamUrl) {
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("KESKOAPP/1.0")
            .setAllowCrossProtocolRedirects(true)

        val mediaItemBuilder = MediaItem.Builder().setUri(streamUrl)
        if (streamUrl.contains(".m3u8", ignoreCase = true)) {
            mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_M3U8)
        }

        val mediaSource = HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mediaItemBuilder.build())

        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    var isBuffering by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                isBuffering = state == Player.STATE_BUFFERING
                if (state == Player.STATE_READY) errorMessage = null
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                errorMessage = error.message ?: "Ошибка воспроизведения"
                isBuffering = false
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> exoPlayer.pause()
                Lifecycle.Event.ON_START -> exoPlayer.play()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // ===== Автоскрытие бара =====
    var controlsVisible by remember { mutableStateOf(true) }
    LaunchedEffect(controlsVisible, sidebarOpen) {
        if (controlsVisible && !sidebarOpen) {
            delay(4000)
            controlsVisible = false
        }
    }

    // Перехватываем системную «назад», пока открыт сайдбар
    BackHandler(enabled = sidebarOpen) {
        sidebarOpen = false
    }

    // ===== UI =====
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .keepScreenOn()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Игнорируем тап, если открыт сайдбар — там своя обработка
                if (!sidebarOpen) {
                    controlsVisible = !controlsVisible
                }
            }
    ) {
        // ===== Видео =====
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            }
        )

        // ===== Индикатор буферизации =====
        if (isBuffering) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // ===== Ошибка =====
        errorMessage?.let { msg ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Text(
                    text = msg,
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // ===== Затемнение под сайдбаром =====
        if (sidebarOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { sidebarOpen = false }
            )
        }

        // ===== Сайдбар справа =====
        AnimatedVisibility(
            visible = sidebarOpen,
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it }),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            PlayerChannelSidebar(
                channels = allChannels,
                currentChannelId = currentChannelId,
                listState = sidebarListState,        // ← добавили
                onSelect = { channelId ->
                    onSelectChannel?.invoke(channelId)
                    sidebarOpen = false
                },
                onClose = { sidebarOpen = false }
            )
        }

        // ===== Верхний скрывающийся бар =====
        PlayerOverlayBar(
            visible = controlsVisible && !sidebarOpen,
            title = title,
            epgCurrent = epgCurrent,
            epgNext = epgNext,
            onChannelList = { sidebarOpen = true },
            onPrevChannel = { onPrevChannel?.invoke() },
            onNextChannel = { onNextChannel?.invoke() },
            onBack = onBack,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

// ============================================================
//  Верхний бар с EPG и кнопками
// ============================================================

@Composable
private fun PlayerOverlayBar(
    visible: Boolean,
    title: String,
    epgCurrent: String?,
    epgNext: String?,
    onChannelList: () -> Unit,
    onPrevChannel: () -> Unit,
    onNextChannel: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 2 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it / 2 }),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.85f),
                            Color.Black.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = Color.White
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                androidx.compose.material3.Text(
                    text = title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                epgCurrent?.let {
                    androidx.compose.material3.Text(
                        text = "Сейчас: $it",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                epgNext?.let {
                    androidx.compose.material3.Text(
                        text = "Далее: $it",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(onClick = onChannelList) {
                Icon(
                    Icons.AutoMirrored.Filled.List,
                    contentDescription = "Список каналов",
                    tint = Color.White
                )
            }
            IconButton(onClick = onPrevChannel) {
                Icon(
                    Icons.Filled.SkipPrevious,
                    contentDescription = "Предыдущий канал",
                    tint = Color.White
                )
            }
            IconButton(onClick = onNextChannel) {
                Icon(
                    Icons.Filled.SkipNext,
                    contentDescription = "Следующий канал",
                    tint = Color.White
                )
            }
        }
    }
}

// ============================================================
//  Сайдбар со списком каналов
// ============================================================

@Composable
private fun PlayerChannelSidebar(
    channels: List<PlayerChannelItem>,
    currentChannelId: Int,
    onSelect: (Int) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState
) {
    Surface(
        modifier = modifier
            .width(300.dp)
            .fillMaxHeight(),
        color = Color.Black.copy(alpha = 0.85f)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ===== Шапка =====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.Text(
                    text = "Каналы",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onClose) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Закрыть",
                        tint = Color.White
                    )
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

            // ===== Список =====
            if (channels.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.Text(
                        text = "Список каналов пуст",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(channels, key = { it.id }) { channel ->
                        PlayerChannelRow(
                            channel = channel,
                            selected = channel.id == currentChannelId,
                            onClick = { onSelect(channel.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerChannelRow(
    channel: PlayerChannelItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (selected) Color.White.copy(alpha = 0.15f) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Номер
        androidx.compose.material3.Text(
            text = channel.number.toString(),
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp,
            modifier = Modifier.width(28.dp)
        )

        // Логотип
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White.copy(alpha = 0.05f)),
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
                    Icons.Filled.Tv,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(Modifier.width(10.dp))

        // Название
        androidx.compose.material3.Text(
            text = channel.name,
            color = if (selected) Color.White else Color.White.copy(alpha = 0.85f),
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        // Индикатор текущего канала
        if (selected) {
            Icon(
                Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}