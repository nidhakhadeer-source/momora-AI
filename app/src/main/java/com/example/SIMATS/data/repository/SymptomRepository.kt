package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.SymptomEntryData
import com.example.SIMATS.data.model.SymptomSeverity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface SymptomRepository {
    fun getSymptomEntryData(): Flow<SymptomEntryData>
    fun saveSymptom(data: SymptomEntryData): Flow<Result<Boolean>>
}

class SymptomRepositoryImpl : SymptomRepository {
    override fun getSymptomEntryData(): Flow<SymptomEntryData> = flow {
        emit(
            SymptomEntryData(
                symptomName = "Heartburn after meals",
                severity = SymptomSeverity.MILD,
                startedText = "26 Oct, 13:00",
                durationText = "About 20 min",
                aiGuidanceText = "Smaller meals and sitting upright after eating may help. Speak with your maternity team if it persists, worsens, or worries you.",
                availableSymptoms = listOf(
                    "Heartburn after meals",
                    "Morning sickness / Nausea",
                    "Lower back tension",
                    "Mild fatigue",
                    "Swollen ankles"
                )
            )
        )
    }

    override fun saveSymptom(data: SymptomEntryData): Flow<Result<Boolean>> = flow {
        delay(800)
        emit(Result.success(true))
    }
}
