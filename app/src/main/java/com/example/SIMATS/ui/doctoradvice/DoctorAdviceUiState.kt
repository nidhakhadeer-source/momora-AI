package com.example.SIMATS.ui.doctoradvice

import com.example.SIMATS.data.model.DoctorAdviceData

data class DoctorAdviceUiState(
    val adviceData: DoctorAdviceData? = null,
    val subjectText: String = "Heartburn: gentle steps & follow-up",
    val adviceText: String = "Hi Maya, try smaller meals and stay upright after eating. Let's review this at your 29 Oct visit. Contact the clinic sooner if symptoms persist or worsen.\n\nFor severe chest or abdominal pain, seek urgent maternity care or local emergency services.",
    val isSaveCopyChecked: Boolean = true,
    val selectedNavIndex: Int = 3, // Advice tab active (index 3)
    val isSending: Boolean = false,
    val isSentSuccess: Boolean = false,
    val errorMessage: String? = null
)
