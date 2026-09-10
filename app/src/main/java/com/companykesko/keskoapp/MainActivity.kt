package com.companykesko.keskoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.companykesko.keskoapp.data.User
import com.companykesko.keskoapp.ui.AuthState
import com.companykesko.keskoapp.ui.AuthViewModel
import com.companykesko.keskoapp.ui.ChannelsState
import com.companykesko.keskoapp.ui.ChannelsViewModel
import com.companykesko.keskoapp.ui.theme.KESKOAPPTheme
import kotlinx.coroutines.launch

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

/**
 * Точка входа UI. Следит за состоянием авторизации.
 * - Loading → показываем прогресс
 * - Error   → показываем сообщение об ошибке
 * - Success → отдаём user в MainContent
 */
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

/**
 * Основной UI приложения. Показывается только после успешной авторизации.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(user: User) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
                // Показываем экран в зависимости от выбранного пункта меню
                when (selectedIndex) {
                    0 -> ChannelsScreen()               // ТВ
                    else -> Greeting(name = user.title ?: "Гость")
                }
            }
        }
    }
}

/**
 * Экран со списком каналов. Грузит их через ChannelsViewModel.
 */
@Composable
fun ChannelsScreen(viewModel: ChannelsViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Загружаем каналы один раз при первом показе экрана
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
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(s.channels) { channel ->
                    Card(
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