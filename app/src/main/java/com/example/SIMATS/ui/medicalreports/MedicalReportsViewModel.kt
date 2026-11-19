package com.example.SIMATS.ui.medicalreports

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.model.MedicalReportItem
import com.example.SIMATS.data.model.ReportCategory
import com.example.SIMATS.data.repository.MedicalReportsRepository
import com.example.SIMATS.data.repository.MedicalReportsRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.Locale

class MedicalReportsViewModel(
    private val repository: MedicalReportsRepository = MedicalReportsRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicalReportsUiState())
    val uiState: StateFlow<MedicalReportsUiState> = _uiState.asStateFlow()

    init {
        loadReports()
    }

    fun onEvent(event: MedicalReportsUiEvent) {
        when (event) {
            is MedicalReportsUiEvent.OnCategorySelected -> {
                _uiState.value = _uiState.value.copy(selectedCategory = event.category)
            }
            is MedicalReportsUiEvent.OnReportCardClicked -> {
                _uiState.value = _uiState.value.copy(
                    selectedReportForDetails = event.report,
                    showReportDetailSheet = true
                )
            }
            is MedicalReportsUiEvent.OnViewReportClicked -> {
                val report = _uiState.value.reports.find { it.id == event.reportId }
                if (report != null) {
                    _uiState.value = _uiState.value.copy(
                        selectedReportForDetails = report,
                        showReportDetailSheet = true
                    )
                }
            }
            is MedicalReportsUiEvent.OnOpenReport -> {
                openReport(event.report, event.context, isShare = false)
            }
            is MedicalReportsUiEvent.OnShareReport -> {
                openReport(event.report, event.context, isShare = true)
            }
            is MedicalReportsUiEvent.OnFilePicked -> {
                handleFilePicked(event.uri, event.context)
            }
            is MedicalReportsUiEvent.OnUploadTitleChanged -> {
                _uiState.value = _uiState.value.copy(
                    uploadTitle = event.title,
                    uploadErrorMessage = null
                )
            }
            is MedicalReportsUiEvent.OnUploadCategoryChanged -> {
                _uiState.value = _uiState.value.copy(uploadCategory = event.category)
            }
            is MedicalReportsUiEvent.OnUploadDescriptionChanged -> {
                _uiState.value = _uiState.value.copy(uploadDescription = event.description)
            }
            is MedicalReportsUiEvent.OnConfirmUpload -> {
                uploadSelectedReport(event.context)
            }
            MedicalReportsUiEvent.OnDismissUploadDialog -> {
                _uiState.value = _uiState.value.copy(
                    showUploadDialog = false,
                    selectedFileUri = null,
                    uploadErrorMessage = null
                )
            }
            MedicalReportsUiEvent.OnDismissReportDetailSheet -> {
                _uiState.value = _uiState.value.copy(
                    showReportDetailSheet = false,
                    selectedReportForDetails = null
                )
            }
            MedicalReportsUiEvent.OnDismissMessage -> {
                _uiState.value = _uiState.value.copy(
                    errorMessage = null,
                    successMessage = null
                )
            }
            MedicalReportsUiEvent.OnChooseFileClicked -> {
                // Handled in UI by launching the system document picker
            }
            MedicalReportsUiEvent.OnRetryClicked -> {
                loadReports(isRefresh = false)
            }
            MedicalReportsUiEvent.OnRefresh -> {
                refreshReports()
            }
            is MedicalReportsUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            MedicalReportsUiEvent.OnBackClicked -> {}
            MedicalReportsUiEvent.OnNotificationClicked -> {}
        }
    }

    fun loadReports(isRefresh: Boolean = false) {
        // Prevent multiple simultaneous refresh/load requests
        if (_uiState.value.isRefreshing || (_uiState.value.isLoading && !isRefresh)) {
            return
        }

        if (isRefresh) {
            _uiState.value = _uiState.value.copy(isRefreshing = true, errorMessage = null)
        } else {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        }

        viewModelScope.launch {
            repository.getReports().collect { result ->
                result.fold(
                    onSuccess = { list ->
                        _uiState.value = _uiState.value.copy(
                            reports = list,
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = null
                        )
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            // Preserve existing reports list if refreshing fails
                            errorMessage = err.message ?: if (isRefresh) "Failed to refresh medical reports" else "Failed to load medical reports"
                        )
                    }
                )
            }
        }
    }

    fun refreshReports() {
        loadReports(isRefresh = true)
    }

    private fun handleFilePicked(uri: Uri, context: Context) {
        val name = ReportFileHelper.getFileName(context, uri)
        val size = ReportFileHelper.getFileSize(context, uri)

        // 10 MB limit
        val maxSizeBytes = 10 * 1024 * 1024L
        if (size > maxSizeBytes) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "File size (${MedicalReportItem.formatBytes(size)}) exceeds maximum allowed 10 MB."
            )
            return
        }

        val ext = name.substringAfterLast('.', "").lowercase()
        val validExtensions = setOf("pdf", "jpg", "jpeg", "png")
        if (ext.isNotEmpty() && ext !in validExtensions) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Unsupported file format (.$ext). Please upload a PDF, JPG, or PNG document."
            )
            return
        }

        val defaultCategory = if (name.contains("scan", ignoreCase = true) ||
            name.contains("ultrasound", ignoreCase = true) ||
            name.contains("usg", ignoreCase = true) ||
            name.contains("sono", ignoreCase = true)
        ) {
            ReportCategory.ULTRASOUND
        } else {
            ReportCategory.LAB_RESULTS
        }

        val defaultTitle = name.substringBeforeLast('.')
            .replace('_', ' ')
            .replace('-', ' ')
            .trim()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString() }

        _uiState.value = _uiState.value.copy(
            showUploadDialog = true,
            selectedFileUri = uri,
            uploadFileName = name,
            uploadFileSizeBytes = size,
            uploadFileSizeFormatted = MedicalReportItem.formatBytes(size),
            uploadTitle = defaultTitle.ifBlank { name },
            uploadCategory = defaultCategory,
            uploadDescription = "",
            uploadErrorMessage = null,
            errorMessage = null
        )
    }

    private fun uploadSelectedReport(context: Context) {
        val uri = _uiState.value.selectedFileUri ?: return
        val title = _uiState.value.uploadTitle.trim()

        if (title.isBlank()) {
            _uiState.value = _uiState.value.copy(uploadErrorMessage = "Please enter a report title")
            return
        }

        if (_uiState.value.isUploading) return

        _uiState.value = _uiState.value.copy(isUploading = true, uploadErrorMessage = null)

        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream == null) {
                    _uiState.value = _uiState.value.copy(
                        isUploading = false,
                        uploadErrorMessage = "Unable to read selected file"
                    )
                    return@launch
                }

                val bytes = inputStream.use { it.readBytes() }
                val mimeType = context.contentResolver.getType(uri)
                    ?: ReportFileHelper.resolveMimeType(_uiState.value.uploadFileName)
                val fileMediaType = MediaType.parse(mimeType)
                val requestBody = RequestBody.create(fileMediaType, bytes)
                val filePart = MultipartBody.Part.createFormData(
                    "file",
                    _uiState.value.uploadFileName.ifBlank { "report.pdf" },
                    requestBody
                )

                val textMediaType = MediaType.parse("text/plain")
                val titleBody = RequestBody.create(textMediaType, title)
                val catStr = if (_uiState.value.uploadCategory == ReportCategory.ULTRASOUND) "ULTRASOUND" else "LAB_RESULTS"
                val categoryBody = RequestBody.create(textMediaType, catStr)
                val descStr = _uiState.value.uploadDescription.trim().takeIf { it.isNotBlank() }
                val descBody = descStr?.let { RequestBody.create(textMediaType, it) }

                repository.uploadMedicalReport(filePart, titleBody, categoryBody, descBody).collect { result ->
                    result.fold(
                        onSuccess = {
                            _uiState.value = _uiState.value.copy(
                                isUploading = false,
                                showUploadDialog = false,
                                selectedFileUri = null,
                                successMessage = "Medical report uploaded successfully"
                            )
                            loadReports()
                        },
                        onFailure = { err ->
                            _uiState.value = _uiState.value.copy(
                                isUploading = false,
                                uploadErrorMessage = err.message ?: "Failed to upload report"
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    uploadErrorMessage = e.localizedMessage ?: "Failed to process file"
                )
            }
        }
    }

    fun openReport(report: MedicalReportItem, context: Context, isShare: Boolean = false) {
        if (_uiState.value.downloadingReportId == report.id) return

        val safeTitle = report.title.ifBlank { "report_${report.id}" }
            .replace(Regex("[^a-zA-Z0-9_-]"), "_")
        val ext = when {
            report.originalFilename?.lowercase()?.endsWith(".jpg") == true -> "jpg"
            report.originalFilename?.lowercase()?.endsWith(".jpeg") == true -> "jpeg"
            report.originalFilename?.lowercase()?.endsWith(".png") == true -> "png"
            report.fileType.equals("jpg", ignoreCase = true) || report.fileType.equals("jpeg", ignoreCase = true) -> "jpg"
            report.fileType.equals("png", ignoreCase = true) -> "png"
            else -> "pdf"
        }
        val mime = ReportFileHelper.resolveMimeType(
            fileName = report.originalFilename ?: "report.$ext",
            fileType = report.fileType,
            mimeType = report.mimeType
        )

        val destDir = File(context.cacheDir, "reports")
        destDir.mkdirs()
        val destFile = File(destDir, "report_${report.id}_$safeTitle.$ext")

        if (destFile.exists() && destFile.length() > 0) {
            if (isShare) {
                ReportFileHelper.shareFile(context, destFile, mime, report.title)
            } else {
                ReportFileHelper.openFile(context, destFile, mime)
            }
            return
        }

        _uiState.value = _uiState.value.copy(downloadingReportId = report.id, errorMessage = null)
        viewModelScope.launch {
            val flow = if (!report.downloadUrl.isNullOrBlank()) {
                repository.downloadReportFromUrl(report.downloadUrl, destFile)
            } else {
                repository.downloadReportFile(report.id, destFile)
            }
            flow.collect { result ->
                _uiState.value = _uiState.value.copy(downloadingReportId = null)
                result.fold(
                    onSuccess = { file ->
                        if (isShare) {
                            ReportFileHelper.shareFile(context, file, mime, report.title)
                        } else {
                            ReportFileHelper.openFile(context, file, mime)
                        }
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = err.message ?: "Failed to open medical report"
                        )
                    }
                )
            }
        }
    }

    class Factory(private val repository: MedicalReportsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MedicalReportsViewModel(repository) as T
        }
    }
}
