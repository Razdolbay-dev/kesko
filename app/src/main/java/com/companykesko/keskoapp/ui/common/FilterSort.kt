package com.companykesko.keskoapp.ui.common

/**
 * Тип сортировки, применяемый к спискам.
 */
enum class SortOption(val label: String) {
    DEFAULT("По умолчанию"),
    NAME_ASC("Название: А → Я"),
    NAME_DESC("Название: Я → А")
}

/**
 * Описание фильтра, который можно применить к списку.
 *
 * @param title      заголовок в шторке («Жанр», «Категория», …)
 * @param options    список доступных значений + метка «Все»
 * @param selected   текущее выбранное значение (null = «Все»)
 */
data class FilterConfig(
    val title: String,
    val options: List<String>,
    val selected: String?
)

/**
 * Что умеет ViewModel, поддерживающая фильтр/сортировку.
 */
interface FilterSortable {
    fun setFilter(value: String?)
    fun setSort(option: SortOption)
    fun resetFilterSort()
    fun currentFilterOptions(): List<String>
    fun currentFilterValue(): String?
    fun currentSortOption(): SortOption
}