package com.companykesko.keskoapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companykesko.keskoapp.data.ApiClient
import com.companykesko.keskoapp.data.ChannelDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ChannelDetailState {
    data object Loading : ChannelDetailState
    data class Success(val channel: ChannelDetail) : ChannelDetailState
    data class Error(val message: String) : ChannelDetailState
}

class ChannelDetailViewModel : ViewModel() {

    private val _state = MutableStateFlow<ChannelDetailState>(ChannelDetailState.Loading)
    val state: StateFlow<ChannelDetailState> = _state.asStateFlow()

    private var loadedId: Int? = null

    fun load(channelId: Int) {
        if (loadedId == channelId && _state.value is ChannelDetailState.Success) return
        loadedId = channelId

        viewModelScope.launch {
            _state.value = ChannelDetailState.Loading
            try {
                val response = ApiClient.service.getChannelDetail(channelId)
                val channel = response.channel
                if (response.success && channel != null) {
                    _state.value = ChannelDetailState.Success(channel)
                } else {
                    _state.value = ChannelDetailState.Error("Канал не найден")
                }
            } catch (e: Exception) {
                _state.value = ChannelDetailState.Error(e.message ?: "Ошибка сети")
            }
        }
    }
}