package com.companykesko.keskoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.companykesko.keskoapp.data.CastMember
import com.companykesko.keskoapp.data.Movie
import com.companykesko.keskoapp.data.MovieDetail
import com.companykesko.keskoapp.data.SimilarMovie
import com.companykesko.keskoapp.data.TvShow
import com.companykesko.keskoapp.data.User
import com.companykesko.keskoapp.ui.AuthState
import com.companykesko.keskoapp.ui.AuthViewModel
import com.companykesko.keskoapp.ui.ChannelsState
import com.companykesko.keskoapp.ui.ChannelsViewModel
import com.companykesko.keskoapp.ui.MovieDetailState
import com.companykesko.keskoapp.ui.MovieDetailViewModel
import com.companykesko.keskoapp.ui.MoviesState
import com.companykesko.keskoapp.ui.MoviesViewModel
import com.companykesko.keskoapp.ui.TvShowsState
import com.companykesko.keskoapp.ui.TvShowsViewModel
import com.companykesko.keskoapp.ui.theme.KESKOAPPTheme
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CardDefaults
import com.companykesko.keskoapp.data.ApiClient
import com.companykesko.keskoapp.data.ChannelDetail
import com.companykesko.keskoapp.data.EpgProgram
import com.companykesko.keskoapp.data.Episode
import com.companykesko.keskoapp.data.SimilarTvShow
import com.companykesko.keskoapp.data.TvShowDetail
import com.companykesko.keskoapp.ui.ChannelDetailState
import com.companykesko.keskoapp.ui.ChannelDetailViewModel
import com.companykesko.keskoapp.ui.MoviesViewModelFactory
import com.companykesko.keskoapp.ui.PlayerChannelItem
import com.companykesko.keskoapp.ui.PlayerScreen
import com.companykesko.keskoapp.ui.TvShowDetailState
import com.companykesko.keskoapp.ui.TvShowDetailViewModel
import com.companykesko.keskoapp.ui.TvShowsViewModelFactory
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.saveable.rememberSaveable

// ===== Модель навигации по деталям =====

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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KESKOAPPTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: AuthViewModel = viewModel()) {
    val authState by viewModel.state.collectAsStateWithLifecycle()

    when (val state = authState) {
        is AuthState.Idle, AuthState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is AuthState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ошибка: ${state.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        is AuthState.Success -> {
            MainContent(user = state.user)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(user: User) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val channelsViewModel: ChannelsViewModel = viewModel(key = "channels")
    // Универсальный стек детальных экранов. Пусто = показываем список
    val detailStack = remember { mutableStateListOf<DetailRoute>() }
    // Стейты скролла для всех списков — живут на уровне MainContent
    val channelsListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val moviesListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val tvShowsListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val cartoonsListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val cartoonSerialsListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    // В MainContent
    val detailScrollStates = remember {
        mutableStateMapOf<String, LazyListState>()
    }

    fun listStateFor(key: String): LazyListState {
        return detailScrollStates.getOrPut(key) { LazyListState() }
    }
    val menuItems = listOf(
        "ТВ" to Icons.Default.Tv,
        "Радио" to Icons.Default.Radio,
        "Камеры" to Icons.Default.Videocam,
        "Фильмы" to Icons.Default.Movie,
        "Сериалы" to Icons.Default.LiveTv,
        "Мультфильмы" to Icons.Default.Animation,
        "Мультсериалы" to Icons.Default.ChildCare,
        "Профиль" to Icons.Default.Person
    )

    var selectedIndex by remember { mutableStateOf(0) }

    // ===== Если открыт детальный экран — показываем его и перехватываем «назад» =====
    if (detailStack.isNotEmpty()) {
        BackHandler {
            detailStack.removeAt(detailStack.lastIndex)
        }

        val route = detailStack.last()
        when (route.type) {
            DetailType.MOVIE -> MovieDetailScreen(
                movieId = route.id,
                onBack = { detailStack.removeAt(detailStack.lastIndex) },
                onSimilarClick = { newId ->
                    detailStack.add(DetailRoute(DetailType.MOVIE, newId))
                }
            )
            DetailType.TV -> TvShowDetailScreen(
                showId = route.id,
                onBack = { detailStack.removeAt(detailStack.lastIndex) },
                onSimilarClick = { newId ->
                    detailStack.add(DetailRoute(DetailType.TV, newId))
                }
            )
            DetailType.CHANNEL -> {
                // Находим индекс канала в общем списке
                val channelsState = channelsViewModel.state.collectAsStateWithLifecycle().value
                val channelsList = (channelsState as? ChannelsState.Success)?.channels ?: emptyList()
                val channelIndex = channelsList.indexOfFirst { it.id == route.id.toInt() }

                ChannelDetailScreen(
                    channelId = route.id.toInt(),
                    channelIndex = channelIndex,
                    onBack = { detailStack.removeAt(detailStack.lastIndex) },
                    onPlay = { url, title, subtitle, epgCurrent, epgNext ->
                        detailStack.add(
                            DetailRoute(
                                type = DetailType.PLAYER,
                                id = route.id,
                                streamUrl = url,
                                title = title,
                                subtitle = subtitle,
                                channelIndex = channelIndex,
                                epgCurrentName = epgCurrent,
                                epgNextName = epgNext
                            )
                        )
                    }
                )
            }
            DetailType.PLAYER -> {
                val url = route.streamUrl
                if (url.isNullOrBlank()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Поток недоступен")
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { detailStack.removeAt(detailStack.lastIndex) }) {
                                Text("Назад")
                            }
                        }
                    }
                } else {
                    val channelsState = channelsViewModel.state.collectAsStateWithLifecycle().value
                    val channelsList = (channelsState as? ChannelsState.Success)?.channels ?: emptyList()

                    // Готовим список для сайдбара
                    val playerChannels = remember(channelsList) {
                        channelsList.mapNotNull { ch ->
                            ch.link?.takeIf { it.isNotBlank() }?.let { link ->
                                PlayerChannelItem(
                                    id = ch.id,
                                    number = ch.number,
                                    name = ch.name ?: "Канал ${ch.number}",
                                    logo = ch.logo,
                                    streamUrl = link
                                )
                            }
                        }
                    }

                    val currentChannelId = route.id.toInt()

                    // Колбэк выбора канала из сайдбара
                    val selectChannel: (Int) -> Unit = { newChannelId ->
                        val idx = playerChannels.indexOfFirst { it.id == newChannelId }
                        if (idx >= 0) {
                            val chosen = playerChannels[idx]
                            detailStack[detailStack.lastIndex] = DetailRoute(
                                type = DetailType.PLAYER,
                                id = chosen.id.toLong(),
                                streamUrl = chosen.streamUrl,
                                title = chosen.name,
                                subtitle = "Канал №${chosen.number}",
                                channelIndex = idx,
                                epgCurrentName = null,
                                epgNextName = null
                            )
                            scope.launch {
                                try {
                                    val detail = ApiClient.service.getChannelDetail(chosen.id).channel
                                    val current = detail?.epgCurrent?.name
                                    val nextProg = detail?.epg?.firstOrNull { it.isFuture == true }?.name
                                    val currentRoute = detailStack.lastOrNull()
                                    if (currentRoute?.type == DetailType.PLAYER &&
                                        currentRoute.id == chosen.id.toLong()
                                    ) {
                                        detailStack[detailStack.lastIndex] = currentRoute.copy(
                                            epgCurrentName = current,
                                            epgNextName = nextProg
                                        )
                                    }
                                } catch (_: Exception) { }
                            }
                        }
                    }

                    val scope = rememberCoroutineScope()

                    // Колбэк переключения на предыдущий канал
                    val goPrev: (() -> Unit)? = if (route.channelIndex > 0 && channelsList.isNotEmpty()) {
                        {
                            val prevIndex = route.channelIndex - 1
                            val prev = channelsList[prevIndex]
                            detailStack[detailStack.lastIndex] = DetailRoute(
                                type = DetailType.PLAYER,
                                id = prev.id.toLong(),
                                streamUrl = prev.link,
                                title = prev.name ?: "Канал",
                                subtitle = "Канал №${prev.number}",
                                channelIndex = prevIndex,
                                epgCurrentName = null,
                                epgNextName = null
                            )

                            scope.launch {
                                try {
                                    val detail = ApiClient.service.getChannelDetail(prev.id).channel
                                    val current = detail?.epgCurrent?.name
                                    val nextProg = detail?.epg?.firstOrNull { it.isFuture == true }?.name

                                    // Обновляем маршрут на последнем месте в стеке
                                    val currentRoute = detailStack.lastOrNull()
                                    if (currentRoute?.type == DetailType.PLAYER &&
                                        currentRoute.id == prev.id.toLong()) {
                                        detailStack[detailStack.lastIndex] = currentRoute.copy(
                                            epgCurrentName = current,
                                            epgNextName = nextProg
                                        )
                                    }
                                } catch (_: Exception) {
                                    // Игнорируем — плеер всё равно играет
                                }
                            }
                        }
                    } else null

                    // Колбэк переключения на следующий канал
                    val goNext: (() -> Unit)? = if (route.channelIndex >= 0 && route.channelIndex < channelsList.size - 1) {
                        {
                            val nextIndex = route.channelIndex + 1
                            val next = channelsList[nextIndex]
                            // Сразу переключаем плеер
                            detailStack[detailStack.lastIndex] = DetailRoute(
                                type = DetailType.PLAYER,
                                id = next.id.toLong(),
                                streamUrl = next.link,
                                title = next.name ?: "Канал",
                                subtitle = "Канал №${next.number}",
                                channelIndex = nextIndex,
                                epgCurrentName = null,
                                epgNextName = null
                            )

                            scope.launch {
                                try {
                                    val detail = ApiClient.service.getChannelDetail(next.id).channel
                                    val current = detail?.epgCurrent?.name
                                    val nextProg = detail?.epg?.firstOrNull { it.isFuture == true }?.name

                                    // Обновляем маршрут на последнем месте в стеке
                                    val currentRoute = detailStack.lastOrNull()
                                    if (currentRoute?.type == DetailType.PLAYER &&
                                        currentRoute.id == next.id.toLong()) {
                                        detailStack[detailStack.lastIndex] = currentRoute.copy(
                                            epgCurrentName = current,
                                            epgNextName = nextProg
                                        )
                                    }
                                } catch (_: Exception) {
                                    // Игнорируем — плеер всё равно играет
                                }
                            }
                        }
                    } else null

                    // Кнопка «список каналов» — закрываем плеер
                    val goBackToList: () -> Unit = {
                        detailStack.removeAt(detailStack.lastIndex)
                    }

                    PlayerScreen(
                        streamUrl = url,
                        title = route.title ?: "Плеер",
                        subtitle = route.subtitle,
                        epgCurrent = route.epgCurrentName,
                        epgNext = route.epgNextName,
                        currentChannelId = currentChannelId,
                        allChannels = playerChannels,
                        onSelectChannel = selectChannel,
                        onPrevChannel = goPrev,
                        onNextChannel = goNext,
                        onChannelListClick = null,
                        onBack = { detailStack.removeAt(detailStack.lastIndex) }
                    )
                }
            }
        }
        return
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(200.dp)) {

                // ===== Шапка профиля =====
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Профиль",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.width(10.dp))

                    Column {
                        Text(
                            text = user.title ?: "Пользователь",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "ID: ${user.id}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = user.bgbId?.let { "BGB: $it" } ?: "BGB: —",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                HorizontalDivider()

                Spacer(Modifier.height(4.dp))

                // ===== Пункты меню =====
                menuItems.forEachIndexed { index, (title, icon) ->
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = { Text(text = title, fontSize = 14.sp) },
                        selected = index == selectedIndex,
                        onClick = {
                            selectedIndex = index
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(
                            PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        )
                    )
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                BottomAppBar(
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(64.dp)
                ) {
                    IconButton(onClick = {
                        scope.launch {
                            if (drawerState.isClosed) drawerState.open()
                            else drawerState.close()
                        }
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Меню")
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { /* Фильтр */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        border = BorderStroke(1.dp, Color.White)
                    ) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Фильтр", fontSize = 13.sp)
                    }

                    Spacer(Modifier.width(6.dp))

                    Button(
                        onClick = { /* Сортировка */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        border = BorderStroke(1.dp, Color.White)
                    ) {
                        Icon(
                            Icons.Default.Sort,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Сортировка", fontSize = 13.sp)
                    }

                    IconButton(onClick = { /* Настройки */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Настройки")
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (selectedIndex) {
                    0 -> ChannelsScreen(
                        onChannelClick = { channelId ->
                            detailStack.add(DetailRoute(DetailType.CHANNEL, channelId.toLong()))
                        },
                        viewModel = channelsViewModel,
                        listState = channelsListState       // ← новый параметр
                    )
                    1 -> RadioScreen()
                    2 -> CamerasScreen()
                    3 -> MoviesScreen(
                        onMovieClick = { detailStack.add(DetailRoute(DetailType.MOVIE, it)) },
                        listState = moviesListState          // ← новый параметр
                    )
                    4 -> TvShowsScreen(
                        onShowClick = { detailStack.add(DetailRoute(DetailType.TV, it)) },
                        listState = tvShowsListState
                    )
                    5 -> CartoonsScreen(
                        onMovieClick = { detailStack.add(DetailRoute(DetailType.MOVIE, it)) },
                        listState = cartoonsListState
                    )
                    6 -> CartoonSerialsScreen(
                        onShowClick = { detailStack.add(DetailRoute(DetailType.TV, it)) },
                        listState = cartoonSerialsListState
                    )
                    7 -> ProfileScreen(user = user)
                    else -> Greeting(name = user.title ?: "Гость")
                }
            }
        }
    }
}

private val ISO_FORMAT = java.text.SimpleDateFormat(
    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
    java.util.Locale.US
).apply {
    timeZone = java.util.TimeZone.getTimeZone("UTC")
}

private val TIME_FORMAT = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())

private fun formatEpgTime(iso: String?): String {
    if (iso.isNullOrBlank()) return "—"
    return try {
        val date = ISO_FORMAT.parse(iso) ?: return "—"
        TIME_FORMAT.format(date)
    } catch (e: Exception) {
        "—"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelDetailScreen(
    channelId: Int,
    channelIndex: Int,            // ← новый параметр
    onBack: () -> Unit,
    onPlay: (
        url: String,
        title: String,
        subtitle: String?,
        epgCurrent: String?,
        epgNext: String?
    ) -> Unit,                    // ← расширили колбэк
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
    // Имя текущей передачи
    val epgCurrentName = channel.epgCurrent?.name

    // Имя следующей передачи (первая с is_future = true)
    val epgNextName = channel.epg
        .firstOrNull { it.isFuture == true }
        ?.name

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ===== Логотип + название =====
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

        // ===== Кнопка «Смотреть» =====
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

        // ===== Сейчас в эфире =====
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

        // ===== Программа передач =====
        if (channel.epg.isNotEmpty()) {
            item {
                Text(
                    text = "Программа передач",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Группируем: сначала будущие (включая текущую), потом прошедшие
            val upcoming = channel.epg.filter { it.isCurrent == true || it.isFuture == true }
            val past = channel.epg.filter { it.isPast == true }

            // Будущие/текущая
            items(upcoming, key = { it.id }) { program ->
                EpgProgramItem(program = program)
            }

            // Прошедшие — с заголовком «Ранее»
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

        // ===== Информация =====
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

        // ===== Ссылка на поток =====
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

@Composable
private fun EpgProgramItem(program: EpgProgram) {
    val isPast = program.isPast == true
    val isCurrent = program.isCurrent == true

    val contentAlpha = if (isPast) 0.5f else 1f
    val titleColor = when {
        isCurrent -> MaterialTheme.colorScheme.primary
        isPast -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${formatEpgTime(program.time)} – ${formatEpgTime(program.timeTo)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = titleColor.copy(alpha = contentAlpha)
                )
                Spacer(Modifier.weight(1f))
                program.duration?.let { mins ->
                    Text(
                        text = "${mins} мин",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = program.name ?: "Без названия",
                fontSize = 14.sp,
                fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                color = titleColor.copy(alpha = contentAlpha),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            program.descr?.takeIf { it.isNotBlank() }?.let { descr ->
                Spacer(Modifier.height(4.dp))
                Text(
                    text = descr,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ============================================================
//  Каналы (ТВ)
// ============================================================

@Composable
fun ChannelsScreen(
    onChannelClick: (Int) -> Unit,
    viewModel: ChannelsViewModel = viewModel(key = "channels"),
    listState: LazyListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadChannels()
    }

    when (val s = state) {
        is ChannelsState.Idle, ChannelsState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is ChannelsState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Ошибка: ${s.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        is ChannelsState.Success -> {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(s.channels) { channel ->
                    Card(
                        onClick = { onChannelClick(channel.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "${channel.number}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.width(28.dp)
                            )
                            AsyncImage(
                                model = "http://sp.pit.su/stalker_portal/misc/logos/original/${channel.logo}",
                                contentDescription = channel.name,
                                modifier = Modifier.size(40.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = channel.name ?: "Без названия",
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                channel.genreTitle?.let { genre ->
                                    Text(
                                        text = genre,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================
//  Фильмы
// ============================================================

@Composable
fun MoviesScreen(
    onMovieClick: (Long) -> Unit,
    viewModel: MoviesViewModel = viewModel(key = "movies"),
    listState: LazyListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadFirstPage()
    }

    when (val s = state) {
        is MoviesState.Idle, MoviesState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is MoviesState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Ошибка: ${s.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.retry() }) {
                        Text("Повторить")
                    }
                }
            }
        }

        is MoviesState.Success -> {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(s.movies, key = { it.id }) { movie ->
                    MovieCard(
                        movie = movie,
                        onClick = { onMovieClick(movie.id) }
                    )
                }

                item(key = "loader") {
                    LoadMoreFooter(
                        isLoadingMore = s.isLoadingMore,
                        hasMore = s.hasMore,
                        error = s.errorWhileLoadingMore,
                        endText = "Больше фильмов нет"
                    )

                    if (s.hasMore && !s.isLoadingMore && s.errorWhileLoadingMore == null) {
                        LaunchedEffect(s.movies.size) {
                            viewModel.loadNextPage()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MovieCard(movie: Movie, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            if (!movie.posterPath.isNullOrBlank()) {
                AsyncImage(
                    model = "http://inpulse.pit.su/api/image/w500${movie.posterPath}",
                    contentDescription = movie.title,
                    modifier = Modifier
                        .size(width = 70.dp, height = 105.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(width = 70.dp, height = 105.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Movie, contentDescription = null)
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title ?: "Без названия",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                movie.originalTitle?.takeIf { it != movie.title }?.let { original ->
                    Text(
                        text = original,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    movie.releaseDate?.take(4)?.let { year ->
                        Text(
                            text = year,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(8.dp))
                    }

                    movie.voteAverage?.let { rating ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = rating,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                movie.overview?.let { overview ->
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = overview,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun CartoonsScreen(
    onMovieClick: (Long) -> Unit,
    viewModel: MoviesViewModel = viewModel(
        key = "cartoons",
        factory = MoviesViewModelFactory(genreId = 16)
    ),
    listState: LazyListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadFirstPage()
    }

    when (val s = state) {
        is MoviesState.Idle, MoviesState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is MoviesState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Ошибка: ${s.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.retry() }) {
                        Text("Повторить")
                    }
                }
            }
        }

        is MoviesState.Success -> {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(s.movies, key = { it.id }) { movie ->
                    MovieCard(
                        movie = movie,
                        onClick = { onMovieClick(movie.id) }
                    )
                }

                item(key = "loader") {
                    LoadMoreFooter(
                        isLoadingMore = s.isLoadingMore,
                        hasMore = s.hasMore,
                        error = s.errorWhileLoadingMore,
                        endText = "Больше мультфильмов нет"
                    )

                    if (s.hasMore && !s.isLoadingMore && s.errorWhileLoadingMore == null) {
                        LaunchedEffect(s.movies.size) {
                            viewModel.loadNextPage()
                        }
                    }
                }
            }
        }
    }
}

// ============================================================
//  Сериалы (TV)
// ============================================================

@Composable
fun TvShowsScreen(
    onShowClick: (Long) -> Unit,
    viewModel: TvShowsViewModel = viewModel(key = "tvShows"),
    listState: LazyListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadFirstPage()
    }

    when (val s = state) {
        is TvShowsState.Idle, TvShowsState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is TvShowsState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Ошибка: ${s.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.retry() }) {
                        Text("Повторить")
                    }
                }
            }
        }

        is TvShowsState.Success -> {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(s.shows, key = { it.id }) { show ->
                    TvShowCard(
                        show = show,
                        onClick = { onShowClick(show.id) }
                    )
                }

                item(key = "loader") {
                    LoadMoreFooter(
                        isLoadingMore = s.isLoadingMore,
                        hasMore = s.hasMore,
                        error = s.errorWhileLoadingMore,
                        endText = "Больше сериалов нет"
                    )

                    if (s.hasMore && !s.isLoadingMore && s.errorWhileLoadingMore == null) {
                        LaunchedEffect(s.shows.size) {
                            viewModel.loadNextPage()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TvShowCard(show: TvShow, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            if (!show.posterPath.isNullOrBlank()) {
                AsyncImage(
                    model = "http://inpulse.pit.su/api/image/w200${show.posterPath}",
                    contentDescription = show.name,
                    modifier = Modifier
                        .size(width = 70.dp, height = 105.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(width = 70.dp, height = 105.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.LiveTv, contentDescription = null)
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = show.name ?: "Без названия",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                show.originalName?.takeIf { it != show.name }?.let { original ->
                    Text(
                        text = original,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    show.firstAirDate?.take(4)?.let { year ->
                        val lastYear = show.lastAirDate?.take(4)
                        val years = if (lastYear != null && lastYear != year) "$year – $lastYear" else year
                        Text(
                            text = years,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(8.dp))
                    }

                    show.voteAverage?.let { rating ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = rating,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                val seasons = show.numberOfSeasons
                val episodes = show.numberOfEpisodes
                if (seasons != null || episodes != null) {
                    Spacer(Modifier.height(2.dp))
                    val parts = buildList {
                        seasons?.let { add("$it сез.") }
                        episodes?.let { add("$it эп.") }
                    }
                    Text(
                        text = parts.joinToString(" • "),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                show.overview?.let { overview ->
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = overview,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}


@Composable
fun CartoonSerialsScreen(
    onShowClick: (Long) -> Unit,
    viewModel: TvShowsViewModel = viewModel(
        key = "cartoonSerials",
        factory = TvShowsViewModelFactory(genreId = 16)
    ),
    listState: LazyListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadFirstPage()
    }

    when (val s = state) {
        is TvShowsState.Idle, TvShowsState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is TvShowsState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Ошибка: ${s.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.retry() }) {
                        Text("Повторить")
                    }
                }
            }
        }

        is TvShowsState.Success -> {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(s.shows, key = { it.id }) { show ->
                    TvShowCard(
                        show = show,
                        onClick = { onShowClick(show.id) }
                    )
                }

                item(key = "loader") {
                    LoadMoreFooter(
                        isLoadingMore = s.isLoadingMore,
                        hasMore = s.hasMore,
                        error = s.errorWhileLoadingMore,
                        endText = "Больше мультсериалов нет"
                    )

                    if (s.hasMore && !s.isLoadingMore && s.errorWhileLoadingMore == null) {
                        LaunchedEffect(s.shows.size) {
                            viewModel.loadNextPage()
                        }
                    }
                }
            }
        }
    }
}

// ============================================================
//  Общий футер для пагинации
// ============================================================

@Composable
private fun LoadMoreFooter(
    isLoadingMore: Boolean,
    hasMore: Boolean,
    error: String?,
    endText: String = "Больше нет"
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoadingMore -> CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                strokeWidth = 3.dp
            )
            error != null -> Text(
                text = "Ошибка подгрузки: $error",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
            !hasMore -> Text(
                text = endText,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ============================================================
//  Детали фильма
// ============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    movieId: Long,
    onBack: () -> Unit,
    onSimilarClick: (Long) -> Unit,
    viewModel: MovieDetailViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(movieId) {
        viewModel.load(movieId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("О фильме", fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (val s = state) {
                is MovieDetailState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is MovieDetailState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Ошибка: ${s.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is MovieDetailState.Success -> {
                    MovieDetailContent(
                        movie = s.movie,
                        onSimilarClick = onSimilarClick
                    )
                }
            }
        }
    }
}

@Composable
private fun MovieDetailContent(
    movie: MovieDetail,
    onSimilarClick: (Long) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                if (!movie.backdropPath.isNullOrBlank()) {
                    AsyncImage(
                        model = "http://inpulse.pit.su/api/image/w780${movie.backdropPath}",
                        contentDescription = movie.title,
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

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                if (!movie.posterPath.isNullOrBlank()) {
                    AsyncImage(
                        model = "http://inpulse.pit.su/api/image/w300${movie.posterPath}",
                        contentDescription = movie.title,
                        modifier = Modifier
                            .size(width = 110.dp, height = 165.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(12.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = movie.title ?: "Без названия",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    movie.originalTitle?.takeIf { it != movie.title }?.let {
                        Text(
                            text = it,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    movie.tagline?.takeIf { it.isNotBlank() }?.let {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = it,
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        movie.releaseDate?.take(4)?.let { year ->
                            Text(year, fontSize = 13.sp)
                            Text(
                                "  •  ",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        movie.runtime?.let { mins ->
                            Text("${mins} мин", fontSize = 13.sp)
                            Text(
                                "  •  ",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        movie.voteAverage?.let { rating ->
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
                }
            }
        }

        if (movie.genres.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    movie.genres.forEach { genre ->
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

        movie.overview?.takeIf { it.isNotBlank() }?.let { overview ->
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

        val uniqueCast = movie.cast
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

        if (movie.similar.isNotEmpty()) {
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
                        items(movie.similar, key = { it.id }) { sim ->
                            SimilarMovieItem(
                                movie = sim,
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

@Composable
private fun SimilarMovieItem(
    movie: SimilarMovie,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(110.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        if (!movie.posterPath.isNullOrBlank()) {
            AsyncImage(
                model = "http://inpulse.pit.su/api/image/w200${movie.posterPath}",
                contentDescription = movie.title,
                modifier = Modifier
                    .size(width = 110.dp, height = 165.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(width = 110.dp, height = 165.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Movie, contentDescription = null)
            }
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = movie.title ?: "",
            fontSize = 12.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        movie.voteAverage?.let { rating ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(11.dp)
                )
                Spacer(Modifier.width(2.dp))
                Text(
                    text = rating,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CastItem(person: CastMember) {
    Column(
        modifier = Modifier.width(90.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!person.profilePath.isNullOrBlank()) {
            AsyncImage(
                model = "http://inpulse.pit.su/api/image/w185${person.profilePath}",
                contentDescription = person.name,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null)
            }
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = person.name ?: "",
            fontSize = 12.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        person.characterName?.takeIf { it.isNotBlank() }?.let {
            Text(
                text = it,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ============================================================
//  Заглушка для деталей сериала — сделаем следующим шагом
// ============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvShowDetailScreen(
    showId: Long,
    onBack: () -> Unit,
    onSimilarClick: (Long) -> Unit,
    viewModel: TvShowDetailViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(showId) {
        viewModel.load(showId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("О сериале", fontSize = 16.sp) },
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
                is TvShowDetailState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is TvShowDetailState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Ошибка: ${s.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is TvShowDetailState.Success -> {
                    TvShowDetailContent(
                        show = s.show,
                        onSimilarClick = onSimilarClick
                    )
                }
            }
        }
    }
}

@Composable
private fun TvShowDetailContent(
    show: TvShowDetail,
    onSimilarClick: (Long) -> Unit
) {
    // Какой сезон сейчас выбран (индекс в списке seasons)
    var selectedSeasonIndex by remember(show.id) { mutableStateOf(0) }

    LazyColumn(modifier = Modifier.fillMaxSize()) {

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

                    // Годы
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

                    // Сезоны • эпизоды • рейтинг
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

        // ===== Выбор сезона (чипы) =====
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
                                    text = season.name
                                        ?: "Сезон ${season.seasonNumber}",
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

        // ===== Список серий выбранного сезона =====
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

            items(
                currentSeason.episodes,
                key = { it.id }
            ) { episode ->
                EpisodeItem(episode = episode, onClick = {
                    // TODO: открыть плеер — сделаем позже
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

        // ===== Похожие сериалы =====
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

@Composable
private fun EpisodeItem(
    episode: Episode,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            // Превью серии
            Box(
                modifier = Modifier
                    .size(width = 110.dp, height = 65.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (!episode.stillPath.isNullOrBlank()) {
                    AsyncImage(
                        model = "http://inpulse.pit.su/api/image/w300${episode.stillPath}",
                        contentDescription = episode.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.LiveTv,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${episode.episodeNumber}. ${episode.name ?: "Без названия"}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    episode.airDate?.take(10)?.let { date ->
                        Text(
                            text = date,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    episode.runtime?.let { mins ->
                        Text(
                            text = "$mins мин",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                episode.overview?.takeIf { it.isNotBlank() }?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = it,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun SimilarTvShowItem(
    show: SimilarTvShow,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(110.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        if (!show.posterPath.isNullOrBlank()) {
            AsyncImage(
                model = "http://inpulse.pit.su/api/image/w200${show.posterPath}",
                contentDescription = show.name,
                modifier = Modifier
                    .size(width = 110.dp, height = 165.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(width = 110.dp, height = 165.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LiveTv, contentDescription = null)
            }
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = show.name ?: "",
            fontSize = 12.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        show.voteAverage?.let { rating ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(11.dp)
                )
                Spacer(Modifier.width(2.dp))
                Text(
                    text = rating,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ============================================================

// ============================================================
//  Заглушки для разделов в разработке
// ============================================================

@Composable
fun RadioScreen() {
    PlaceholderScreen(
        icon = Icons.Default.Radio,
        title = "Радио",
        subtitle = "Раздел в разработке"
    )
}

@Composable
fun CamerasScreen() {
    PlaceholderScreen(
        icon = Icons.Default.Videocam,
        title = "Камеры",
        subtitle = "Раздел в разработке"
    )
}

@Composable
fun ProfileScreen(user: User) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Профиль",
            modifier = Modifier.size(96.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = user.title ?: "Пользователь",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = user.email ?: "—",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        HorizontalDivider(modifier = Modifier.width(200.dp))

        Spacer(Modifier.height(16.dp))

        ProfileRow(label = "ID", value = user.id.toString())
        user.bgbId?.let { ProfileRow(label = "BGB ID", value = it) }
        user.balance?.let { ProfileRow(label = "Баланс", value = "$it ₽") }
        user.isAdmin?.let { admin ->
            ProfileRow(label = "Админ", value = if (admin == 1) "Да" else "Нет")
        }
        user.lastVisit?.let {
            ProfileRow(label = "Последний визит", value = it.take(10))
        }
    }
}

@Composable
private fun ProfileRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun PlaceholderScreen(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = subtitle,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Hello $name!")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KESKOAPPTheme {
        Greeting("Android")
    }
}