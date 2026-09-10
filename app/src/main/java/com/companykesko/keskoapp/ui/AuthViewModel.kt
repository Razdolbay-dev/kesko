package com.companykesko.keskoapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companykesko.keskoapp.data.ApiClient
import com.companykesko.keskoapp.data.LoginRequest
import com.companykesko.keskoapp.data.TokenStore
import com.companykesko.keskoapp.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthState {
    data object Idle : AuthState
    data object Loading : AuthState
    data class Success(val user: User, val token: String) : AuthState
    data class Error(val message: String) : AuthState
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    // Токен в памяти — потом можно сохранить в DataStore
    var token: String? = null
        private set

    init {
        autoLogin()
    }

    private fun autoLogin() {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                // 1. Инициализируем ApiClient (подтянет токен из DataStore)
                ApiClient.init(getApplication())

                // 2. Если токен уже есть — пробуем с ним сразу
                //    (на реальном API надо бы проверять срок жизни, но пока просто пробуем)
                val saved = TokenStore.get(getApplication())

                val email = "chulkov@netonline.ru"
                val password = "secure123"

                // 3. Логинимся (если сервер вернёт 401 — сохраним новый токен)
                val response = ApiClient.service.login(LoginRequest(email, password))
                val user = response.user
                val token = response.token

                if (user != null && token != null) {
                    // Перезаписываем токен — старый мог быть просрочен
                    TokenStore.save(getApplication(), token)
                    ApiClient.setToken(token)
                    _state.value = AuthState.Success(user, token)
                } else {
                    _state.value = AuthState.Error(response.message ?: "Пустой ответ")
                }
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.message ?: "Ошибка сети")
            }
        }
    }
}