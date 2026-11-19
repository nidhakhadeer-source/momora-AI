package com.example.SIMATS.data.model

import com.google.gson.annotations.SerializedName

data class PatientMedicalHistoryData(
    val patientName: String,
    val patientInitials: String,
    val gestationalAge: String,
    val dueDateText: String,
    val patientIdText: String,
    val clinicalBackgroundText: String,
    val patientConfirmedDateText: String? = null,
    val medications: List<PatientMedicationItem> = emptyList(),
    val reports: List<PatientReportItem> = emptyList(),
    val visits: List<PatientVisitItem> = emptyList(),
    val recentCareRecordTitle: String? = null,
    val recentCareRecordDetails: String? = null
)

data class PatientReportItem(
    @SerializedName(value = "id", alternate = ["report_id", "reportId"])
    val id: String = "",

    @SerializedName("title")
    val title: String = "",

    @SerializedName(value = "reportDate", alternate = ["report_date", "created_at", "upload_date"])
    val reportDate: String? = null,

    @SerializedName("category")
    val category: String? = null,

    @SerializedName(value = "size", alternate = ["file_size_formatted", "fileSizeFormatted"])
    val size: String? = null,

    @SerializedName(value = "fileType", alternate = ["file_type"])
    val fileType: String? = null,

    @SerializedName(value = "originalFilename", alternate = ["original_filename"])
    val originalFilename: String? = null,

    @SerializedName(value = "fileSize", alternate = ["file_size"])
    val fileSize: Long? = null,

    @SerializedName(value = "hasFile", alternate = ["has_file"])
    val hasFile: Boolean = true,

    @SerializedName(value = "downloadUrl", alternate = ["download_url"])
    val downloadUrl: String? = null,

    @SerializedName(value = "mimeType", alternate = ["mime_type"])
    val mimeType: String? = null,

    @SerializedName(value = "description", alternate = ["notes"])
    val description: String? = null
)

data class PatientVisitItem(
    val id: String,
    val date: String? = null,
    val time: String? = null,
    val status: String? = null,
    val notes: String? = null
)

data class PatientMedicationItem(
    val id: String,
    val name: String,
    val dosageDetails: String,
    val isTakenToday: Boolean
)
