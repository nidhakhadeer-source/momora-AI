package com.example.SIMATS.ui.duedate

import com.example.SIMATS.data.model.PatientDashboardData
import com.example.SIMATS.data.model.PregnancyDetailsData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class PregnancyMetrics(
    val weeks: Int,
    val days: Int,
    val trimester: Int,
    val trimesterText: String,
    val remainingDays: Int,
    val countdownText: String,
    val gestationalAgeText: String,
    val progress: Float,
    val lmpFormatted: String,
    val eddFormatted: String
)

object PregnancyCalculator {
    private val dateFormats = listOf(
        "d MMM yyyy",
        "dd MMM yyyy",
        "yyyy-MM-dd",
        "dd/MM/yyyy",
        "dd-MM-yyyy",
        "d MMMM yyyy"
    )

    fun parseDate(dateStr: String): Date? {
        val trimmed = dateStr.trim()
        for (pattern in dateFormats) {
            try {
                val format = SimpleDateFormat(pattern, Locale.ENGLISH).apply {
                    isLenient = false
                    timeZone = TimeZone.getDefault()
                }
                val parsed = format.parse(trimmed)
                if (parsed != null) return parsed
            } catch (_: Exception) {}
        }
        return null
    }

    fun formatDate(date: Date): String {
        val format = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).apply {
            timeZone = TimeZone.getDefault()
        }
        return format.format(date)
    }

    // EDD = LMP + 280 days (40 weeks)
    fun calculateEddFromLmp(lmp: Date): Date {
        val cal = Calendar.getInstance().apply {
            time = lmp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_YEAR, 280)
        }
        return cal.time
    }

    // LMP = EDD - 280 days
    fun calculateLmpFromEdd(edd: Date): Date {
        val cal = Calendar.getInstance().apply {
            time = edd
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_YEAR, -280)
        }
        return cal.time
    }

    fun calculateGestationalAge(lmp: Date, targetDate: Date = Date()): Pair<Int, Int> {
        val calLmp = Calendar.getInstance().apply {
            time = lmp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val calTarget = Calendar.getInstance().apply {
            time = targetDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val diffMillis = calTarget.timeInMillis - calLmp.timeInMillis
        val elapsedDays = Math.round(diffMillis.toDouble() / (1000.0 * 60 * 60 * 24)).toInt()
        val clampedDays = elapsedDays.coerceIn(0, 294) // up to 42 weeks
        val weeks = clampedDays / 7
        val days = clampedDays % 7
        return Pair(weeks, days)
    }

    fun calculateRemainingDays(edd: Date, targetDate: Date = Date()): Int {
        val calEdd = Calendar.getInstance().apply {
            time = edd
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val calTarget = Calendar.getInstance().apply {
            time = targetDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val diffMillis = calEdd.timeInMillis - calTarget.timeInMillis
        val diffDays = Math.round(diffMillis.toDouble() / (1000.0 * 60 * 60 * 24)).toInt()
        return maxOf(0, diffDays)
    }

    fun determineTrimester(weeks: Int): Int {
        return when {
            weeks <= 13 -> 1
            weeks in 14..27 -> 2
            else -> 3
        }
    }

    fun determineTrimesterText(trimester: Int): String {
        return when (trimester) {
            1 -> "FIRST TRIMESTER"
            3 -> "THIRD TRIMESTER"
            else -> "SECOND TRIMESTER"
        }
    }

    fun calculateMetrics(lmpStr: String, eddStr: String, targetDate: Date = Date()): PregnancyMetrics {
        var lmpDate = parseDate(lmpStr)
        var eddDate = parseDate(eddStr)

        if (lmpDate == null && eddDate != null) {
            lmpDate = calculateLmpFromEdd(eddDate)
        } else if (eddDate == null && lmpDate != null) {
            eddDate = calculateEddFromLmp(lmpDate)
        } else if (lmpDate == null && eddDate == null) {
            val cal = Calendar.getInstance().apply {
                time = targetDate
                add(Calendar.DAY_OF_YEAR, -171)
            }
            lmpDate = cal.time
            eddDate = calculateEddFromLmp(lmpDate)
        }

        val nonNullLmp = lmpDate!!
        val nonNullEdd = eddDate!!

        val (weeks, days) = calculateGestationalAge(nonNullLmp, targetDate)
        val remainingDays = calculateRemainingDays(nonNullEdd, targetDate)
        val remWeeks = remainingDays / 7
        val remDays = remainingDays % 7
        val trimester = determineTrimester(weeks)
        val trimesterText = determineTrimesterText(trimester)
        val countdownText = "$remWeeks weeks, $remDays days until your estimated due date."
        val gestationalAgeText = "$weeks weeks, $days days"
        val progress = (weeks.toFloat() / 40f).coerceIn(0f, 1f)

        return PregnancyMetrics(
            weeks = weeks,
            days = days,
            trimester = trimester,
            trimesterText = trimesterText,
            remainingDays = remainingDays,
            countdownText = countdownText,
            gestationalAgeText = gestationalAgeText,
            progress = progress,
            lmpFormatted = formatDate(nonNullLmp),
            eddFormatted = formatDate(nonNullEdd)
        )
    }

    fun extractDateFromDueDateText(text: String): String? {
        val regex = Regex("""\b(\d{1,2}\s+[A-Za-z]{3,}\s+\d{4})\b""")
        return regex.find(text)?.groupValues?.get(1)
    }

    fun computeFromDashboardData(dashboardData: PatientDashboardData, targetDate: Date = Date()): PregnancyMetrics? {
        val extractedDueDate = extractDateFromDueDateText(dashboardData.dueDateText) ?: return null
        return calculateMetrics(lmpStr = "", eddStr = extractedDueDate, targetDate = targetDate)
    }
}

object PregnancyDataHolder {
    private val _pregnancyData = MutableStateFlow<PregnancyDetailsData?>(null)
    val pregnancyData: StateFlow<PregnancyDetailsData?> = _pregnancyData.asStateFlow()

    private val _pregnancyMetrics = MutableStateFlow<PregnancyMetrics?>(null)
    val pregnancyMetrics: StateFlow<PregnancyMetrics?> = _pregnancyMetrics.asStateFlow()

    fun updatePregnancyData(data: PregnancyDetailsData, targetDate: Date = Date()): PregnancyMetrics {
        _pregnancyData.value = data
        val lmp = data.lastMenstrualPeriod.ifBlank { "09 May 2026" }
        val edd = data.estimatedDueDate.ifBlank { "13 Feb 2027" }
        val metrics = PregnancyCalculator.calculateMetrics(lmp, edd, targetDate)
        _pregnancyMetrics.value = metrics
        return metrics
    }

    fun updateMetrics(metrics: PregnancyMetrics) {
        _pregnancyMetrics.value = metrics
        _pregnancyData.value = _pregnancyData.value?.copy(
            estimatedDueDate = metrics.eddFormatted,
            lastMenstrualPeriod = metrics.lmpFormatted,
            gestationalAgeText = metrics.gestationalAgeText,
            trimesterText = metrics.trimesterText,
            countdownText = metrics.countdownText
        )
    }

    fun getOrComputeMetrics(targetDate: Date = Date()): PregnancyMetrics? {
        val current = _pregnancyMetrics.value
        if (current != null) return current
        val data = _pregnancyData.value
        if (data != null) {
            return updatePregnancyData(data, targetDate)
        }
        return null
    }

    fun clear() {
        _pregnancyData.value = null
        _pregnancyMetrics.value = null
    }
}

data class WeeklyPregnancyInfo(
    val week: Int,
    val babySizeComparison: String,
    val babyDevelopment: String,
    val motherBodyChanges: String,
    val careTip: String
)

object PregnancyInfoHelper {
    fun getInfoForWeek(week: Int): WeeklyPregnancyInfo {
        val clampedWeek = week.coerceIn(1, 40)
        return when (clampedWeek) {
            in 1..4 -> WeeklyPregnancyInfo(
                week = clampedWeek,
                babySizeComparison = "Poppy seed (< 1 mm)",
                babyDevelopment = "A fertilized egg forms a blastocyst and implants into the uterine lining. The amniotic sac and placenta begin taking shape.",
                motherBodyChanges = "You may not feel different yet, though some notice light spotting, tender breasts, or subtle fatigue as hormones rise.",
                careTip = "Start a daily prenatal vitamin with at least 400 mcg of folic acid and prioritize hydration."
            )
            in 5..8 -> WeeklyPregnancyInfo(
                week = clampedWeek,
                babySizeComparison = "Blueberry (~1.6 cm, 1 g)",
                babyDevelopment = "Baby's tiny heart begins beating, limb buds appear, and facial features like eyes, nose, and lips begin forming.",
                motherBodyChanges = "Morning queasiness, heightened sense of smell, and frequent trips to the bathroom are common as hCG peaks.",
                careTip = "Eat small, frequent meals with bland proteins and complex carbs to soothe mild nausea."
            )
            in 9..12 -> WeeklyPregnancyInfo(
                week = clampedWeek,
                babySizeComparison = "Lime (~5.4 cm, 14 g)",
                babyDevelopment = "All essential body organs are formed. Baby's fingers and toes separate, vocal cords develop, and tiny reflexes begin.",
                motherBodyChanges = "Blood volume is steadily increasing. Your clothes may feel snug around the waist as your uterus expands upwards.",
                careTip = "Wear comfortable, non-restrictive clothing and drink water consistently throughout the day."
            )
            in 13..16 -> WeeklyPregnancyInfo(
                week = clampedWeek,
                babySizeComparison = "Avocado (~11.6 cm, 100 g)",
                babyDevelopment = "Second trimester begins! Baby can make facial expressions, suck their thumb, and their bones are hardening.",
                motherBodyChanges = "Energy often rebounds and nausea eases. You might notice a faint dark line (linea nigra) on your abdomen.",
                careTip = "Introduce gentle prenatal yoga or daily 20-minute walks to support posture and circulation."
            )
            in 17..20 -> WeeklyPregnancyInfo(
                week = clampedWeek,
                babySizeComparison = "Banana (~16.4 cm, 300 g)",
                babyDevelopment = "A protective waxy coating (vernix) covers baby's delicate skin. Sensory development accelerates and hearing sharpens.",
                motherBodyChanges = "You may feel the magical fluttering of baby's first kicks (quickening). Your center of gravity begins shifting forward.",
                careTip = "Use a small support pillow between your knees when resting on your side to relieve lower back tension."
            )
            in 21..24 -> WeeklyPregnancyInfo(
                week = clampedWeek,
                babySizeComparison = "Ear of corn (~30 cm, 600 g)",
                babyDevelopment = "Baby's taste buds are forming, lungs produce surfactant, and they recognize maternal heartbeat and familiar voices.",
                motherBodyChanges = "Your uterus sits about 4 cm above your belly button. You may feel more distinct kicks, rhythmic hiccups, and active movements.",
                careTip = "Talk, sing, or read softly to your belly; your baby can hear and find your voice deeply comforting."
            )
            in 25..28 -> WeeklyPregnancyInfo(
                week = clampedWeek,
                babySizeComparison = "Eggplant (~37 cm, 1 kg)",
                babyDevelopment = "Baby can open their eyes and blink! Rapid brain cell connections form and regular sleep/wake cycles emerge.",
                motherBodyChanges = "As the third trimester approaches, you might experience mild ankle swelling, leg cramps, or vivid dreams.",
                careTip = "Elevate your feet in the evening and include calcium- and magnesium-rich foods in your dinner."
            )
            in 29..32 -> WeeklyPregnancyInfo(
                week = clampedWeek,
                babySizeComparison = "Butternut squash (~42 cm, 1.7 kg)",
                babyDevelopment = "Baby gains steady body fat to regulate temperature. Bones are fully formed, though skull bones remain soft for delivery.",
                motherBodyChanges = "Shortness of breath can occur as your uterus nudges upward against your diaphragm. Practice deep diaphragmatic breathing.",
                careTip = "Eat nutrient-dense smaller portions and stay mindful of daily kick counts during baby's active hours."
            )
            in 33..36 -> WeeklyPregnancyInfo(
                week = clampedWeek,
                babySizeComparison = "Honeydew melon (~47 cm, 2.6 kg)",
                babyDevelopment = "Lungs and central nervous system are nearly fully mature. Baby turns head-down in preparation for labor.",
                motherBodyChanges = "You may feel increased pelvic pressure as baby settles lower ('lightening'). Mild Braxton Hicks contractions can become noticeable.",
                careTip = "Pack your hospital bag, finalize your birth preferences, and keep emergency contact numbers handy."
            )
            else -> WeeklyPregnancyInfo(
                week = clampedWeek,
                babySizeComparison = "Small pumpkin (~50 cm, 3.3 kg)",
                babyDevelopment = "Full term! Baby's grasp is firm, organs are ready for independent breathing, and they are preparing for their arrival.",
                motherBodyChanges = "Cervix begins softening and effacing. You may feel a nesting urge alongside anticipation.",
                careTip = "Rest whenever possible, trust your body, and contact your care team if you notice regular contractions or fluid leaks."
            )
        }
    }
}
