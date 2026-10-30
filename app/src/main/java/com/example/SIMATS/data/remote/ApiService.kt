package com.example.SIMATS.data.remote

import com.example.SIMATS.data.model.AddDoctorData
import com.example.SIMATS.data.model.AdminDashboardData
import com.example.SIMATS.data.model.AdminSettingsData
import com.example.SIMATS.data.model.AppointmentBookingData
import com.example.SIMATS.data.model.AssignedPatientItem
import com.example.SIMATS.data.model.BabyDevelopmentData
import com.example.SIMATS.data.model.BloodPressureData
import com.example.SIMATS.data.model.ChatMessage
import com.example.SIMATS.data.model.DoctorAccountItem
import com.example.SIMATS.data.model.DoctorAdviceData
import com.example.SIMATS.data.model.DoctorDashboardData
import com.example.SIMATS.data.model.DoctorScheduleData
import com.example.SIMATS.data.model.HealthMonitoringData
import com.example.SIMATS.data.model.MedicalReportItem
import com.example.SIMATS.data.model.MedicationReminderData
import com.example.SIMATS.data.model.NotificationItem
import com.example.SIMATS.data.model.NutritionPlanData
import com.example.SIMATS.data.model.PatientAccountItem
import com.example.SIMATS.data.model.PatientDashboardData
import com.example.SIMATS.data.model.PatientHealthMonitoringData
import com.example.SIMATS.data.model.PatientMedicalHistoryData
import com.example.SIMATS.data.model.PatientProfileData
import com.example.SIMATS.data.model.PregnancyDetailsData
import com.example.SIMATS.data.model.SymptomEntryData
import com.example.SIMATS.data.model.WeightTrackerData
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    // -------------------------------------------------------------------------
    // Authentication
    // -------------------------------------------------------------------------
    @FormUrlEncoded
    @POST("auth/login.php")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("role") role: String? = null
    ): LoginResponse

    @FormUrlEncoded
    @POST("auth/signup.php")
    suspend fun signup(
        @Field("fullName") fullName: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("dateOfBirth") dateOfBirth: String? = null
    ): SignUpResponse

    // -------------------------------------------------------------------------
    // Patient Profile
    // -------------------------------------------------------------------------
    @GET("profile/profile_fetch.php")
    suspend fun getProfile(
        @Query("user_id") userId: Int? = null
    ): DataResponse<PatientProfileData>

    @FormUrlEncoded
    @POST("profile/profile_edit.php")
    suspend fun updateProfile(
        @FieldMap fields: Map<String, String>
    ): BaseResponse

    // -------------------------------------------------------------------------
    // Patient Dashboard
    // -------------------------------------------------------------------------
    @GET("dashboard/patient_dashboard.php")
    suspend fun getPatientDashboard(
        @Query("user_id") userId: Int? = null
    ): DataResponse<PatientDashboardData>

    // -------------------------------------------------------------------------
    // Pregnancy Details
    // -------------------------------------------------------------------------
    @GET("pregnancy/pregnancy_details.php")
    suspend fun getPregnancyDetails(
        @Query("user_id") userId: Int? = null
    ): DataResponse<PregnancyDetailsData>

    @FormUrlEncoded
    @POST("pregnancy/pregnancy_details.php")
    suspend fun savePregnancyDetails(
        @Field("estimatedDueDate") edd: String,
        @Field("lastMenstrualPeriod") lmp: String? = null
    ): BaseResponse

    // -------------------------------------------------------------------------
    // Health Monitoring & Vitals
    // -------------------------------------------------------------------------
    @GET("health/vitals.php")
    suspend fun getHealthVitals(
        @Query("user_id") userId: Int? = null
    ): DataResponse<HealthMonitoringData>

    @GET("health/blood_pressure.php")
    suspend fun getBloodPressureData(
        @Query("user_id") userId: Int? = null
    ): DataResponse<BloodPressureData>

    @FormUrlEncoded
    @POST("health/blood_pressure.php")
    suspend fun saveBloodPressure(
        @Field("systolic") systolic: String,
        @Field("diastolic") diastolic: String
    ): BaseResponse

    @GET("health/weight.php")
    suspend fun getWeightData(
        @Query("user_id") userId: Int? = null
    ): DataResponse<WeightTrackerData>

    @FormUrlEncoded
    @POST("health/weight.php")
    suspend fun saveWeight(
        @Field("weight") weight: String,
        @Field("date") date: String
    ): BaseResponse

    @GET("health/symptoms.php")
    suspend fun getSymptomData(
        @Query("user_id") userId: Int? = null
    ): DataResponse<SymptomEntryData>

    @FormUrlEncoded
    @POST("health/symptoms.php")
    suspend fun saveSymptom(
        @FieldMap fields: Map<String, String>
    ): BaseResponse

    // -------------------------------------------------------------------------
    // Baby Development & Nutrition
    // -------------------------------------------------------------------------
    @GET("baby/development.php")
    suspend fun getBabyDevelopment(
        @Query("week") week: Int = 24
    ): DataResponse<BabyDevelopmentData>

    @GET("nutrition/plan.php")
    suspend fun getNutritionPlan(
        @Query("trimester") trimester: Int = 2
    ): DataResponse<NutritionPlanData>

    // -------------------------------------------------------------------------
    // Medication Reminders
    // -------------------------------------------------------------------------
    @GET("medication/reminders.php")
    suspend fun getMedicationReminders(
        @Query("user_id") userId: Int? = null
    ): DataResponse<MedicationReminderData>

    @FormUrlEncoded
    @POST("medication/reminders.php")
    suspend fun markMedicationTaken(
        @Field("medicationId") medicationId: Int
    ): BaseResponse

    // -------------------------------------------------------------------------
    // Appointments & Booking
    // -------------------------------------------------------------------------
    @GET("appointments/appointments.php")
    suspend fun getAppointmentBookingData(
        @Query("user_id") userId: Int? = null
    ): DataResponse<AppointmentBookingData>

    @FormUrlEncoded
    @POST("appointments/appointments.php")
    suspend fun confirmAppointment(
        @FieldMap fields: Map<String, String>
    ): BaseResponse

    // -------------------------------------------------------------------------
    // Medical Reports
    // -------------------------------------------------------------------------
    @GET("reports/reports.php")
    suspend fun getReports(
        @Query("user_id") userId: Int? = null
    ): DataResponse<List<MedicalReportItem>>

    @FormUrlEncoded
    @POST("reports/reports.php")
    suspend fun uploadReport(
        @Field("fileName") fileName: String,
        @Field("category") category: String = "LAB_RESULTS"
    ): BaseResponse

    // -------------------------------------------------------------------------
    // Notifications
    // -------------------------------------------------------------------------
    @GET("notifications/notifications.php")
    suspend fun getNotifications(
        @Query("user_id") userId: Int? = null
    ): DataResponse<List<NotificationItem>>

    @POST("notifications/notifications.php")
    suspend fun markAllNotificationsRead(): BaseResponse

    // -------------------------------------------------------------------------
    // AI Chat
    // -------------------------------------------------------------------------
    @GET("ai/chat.php")
    suspend fun getChatMessages(
        @Query("user_id") userId: Int? = null
    ): DataResponse<List<ChatMessage>>

    @FormUrlEncoded
    @POST("ai/chat.php")
    suspend fun sendChatMessage(
        @Field("message") message: String
    ): DataResponse<ChatMessage>

    // -------------------------------------------------------------------------
    // Doctor Portal
    // -------------------------------------------------------------------------
    @GET("doctor/dashboard.php")
    suspend fun getDoctorDashboard(
        @Query("user_id") userId: Int? = null
    ): DataResponse<DoctorDashboardData>

    @GET("doctor/patients.php")
    suspend fun getAssignedPatients(
        @Query("user_id") userId: Int? = null
    ): DataResponse<List<AssignedPatientItem>>

    @GET("doctor/patient_history.php")
    suspend fun getPatientHistory(
        @Query("patientId") patientId: String
    ): DataResponse<PatientMedicalHistoryData>

    @GET("doctor/patient_monitoring.php")
    suspend fun getPatientHealthMonitoring(
        @Query("patientId") patientId: String
    ): DataResponse<PatientHealthMonitoringData>

    @GET("doctor/advice.php")
    suspend fun getDoctorAdviceData(
        @Query("patientId") patientId: String
    ): DataResponse<DoctorAdviceData>

    @FormUrlEncoded
    @POST("doctor/advice.php")
    suspend fun sendDoctorAdvice(
        @FieldMap fields: Map<String, String>
    ): BaseResponse

    @GET("doctor/schedule.php")
    suspend fun getDoctorSchedule(
        @Query("user_id") userId: Int? = null
    ): DataResponse<DoctorScheduleData>

    // -------------------------------------------------------------------------
    // Admin Portal
    // -------------------------------------------------------------------------
    @GET("admin/dashboard.php")
    suspend fun getAdminDashboard(
        @Query("user_id") userId: Int? = null
    ): DataResponse<AdminDashboardData>

    @GET("admin/doctors.php?action=form")
    suspend fun getAddDoctorFormData(): DataResponse<AddDoctorData>

    @FormUrlEncoded
    @POST("admin/doctors.php?action=create")
    suspend fun createDoctorAccount(
        @FieldMap fields: Map<String, String>
    ): BaseResponse

    @GET("admin/doctors.php")
    suspend fun getManageDoctors(): DataResponse<List<DoctorAccountItem>>

    @FormUrlEncoded
    @POST("admin/doctors.php?action=toggle")
    suspend fun toggleDoctorStatus(
        @Field("doctorId") doctorId: String
    ): BaseResponse

    @GET("admin/patients.php")
    suspend fun getManagePatients(): DataResponse<List<PatientAccountItem>>

    @FormUrlEncoded
    @POST("admin/patients.php?action=toggle")
    suspend fun togglePatientAccess(
        @Field("patientId") patientId: String
    ): BaseResponse

    @GET("admin/settings.php")
    suspend fun getAdminSettings(): DataResponse<AdminSettingsData>

    @POST("admin/settings.php")
    suspend fun adminLogout(): BaseResponse
}
