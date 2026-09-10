package com.companykesko.keskoapp.ui.movies

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
import com.companykesko.keskoapp.ui.MoviesState
import com.companykesko.keskoapp.ui.MoviesViewModel
import com.companykesko.keskoapp.ui.MoviesViewModelFactory
import com.companykesko.keskoapp.ui.common.LoadMoreFooter

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