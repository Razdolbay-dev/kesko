package com.companykesko.keskoapp.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.companykesko.keskoapp.data.Genre

data class MediaFilterValues(
    val year: Int? = null,
    val yearFrom: Int? = null,
    val yearTo: Int? = null,
    val voteMin: Float? = null,
    val genreId: Int? = null,
    val originalLanguage: String? = null,
    val status: String? = null,
    val hasSeasons: Boolean? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaFilterSheet(
    title: String,
    initial: MediaFilterValues,
    genres: List<Genre>,
    showGenreField: Boolean = true,
    showStatusField: Boolean = false,       // true для сериалов
    showHasSeasonsField: Boolean = false,   // true для сериалов
    onApply: (MediaFilterValues) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var year by remember { mutableStateOf(initial.year?.toString() ?: "") }
    var yearFrom by remember { mutableStateOf(initial.yearFrom?.toString() ?: "") }
    var yearTo by remember { mutableStateOf(initial.yearTo?.toString() ?: "") }
    var voteMin by remember { mutableStateOf(initial.voteMin?.toString() ?: "") }
    var genreId by remember { mutableStateOf(initial.genreId) }
    var language by remember { mutableStateOf(initial.originalLanguage) }
    var status by remember { mutableStateOf(initial.status) }
    var hasSeasons by remember { mutableStateOf(initial.hasSeasons) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onReset) { Text("Сбросить") }
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))

            // Год
            OutlinedTextField(
                value = year,
                onValueChange = { year = it.filter { c -> c.isDigit() }.take(4) },
                label = { Text("Год") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            // Год от / до
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = yearFrom,
                    onValueChange = { yearFrom = it.filter { c -> c.isDigit() }.take(4) },
                    label = { Text("Год от") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = yearTo,
                    onValueChange = { yearTo = it.filter { c -> c.isDigit() }.take(4) },
                    label = { Text("Год до") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))

            // Рейтинг
            OutlinedTextField(
                value = voteMin,
                onValueChange = { voteMin = it.filter { c -> c.isDigit() || c == '.' }.take(4) },
                label = { Text("Рейтинг от") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            // Жанр
            if (showGenreField) {
                GenreDropdown(genres = genres, selectedId = genreId, onSelect = { genreId = it })
                Spacer(Modifier.height(8.dp))
            }

            // Язык
            LanguageDropdown(selected = language, onSelect = { language = it })
            Spacer(Modifier.height(8.dp))

            // Статус (только для сериалов)
            if (showStatusField) {
                StatusDropdown(selected = status, onSelect = { status = it })
                Spacer(Modifier.height(8.dp))
            }

            // Наличие сезонов (только для сериалов)
            if (showHasSeasonsField) {
                Text(
                    text = "Сезоны",
                    fontSize = 13.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = hasSeasons == null,
                        onClick = { hasSeasons = null },
                        label = { Text("Любой") }
                    )
                    FilterChip(
                        selected = hasSeasons == true,
                        onClick = { hasSeasons = true },
                        label = { Text("С сезонами") }
                    )
                    FilterChip(
                        selected = hasSeasons == false,
                        onClick = { hasSeasons = false },
                        label = { Text("Без сезонов") }
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) { Text("Отмена") }

                Button(
                    onClick = {
                        onApply(
                            MediaFilterValues(
                                year = year.toIntOrNull(),
                                yearFrom = yearFrom.toIntOrNull(),
                                yearTo = yearTo.toIntOrNull(),
                                voteMin = voteMin.toFloatOrNull(),
                                genreId = genreId,
                                originalLanguage = language,
                                status = status,
                                hasSeasons = hasSeasons
                            )
                        )
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Применить") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenreDropdown(
    genres: List<Genre>,
    selectedId: Int?,
    onSelect: (Int?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val label = genres.firstOrNull { it.id == selectedId }?.name ?: "Любой"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = label,
            onValueChange = {},
            readOnly = true,
            label = { Text("Жанр") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Любой") },
                onClick = { onSelect(null); expanded = false }
            )
            genres.forEach { genre ->
                DropdownMenuItem(
                    text = { Text(genre.name ?: "Без названия") },
                    onClick = { onSelect(genre.id); expanded = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageDropdown(
    selected: String?,
    onSelect: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val languages = listOf(
        "en" to "Английский",
        "ru" to "Русский",
        "ja" to "Японский",
        "ko" to "Корейский",
        "fr" to "Французский",
        "de" to "Немецкий",
        "es" to "Испанский",
        "it" to "Итальянский",
        "zh" to "Китайский"
    )
    val label = languages.firstOrNull { it.first == selected }?.second ?: "Любой"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = label,
            onValueChange = {},
            readOnly = true,
            label = { Text("Язык") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Любой") },
                onClick = { onSelect(null); expanded = false }
            )
            languages.forEach { (code, name) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = { onSelect(code); expanded = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatusDropdown(
    selected: String?,
    onSelect: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val statuses = listOf(
        "Returning Series" to "Продолжается",
        "Ended" to "Завершён",
        "Canceled" to "Отменён",
        "In Production" to "В производстве",
        "Planned" to "Запланирован",
        "Pilot" to "Пилот"
    )
    val label = statuses.firstOrNull { it.first == selected }?.second ?: "Любой"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = label,
            onValueChange = {},
            readOnly = true,
            label = { Text("Статус") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Любой") },
                onClick = { onSelect(null); expanded = false }
            )
            statuses.forEach { (code, name) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = { onSelect(code); expanded = false }
                )
            }
        }
    }
}