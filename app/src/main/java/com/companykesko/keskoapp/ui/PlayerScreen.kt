package com.companykesko.keskoapp.ui

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.keepScreenOn
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.common.TrackSelectionParameters

/**
 * Экран плеера. Принимает готовый URL потока (HLS .m3u8 или что-то другое).
 *
 * @param streamUrl  ссылка на HLS-плейлист
 * @param title      заголовок (название канала / фильма / серии)
 * @param subtitle   подзаголовок (номер канала, год, длительность)
 * @param onBack     колбэк назад
 */
@OptIn(UnstableApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    streamUrl: String,
    title: String,
    subtitle: String? = null,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Создаём плеер один раз при входе на экран (или при смене URL)
    val exoPlayer = remember(streamUrl) {
        val renderersFactory = DefaultRenderersFactory(context)
            .setExtensionRendererMode(
                DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
            )

        ExoPlayer.Builder(context, renderersFactory).build().apply {
            val dataSourceFactory = DefaultHttpDataSource.Factory()
                .setUserAgent("KESKOAPP/1.0")
                .setAllowCrossProtocolRedirects(true)

            val mediaItemBuilder = MediaItem.Builder()
                .setUri(streamUrl)
            if (streamUrl.contains(".m3u8", ignoreCase = true)) {
                mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_M3U8)
            }

            val mediaSource = HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItemBuilder.build())

            setMediaSource(mediaSource)
            prepare()
            playWhenReady = true
            volume = 1.0f
        }
    }

    // Стейт для буферизации и ошибок
    var isBuffering by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Подписываемся на события плеера
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

    // Авто-пауза при уходе с экрана (жизненный цикл)
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

    Scaffold(
        topBar = {
            @kotlin.OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            maxLines = 1
                        )
                        subtitle?.let {
                            Text(
                                text = it,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
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
        Column(modifier = Modifier.padding(innerPadding)) {

            // ===== Видео =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)
                    .keepScreenOn()
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = false
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            clipToOutline = true
                        }
                    }
                )

                if (isBuffering) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }

                errorMessage?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = msg,
                            color = Color.White,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // ===== Информация под плеером =====
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                subtitle?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = it,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Поток: $streamUrl",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}