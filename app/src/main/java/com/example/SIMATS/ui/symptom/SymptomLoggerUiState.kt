package com.example.SIMATS.ui.symptom

import com.example.SIMATS.data.model.SymptomEntryData
import com.example.SIMATS.data.model.SymptomSeverity

data class SymptomLoggerUiState(
    val entryData: SymptomEntryData? = null,
    val selectedSymptom: String = "Heartburn after meals",
    val selectedSeverity: SymptomSeverity = SymptomSeverity.MILD,
    val startedText: String = "26 Oct, 13:00",
    val durationText: String = "About 20 min",
    val isDropdownExpanded: Boolean = false,
    val selectedNavIndex: Int = 2,
    val isLoading: Boolean = false,
    val isSavedSuccess: Boolean = false,
    val errorMessage: String? = null
)
