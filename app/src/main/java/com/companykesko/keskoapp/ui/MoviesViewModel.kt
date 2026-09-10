package com.companykesko.keskoapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companykesko.keskoapp.data.ApiClient
import com.companykesko.keskoapp.data.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider

class MoviesViewModelFactory(
    private val genreId: Int?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MoviesViewModel(genreId) as T
    }
}

sealed interface MoviesState {
    data object Idle : MoviesState
    data object Loading : MoviesState
    data class Success(
        val movies: List<Movie>,
        val isLoadingMore: Boolean = false,
        val hasMore: Boolean = true,
        val errorWhileLoadingMore: String? = null
    ) : MoviesState
    data class Error(val message: String) : MoviesState
}

class MoviesViewModel(
    private val genreId: Int? = null
) : ViewModel() {

    private val _state = MutableStateFlow<MoviesState>(MoviesState.Idle)
    val state: StateFlow<MoviesState> = _state.asStateFlow()

    private var currentPage = 1
    private val perPage = 25
    private var isLoading = false
    private var totalPages = Int.MAX_VALUE
    private val loadedMovies = mutableListOf<Movie>()

    fun loadFirstPage() {
        if (_state.value is MoviesState.Success) return
        loadPage(reset = true)
    }

    fun loadNextPage() {
        val s = _state.value
        if (s !is MoviesState.Success) return
        if (!s.hasMore || s.isLoadingMore) return
        loadPage(reset = false)
    }

    fun retry() {
        val s = _state.value
        if (s is MoviesState.Success && s.errorWhileLoadingMore != null) {
            loadPage(reset = false)
        } else {
            loadPage(reset = true)
        }
    }

    private fun loadPage(reset: Boolean) {
        if (isLoading) return
        isLoading = true

        viewModelScope.launch {
            try {
                if (reset) {
                    currentPage = 1
                    loadedMovies.clear()
                    _state.value = MoviesState.Loading
                } else {
                    val cur = _state.value as? MoviesState.Success
                    if (cur != null) {
                        _state.value = cur.copy(
                            isLoadingMore = true,
                            errorWhileLoadingMore = null
                        )
                    }
                }

                val response = ApiClient.service.getMovies(
                    page = currentPage,
                    perPage = perPage,
                    genreId = genreId,
                    isPublished = 1
                )

                if (!response.success) {
                    throw Exception(response.message ?: "Ошибка сервера")
                }

                loadedMovies.addAll(response.data)

                val pagination = response.pagination
                totalPages = pagination?.totalPages ?: Int.MAX_VALUE
                val hasMore = currentPage < totalPages

                currentPage += 1

                _state.value = MoviesState.Success(
                    movies = loadedMovies.toList(),
                    isLoadingMore = false,
                    hasMore = hasMore,
                    errorWhileLoadingMore = null
                )
            } catch (e: Exception) {
                val cur = _state.value
                if (reset || cur !is MoviesState.Success) {
                    _state.value = MoviesState.Error(e.message ?: "Ошибка сети")
                } else {
                    _state.value = cur.copy(
                        isLoadingMore = false,
                        errorWhileLoadingMore = e.message ?: "Ошибка догрузки"
                    )
                }
            } finally {
                isLoading = false
            }
        }
    }
}