package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.DoctorDashboardData
import com.example.SIMATS.data.model.DoctorNextAppointment
import com.example.SIMATS.data.model.DoctorReviewItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface DoctorRepository {
    fun getDoctorDashboardData(): Flow<DoctorDashboardData>
}

class DoctorRepositoryImpl : DoctorRepository {
    override fun getDoctorDashboardData(): Flow<DoctorDashboardData> = flow {
        emit(
            DoctorDashboardData(
                doctorName = "Dr Chen",
                clinicName = "Willow Maternity Clinic",
                dateText = "Tuesday, 27 Oct 2026",
                assignedPatientsCount = 18,
                visitsTodayCount = 6,
                upcomingVisitsCount = 4,
                completedVisitsCount = 2,
                toReviewCount = 2,
                reviewItems = listOf(
                    DoctorReviewItem(
                        id = "1",
                        patientName = "Laila Ahmed",
                        gestationalAge = "30w 2d",
                        reportType = "New ultrasound report"
                    ),
                    DoctorReviewItem(
                        id = "2",
                        patientName = "Sofia Reyes",
                        gestationalAge = "18w 5d",
                        reportType = "New lab report"
                    )
                ),
                nextAppointment = DoctorNextAppointment(
                    patientName = "Nora Williams",
                    timeText = "Today, 11:30",
                    visitType = "Routine antenatal",
                    mode = "Video",
                    futureNote = "Maya Patel's next visit · 29 Oct at 10:30"
                )
            )
        )
    }
}
