package com.example.SIMATS.navigation

object SelectedPatientHolder {
    var selectedPatientId: String? = null
    var selectedPatientName: String? = null

    fun clear() {
        selectedPatientId = null
        selectedPatientName = null
    }
}

