package com.companykesko.keskoapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companykesko.keskoapp.data.ApiClient
import com.companykesko.keskoapp.data.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ChannelsState {
    data object Idle : ChannelsState
    data object Loading : ChannelsState
    data class Success(val channels: List<Channel>) : ChannelsState
    data class Error(val message: String) : ChannelsState
}

class ChannelsViewModel : ViewModel() {

    private val _state = MutableStateFlow<ChannelsState>(ChannelsState.Idle)
    val state: StateFlow<ChannelsState> = _state.asStateFlow()

    fun loadChannels() {
        viewModelScope.launch {
            _state.value = ChannelsState.Loading
            try {
                val response = ApiClient.service.getChannels()
                _state.value = ChannelsState.Success(response.channels)
            } catch (e: Exception) {
                _state.value = ChannelsState.Error(e.message ?: "Ошибка загрузки")
            }
        }
    }
}