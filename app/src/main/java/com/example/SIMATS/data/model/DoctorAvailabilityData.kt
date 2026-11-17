package com.example.SIMATS.data.model

data class DoctorAvailabilityResponseData(
    val doctorId: Int = 0,
    val doctorName: String = "",
    val availability: List<DoctorAvailabilitySlot> = emptyList(),
    val weeklyHours: List<DoctorAvailabilitySlot> = emptyList(),
    val totalSlots: Int = 0
)

data class DoctorAvailabilitySlot(
    val id: Int? = null,
    val doctorId: Int? = null,
    val weekday: String = "",
    val startTime: String = "09:00",
    val endTime: String = "17:00",
    val slotMinutes: Int = 30,
    val isActive: Boolean = true
)

data class SaveAvailabilityRequest(
    val weeklyHours: List<DoctorAvailabilitySlot>
)
