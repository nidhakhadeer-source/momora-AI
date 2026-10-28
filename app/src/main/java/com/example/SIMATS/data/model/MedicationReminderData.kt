package com.example.SIMATS.data.model

data class MedicationReminderData(
    val dateText: String,
    val progressStatusText: String,
    val medications: List<MedicationItem>
)

data class MedicationItem(
    val id: Int,
    val name: String,
    val dosageAndTime: String,
    val doctorPrescriptionNote: String? = null,
    val isTaken: Boolean,
    val takenTimeNote: String? = null,
    val dueLaterText: String? = null
)
