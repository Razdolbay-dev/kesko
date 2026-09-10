package com.companykesko.keskoapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companykesko.keskoapp.data.ApiClient
import com.companykesko.keskoapp.data.Channel
import com.companykesko.keskoapp.ui.common.SortOption
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

    private var rawChannels: List<Channel> = emptyList()

    private val _filterOptions = MutableStateFlow<List<String>>(emptyList())
    val filterOptions: StateFlow<List<String>> = _filterOptions.asStateFlow()

    private val _filterValue = MutableStateFlow<String?>(null)
    val filterValue: StateFlow<String?> = _filterValue.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.DEFAULT)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    fun loadChannels() {
        if (_state.value is ChannelsState.Success) return

        viewModelScope.launch {
            _state.value = ChannelsState.Loading
            try {
                val response = ApiClient.service.getChannels()
                rawChannels = response.channels
                _filterOptions.value = response.channels
                    .mapNotNull { it.genreTitle?.takeIf { g -> g.isNotBlank() } }
                    .distinct()
                    .sorted()
                applyFilterSort()
            } catch (e: Exception) {
                _state.value = ChannelsState.Error(e.message ?: "Ошибка загрузки")
            }
        }
    }

    fun setFilter(value: String?) {
        _filterValue.value = value
        applyFilterSort()
    }

    fun setSort(option: SortOption) {
        _sortOption.value = option
        applyFilterSort()
    }

    fun resetFilterSort() {
        _filterValue.value = null
        _sortOption.value = SortOption.DEFAULT
        applyFilterSort()
    }

    private fun applyFilterSort() {
        val filter = _filterValue.value
        val sort = _sortOption.value

        val filtered = if (filter.isNullOrBlank()) rawChannels
        else rawChannels.filter { it.genreTitle == filter }

        val sorted = when (sort) {
            SortOption.DEFAULT -> filtered.sortedBy { it.number }
            SortOption.NAME_ASC -> filtered.sortedBy { it.name?.lowercase() ?: "" }
            SortOption.NAME_DESC -> filtered.sortedByDescending { it.name?.lowercase() ?: "" }
        }

        _state.value = ChannelsState.Success(sorted)
    }
}