package com.companykesko.keskoapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.companykesko.keskoapp.data.ApiClient
import com.companykesko.keskoapp.data.TvShow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface TvShowsState {
    data object Idle : TvShowsState
    data object Loading : TvShowsState
    data class Success(
        val shows: List<TvShow>,
        val isLoadingMore: Boolean = false,
        val hasMore: Boolean = true,
        val errorWhileLoadingMore: String? = null
    ) : TvShowsState
    data class Error(val message: String) : TvShowsState
}

class TvShowsViewModel(
    private val genreId: Int? = null
) : ViewModel() {

    private val _state = MutableStateFlow<TvShowsState>(TvShowsState.Idle)
    val state: StateFlow<TvShowsState> = _state.asStateFlow()

    private var currentPage = 1
    private val perPage = 25
    private var isLoading = false
    private var totalPages = Int.MAX_VALUE
    private val loadedShows = mutableListOf<TvShow>()

    fun loadFirstPage() {
        if (_state.value is TvShowsState.Success) return
        loadPage(reset = true)
    }

    fun loadNextPage() {
        val s = _state.value
        if (s !is TvShowsState.Success) return
        if (!s.hasMore || s.isLoadingMore) return
        loadPage(reset = false)
    }

    fun retry() {
        val s = _state.value
        if (s is TvShowsState.Success && s.errorWhileLoadingMore != null) {
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
                    loadedShows.clear()
                    _state.value = TvShowsState.Loading
                } else {
                    val cur = _state.value as? TvShowsState.Success
                    if (cur != null) {
                        _state.value = cur.copy(
                            isLoadingMore = true,
                            errorWhileLoadingMore = null
                        )
                    }
                }

                val response = ApiClient.service.getTvShows(
                    page = currentPage,
                    perPage = perPage,
                    genreId = genreId,
                    isPublished = 1
                )

                if (!response.success) {
                    throw Exception(response.message ?: "Ошибка сервера")
                }

                loadedShows.addAll(response.data)

                val pagination = response.pagination
                totalPages = pagination?.totalPages ?: Int.MAX_VALUE
                val hasMore = currentPage < totalPages

                currentPage += 1

                _state.value = TvShowsState.Success(
                    shows = loadedShows.toList(),
                    isLoadingMore = false,
                    hasMore = hasMore,
                    errorWhileLoadingMore = null
                )
            } catch (e: Exception) {
                val cur = _state.value
                if (reset || cur !is TvShowsState.Success) {
                    _state.value = TvShowsState.Error(e.message ?: "Ошибка сети")
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

class TvShowsViewModelFactory(
    private val genreId: Int?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TvShowsViewModel(genreId) as T
    }
}