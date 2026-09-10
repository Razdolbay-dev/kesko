package com.companykesko.keskoapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

object TokenStore {

    private val TOKEN_KEY = stringPreferencesKey("auth_token")

    // Сохранить токен
    suspend fun save(context: Context, token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    // Получить токен (Flow — реактивно)
    fun observe(context: Context): Flow<String?> =
        context.dataStore.data.map { it[TOKEN_KEY] }

    // Получить токен один раз
    suspend fun get(context: Context): String? =
        context.dataStore.data.map { it[TOKEN_KEY] }.first()

    // Очистить (например, при logout)
    suspend fun clear(context: Context) {
        context.dataStore.edit { it.remove(TOKEN_KEY) }
    }
}