package com.companykesko.keskoapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companykesko.keskoapp.data.ApiClient
import com.companykesko.keskoapp.data.Genre
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GenresViewModel : ViewModel() {

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres.asStateFlow()

    private var loaded = false

    fun loadIfNeeded() {
        if (loaded) return
        loaded = true
        viewModelScope.launch {
            try {
                _genres.value = ApiClient.service.getGenres()
            } catch (_: Exception) {
                // Тихо игнорируем — покажем пустой список
            }
        }
    }
}