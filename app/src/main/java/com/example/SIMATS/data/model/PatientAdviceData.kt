package com.example.SIMATS.data.model

import com.google.gson.annotations.SerializedName

data class PatientAdviceItem(
    val id: Int = 0,
    @SerializedName("doctorId")
    val doctorId: Int = 0,
    @SerializedName("doctorName")
    val doctorName: String = "",
    val subject: String = "",
    @SerializedName("adviceText")
    val adviceText: String = "",
    @SerializedName("createdAt")
    val createdAt: String = ""
)
