package com.companykesko.keskoapp.ui.tvshows

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.companykesko.keskoapp.ui.TvShowDetailState
import com.companykesko.keskoapp.ui.TvShowDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvShowDetailScreen(
    showId: Long,
    onBack: () -> Unit,
    onSimilarClick: (Long) -> Unit,
    listState: LazyListState,
    viewModel: TvShowDetailViewModel = viewModel(key = "tvShowDetail_$showId")
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
                        listState = listState,
                        onSimilarClick = onSimilarClick
                    )
                }
            }
        }
    }
}