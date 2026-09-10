package com.companykesko.keskoapp.ui.tvshows

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.companykesko.keskoapp.ui.TvShowsState
import com.companykesko.keskoapp.ui.TvShowsViewModel
import com.companykesko.keskoapp.ui.common.LoadMoreFooter

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