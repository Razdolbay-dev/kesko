package com.companykesko.keskoapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companykesko.keskoapp.data.ApiClient
import com.companykesko.keskoapp.data.MovieDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface MovieDetailState {
    data object Loading : MovieDetailState
    data class Success(val movie: MovieDetail) : MovieDetailState
    data class Error(val message: String) : MovieDetailState
}

class MovieDetailViewModel : ViewModel() {

    private val _state = MutableStateFlow<MovieDetailState>(MovieDetailState.Loading)
    val state: StateFlow<MovieDetailState> = _state.asStateFlow()

    private var loadedId: Long? = null

    fun load(movieId: Long) {
        if (loadedId == movieId && _state.value is MovieDetailState.Success) return
        loadedId = movieId

        viewModelScope.launch {
            _state.value = MovieDetailState.Loading
            try {
                val response = ApiClient.service.getMovieDetail(movieId)
                val movie = response.data
                if (response.success && movie != null) {
                    _state.value = MovieDetailState.Success(movie)
                } else {
                    _state.value = MovieDetailState.Error(response.message ?: "Фильм не найден")
                }
            } catch (e: Exception) {
                _state.value = MovieDetailState.Error(e.message ?: "Ошибка сети")
            }
        }
    }
}