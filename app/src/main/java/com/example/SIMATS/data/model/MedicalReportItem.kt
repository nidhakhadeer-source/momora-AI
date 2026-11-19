package com.example.SIMATS.data.model

import com.google.gson.annotations.SerializedName

data class MedicalReportItem(
    @SerializedName(value = "id", alternate = ["report_id", "reportId"])
    val id: String = "",

    @SerializedName("title")
    val title: String = "",

    @SerializedName(value = "dateAndSizeText", alternate = ["date_and_size_text"])
    val dateAndSizeText: String = "",

    @SerializedName(value = "category", alternate = ["report_category"])
    val category: ReportCategory = ReportCategory.ALL,

    @SerializedName(value = "fileType", alternate = ["file_type"])
    val fileType: String = "PDF",

    @SerializedName(value = "originalFilename", alternate = ["original_filename"])
    val originalFilename: String? = null,

    @SerializedName(value = "fileSize", alternate = ["file_size"])
    val fileSize: Long? = null,

    @SerializedName(value = "fileSizeFormatted", alternate = ["file_size_formatted"])
    val fileSizeFormatted: String? = null,

    @SerializedName(value = "mimeType", alternate = ["mime_type"])
    val mimeType: String? = null,

    @SerializedName(value = "description", alternate = ["notes"])
    val description: String? = null,

    @SerializedName(value = "uploadDate", alternate = ["upload_date", "created_at", "reportDate", "report_date"])
    val uploadDate: String? = null,

    @SerializedName(value = "hasFile", alternate = ["has_file"])
    val hasFile: Boolean = true,

    @SerializedName(value = "downloadUrl", alternate = ["download_url", "file_url"])
    val downloadUrl: String? = null
) {
    val displaySize: String
        get() = fileSizeFormatted
            ?: if (fileSize != null && fileSize > 0) formatBytes(fileSize)
            else if (dateAndSizeText.isNotBlank()) {
                val parts = dateAndSizeText.split("·", "??")
                if (parts.size >= 3) parts[2].trim() else ""
            } else ""

    val displayDate: String
        get() = uploadDate?.takeIf { it.isNotBlank() }
            ?: if (dateAndSizeText.isNotBlank()) {
                dateAndSizeText.split("·", "??").firstOrNull()?.trim() ?: ""
            } else ""

    val displaySubtitle: String
        get() {
            if (dateAndSizeText.isNotBlank()) {
                return dateAndSizeText.replace("??", " · ")
            }
            val parts = mutableListOf<String>()
            if (displayDate.isNotBlank()) parts.add(displayDate)
            if (fileType.isNotBlank()) parts.add(fileType.uppercase())
            if (displaySize.isNotBlank()) parts.add(displaySize)
            return if (parts.isNotEmpty()) parts.joinToString(" · ") else "Medical report"
        }

    companion object {
        fun formatBytes(bytes: Long): String {
            if (bytes <= 0) return "0 B"
            val units = arrayOf("B", "KB", "MB", "GB")
            var size = bytes.toDouble()
            var unitIndex = 0
            while (size >= 1024 && unitIndex < units.size - 1) {
                size /= 1024
                unitIndex++
            }
            return String.format(java.util.Locale.US, "%.1f %s", size, units[unitIndex])
        }
    }
}

enum class ReportCategory {
    @SerializedName(value = "ALL", alternate = ["All", "all", "ALL REPORTS", "All reports"])
    ALL,

    @SerializedName(value = "ULTRASOUND", alternate = ["Ultrasound", "ultrasound", "Ultrasound Scan", "ULTRASOUND SCAN"])
    ULTRASOUND,

    @SerializedName(value = "LAB_RESULTS", alternate = ["Lab results", "lab_results", "Lab Results", "LAB", "lab"])
    LAB_RESULTS
}
