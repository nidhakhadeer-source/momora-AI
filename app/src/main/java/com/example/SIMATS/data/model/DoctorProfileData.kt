package com.example.SIMATS.data.model

import com.google.gson.annotations.SerializedName

data class DoctorProfileData(
    @SerializedName("fullName") val fullName: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("specialty") val specialty: String = "Obstetrics",
    @SerializedName("licenseId") val licenseId: String = "",
    @SerializedName("clinic") val clinic: String = "Willow Maternity Clinic",
    @SerializedName("gender") val gender: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("address") val address: String? = null,
    @SerializedName("qualification") val qualification: String? = null,
    @SerializedName("experienceYears") val experienceYears: Int? = null,
    @SerializedName("photoUrl") val photoUrl: String? = null
)

