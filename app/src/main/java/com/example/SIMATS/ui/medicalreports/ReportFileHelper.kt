package com.example.SIMATS.ui.medicalreports

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

object ReportFileHelper {

    fun resolveMimeType(fileName: String, fileType: String? = null, mimeType: String? = null): String {
        if (!mimeType.isNullOrBlank() && mimeType != "application/octet-stream") {
            return mimeType
        }
        val lower = fileName.lowercase()
        return when {
            lower.endsWith(".pdf") || fileType?.equals("pdf", ignoreCase = true) == true -> "application/pdf"
            lower.endsWith(".jpg") || lower.endsWith(".jpeg") || fileType?.equals("jpg", ignoreCase = true) == true || fileType?.equals("jpeg", ignoreCase = true) == true -> "image/jpeg"
            lower.endsWith(".png") || fileType?.equals("png", ignoreCase = true) == true -> "image/png"
            else -> "application/pdf"
        }
    }

    fun openFile(context: Context, file: File, mimeType: String): Boolean {
        return try {
            val authority = "${context.packageName}.fileprovider"
            val uri = FileProvider.getUriForFile(context, authority, file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No app found to open this document", Toast.LENGTH_LONG).show()
            false
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to open document: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            false
        }
    }

    fun shareFile(context: Context, file: File, mimeType: String, title: String): Boolean {
        return try {
            val authority = "${context.packageName}.fileprovider"
            val uri = FileProvider.getUriForFile(context, authority, file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(intent, "Share $title").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
            true
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to share document: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            false
        }
    }

    fun getFileName(context: Context, uri: Uri): String {
        var name = "medical_report"
        if (uri.scheme == "content") {
            try {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1 && cursor.moveToFirst()) {
                        val displayName = cursor.getString(nameIndex)
                        if (!displayName.isNullOrBlank()) {
                            name = displayName
                        }
                    }
                }
            } catch (_: Exception) {}
        } else if (uri.scheme == "file") {
            uri.lastPathSegment?.let { name = it }
        }
        return name
    }

    fun getFileSize(context: Context, uri: Uri): Long {
        var size: Long = 0L
        if (uri.scheme == "content") {
            try {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex != -1 && cursor.moveToFirst()) {
                        size = cursor.getLong(sizeIndex)
                    }
                }
            } catch (_: Exception) {}
        }
        return size
    }
}
