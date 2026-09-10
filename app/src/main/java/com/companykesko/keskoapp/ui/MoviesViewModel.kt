package com.companykesko.keskoapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.companykesko.keskoapp.data.ApiClient
import com.companykesko.keskoapp.data.Movie
import com.companykesko.keskoapp.data.MovieFilters
import com.companykesko.keskoapp.data.MovieSort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
    /** Если задан — жёстко прибиваем genre_id. Используется для Мультфильмов (genreId=16). */
    private val fixedGenreId: Int? = null
) : ViewModel() {

    private val _state = MutableStateFlow<MoviesState>(MoviesState.Idle)
    val state: StateFlow<MoviesState> = _state.asStateFlow()

    // Текущие фильтры и сортировка
    private val _filters = MutableStateFlow(
        MovieFilters(
            genreId = fixedGenreId,
            isPublished = 1
        )
    )
    val filters: StateFlow<MovieFilters> = _filters.asStateFlow()

    private val _sort = MutableStateFlow(MovieSort.POPULARITY_DESC)
    val sort: StateFlow<MovieSort> = _sort.asStateFlow()

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

    // ===== Обновление фильтров =====

    fun updateFilters(newFilters: MovieFilters) {
        // Прибиваем fixedGenreId и isPublished
        _filters.value = newFilters.copy(
            genreId = fixedGenreId ?: newFilters.genreId,
            isPublished = newFilters.isPublished ?: 1
        )
        reload()
    }

    fun updateSort(newSort: MovieSort) {
        _sort.value = newSort
        reload()
    }

    fun resetFilters() {
        _filters.value = MovieFilters(genreId = fixedGenreId, isPublished = 1)
        _sort.value = MovieSort.POPULARITY_DESC
        reload()
    }

    private fun reload() {
        // Сбрасываем и грузим заново
        currentPage = 1
        loadedMovies.clear()
        _state.value = MoviesState.Loading
        loadPage(reset = true, skipReset = true)
    }

    private fun loadPage(reset: Boolean, skipReset: Boolean = false) {
        if (isLoading) return
        isLoading = true

        viewModelScope.launch {
            try {
                if (reset && !skipReset) {
                    currentPage = 1
                    loadedMovies.clear()
                    _state.value = MoviesState.Loading
                } else if (!reset) {
                    val cur = _state.value as? MoviesState.Success
                    if (cur != null) {
                        _state.value = cur.copy(isLoadingMore = true, errorWhileLoadingMore = null)
                    }
                }

                val f = _filters.value
                val s = _sort.value

                val response = ApiClient.service.getMovies(
                    page = currentPage,
                    perPage = perPage,
                    title = f.title,
                    originalTitle = f.originalTitle,
                    genreId = f.genreId,
                    year = f.year,
                    yearFrom = f.yearFrom,
                    yearTo = f.yearTo,
                    voteMin = f.voteMin,
                    voteMax = f.voteMax,
                    isPublished = f.isPublished,
                    dataStatus = f.dataStatus,
                    originalLanguage = f.originalLanguage,
                    sort = s.apiValue
                )

                if (!response.success) throw Exception(response.message ?: "Ошибка сервера")

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

class MoviesViewModelFactory(
    private val genreId: Int?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MoviesViewModel(genreId) as T
    }
}