package com.example.SIMATS.data.model

data class MedicationReminderData(
    val dateText: String,
    val progressStatusText: String,
    val takenCount: Int = 0,
    val totalCount: Int = 0,
    val medications: List<MedicationItem> = emptyList(),
    val history: List<MedicationHistoryItem> = emptyList()
)

data class MedicationItem(
    val id: Int,
    val name: String,
    val dosage: String? = null,
    val frequency: String? = "Daily",
    val reminderTime: String? = "08:00",
    val instructions: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val doctorName: String? = null,
    val dosageAndTime: String,
    val doctorPrescriptionNote: String? = null,
    val isTaken: Boolean = false,
    val takenTimeNote: String? = null,
    val dueLaterText: String? = null,
    val isActive: Boolean = true
)

data class MedicationHistoryItem(
    val id: Int,
    val medicationId: Int,
    val medicationName: String,
    val dosageAndTime: String? = null,
    val action: String = "TAKEN",
    val actionTime: String = "",
    val formattedDate: String = "",
    val formattedTime: String = "",
    val notes: String? = null
)
