package com.companykesko.keskoapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.companykesko.keskoapp.data.ApiClient
import com.companykesko.keskoapp.data.TvShow
import com.companykesko.keskoapp.data.TvShowFilters
import com.companykesko.keskoapp.data.TvShowSort
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
    private val fixedGenreId: Int? = null
) : ViewModel() {

    private val _state = MutableStateFlow<TvShowsState>(TvShowsState.Idle)
    val state: StateFlow<TvShowsState> = _state.asStateFlow()

    private val _filters = MutableStateFlow(
        TvShowFilters(genreId = fixedGenreId, isPublished = 1)
    )
    val filters: StateFlow<TvShowFilters> = _filters.asStateFlow()

    private val _sort = MutableStateFlow(TvShowSort.POPULARITY_DESC)
    val sort: StateFlow<TvShowSort> = _sort.asStateFlow()

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

    fun updateFilters(newFilters: TvShowFilters) {
        _filters.value = newFilters.copy(
            genreId = fixedGenreId ?: newFilters.genreId,
            // isPublished = newFilters.isPublished ?: 1
        )
        reload()
    }

    fun updateSort(newSort: TvShowSort) {
        _sort.value = newSort
        reload()
    }

    fun resetFilters() {
        _filters.value = TvShowFilters(genreId = fixedGenreId, isPublished = 1)
        _sort.value = TvShowSort.POPULARITY_DESC
        reload()
    }

    private fun reload() {
        currentPage = 1
        loadedShows.clear()
        _state.value = TvShowsState.Loading
        loadPage(reset = true, skipReset = true)
    }

    companion object {
        private const val PUBLISHED_ONLY = 1
        private const val PER_PAGE = 25
    }

    private fun loadPage(reset: Boolean, skipReset: Boolean = false) {
        if (isLoading) return
        isLoading = true

        viewModelScope.launch {
            try {
                if (reset && !skipReset) {
                    currentPage = 1
                    loadedShows.clear()
                    _state.value = TvShowsState.Loading
                } else if (!reset) {
                    val cur = _state.value as? TvShowsState.Success
                    if (cur != null) {
                        _state.value = cur.copy(isLoadingMore = true, errorWhileLoadingMore = null)
                    }
                }

                val f = _filters.value
                val s = _sort.value

                val response = ApiClient.service.getTvShows(
                    page = currentPage,
                    perPage = perPage,
                    name = f.name,
                    originalName = f.originalName,
                    genreId = f.genreId,
                    year = f.year,
                    yearFrom = f.yearFrom,
                    yearTo = f.yearTo,
                    voteMin = f.voteMin,
                    voteMax = f.voteMax,
                    status = f.status,
                    isPublished = PUBLISHED_ONLY,
                    dataStatus = f.dataStatus,
                    originalLanguage = f.originalLanguage,
                    hasSeasons = f.hasSeasons,
                    sort = s.apiValue
                )

                if (!response.success) throw Exception(response.message ?: "Ошибка сервера")

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