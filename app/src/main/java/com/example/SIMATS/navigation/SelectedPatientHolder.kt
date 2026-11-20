package com.example.SIMATS.navigation

object SelectedPatientHolder {
    var selectedPatientId: String? = null
    var selectedPatientName: String? = null
    var initialTab: Int = 0

    fun clear() {
        selectedPatientId = null
        selectedPatientName = null
        initialTab = 0
    }
}

