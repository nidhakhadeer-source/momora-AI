package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.DoctorCompactAppointment
import com.example.SIMATS.data.model.DoctorMainAppointment
import com.example.SIMATS.data.model.DoctorScheduleData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface DoctorScheduleRepository {
    fun getScheduleData(): Flow<DoctorScheduleData>
}

class DoctorScheduleRepositoryImpl : DoctorScheduleRepository {
    override fun getScheduleData(): Flow<DoctorScheduleData> = flow {
        emit(
            DoctorScheduleData(
                doctorName = "Dr Amelia Chen",
                dateText = "Thursday, 29 Oct 2026",
                dateTabs = listOf("27 Oct", "28 Oct", "29 Oct"),
                mainAppointment = DoctorMainAppointment(
                    timeRange = "10:30–11:00",
                    statusText = "Confirmed",
                    patientName = "Maya Patel",
                    patientSubtitle = "24w 3d on 27 Oct · Patient P-13042",
                    patientIdText = "P-13042",
                    notesText = "Routine antenatal check · In person, Willow Maternity Clinic · Room 3."
                ),
                otherAppointments = listOf(
                    DoctorCompactAppointment(
                        id = "1",
                        patientName = "Laila Ahmed",
                        timeText = "09:00",
                        visitTypeAndMode = "Routine antenatal · In person",
                        statusText = "Confirmed",
                        isVideo = false
                    ),
                    DoctorCompactAppointment(
                        id = "2",
                        patientName = "Emma Wilson",
                        timeText = "14:00",
                        visitTypeAndMode = "Follow-up · Video visit",
                        statusText = "Confirmed",
                        isVideo = true
                    )
                ),
                visitsSummaryNote = "Today, 27 Oct · 6 visits; 2 completed, 4 upcoming. Next: Nora Williams at 11:30."
            )
        )
    }
}
