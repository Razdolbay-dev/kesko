package com.companykesko.keskoapp.ui.common

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private val ISO_FORMAT = SimpleDateFormat(
    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
    Locale.US
).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

private val TIME_FORMAT = SimpleDateFormat("HH:mm", Locale.getDefault())

fun formatEpgTime(iso: String?): String {
    if (iso.isNullOrBlank()) return "—"
    return try {
        val date = ISO_FORMAT.parse(iso) ?: return "—"
        TIME_FORMAT.format(date)
    } catch (e: Exception) {
        "—"
    }
}