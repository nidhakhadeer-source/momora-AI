package com.example.SIMATS.data.model

import com.google.gson.annotations.SerializedName

data class PatientProfileData(
    @SerializedName("avatarInitials") val avatarInitials: String = "PT",
    @SerializedName("patientName") val patientName: String? = null,
    @SerializedName("patientId") val patientId: String? = null,
    @SerializedName("fullName") val fullName: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("dateOfBirth") val dateOfBirth: String = "",
    @SerializedName("gender") val gender: String? = null,
    @SerializedName("bloodGroup") val bloodGroup: String = "O positive",
    @SerializedName("address") val address: String? = null,
    @SerializedName("profilePhoto") val profilePhoto: String? = null,
    @SerializedName("photoUrl") val photoUrl: String? = null,
    @SerializedName("isCareTeamSharingOn") val isCareTeamSharingOn: Boolean = true,
    @SerializedName("careTeamDoctorName") val careTeamDoctorName: String? = null,
    @SerializedName("reminderSummary") val reminderSummary: String? = null
)
