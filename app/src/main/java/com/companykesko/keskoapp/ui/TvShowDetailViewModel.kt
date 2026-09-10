package com.companykesko.keskoapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companykesko.keskoapp.data.ApiClient
import com.companykesko.keskoapp.data.TvShowDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface TvShowDetailState {
    data object Loading : TvShowDetailState
    data class Success(val show: TvShowDetail) : TvShowDetailState
    data class Error(val message: String) : TvShowDetailState
}

class TvShowDetailViewModel : ViewModel() {

    private val _state = MutableStateFlow<TvShowDetailState>(TvShowDetailState.Loading)
    val state: StateFlow<TvShowDetailState> = _state.asStateFlow()

    private var loadedId: Long? = null

    fun load(showId: Long) {
        if (loadedId == showId && _state.value is TvShowDetailState.Success) return
        loadedId = showId

        viewModelScope.launch {
            _state.value = TvShowDetailState.Loading
            try {
                val response = ApiClient.service.getTvShowDetail(showId)
                val show = response.data
                if (response.success && show != null) {
                    _state.value = TvShowDetailState.Success(show)
                } else {
                    _state.value = TvShowDetailState.Error(response.message ?: "Сериал не найден")
                }
            } catch (e: Exception) {
                _state.value = TvShowDetailState.Error(e.message ?: "Ошибка сети")
            }
        }
    }
}