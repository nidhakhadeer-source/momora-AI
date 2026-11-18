package com.example.SIMATS.ui.components

/**
 * Utility functions to format times and sanitize text displayed in Doctor screens.
 *
 * 1. Shows times as HH:mm everywhere (no seconds, e.g. 10:30 not 10:30:00).
 * 2. Replaces any "??" separators in displayed text with " · ".
 */

/**
 * Formats time strings to HH:mm (removing seconds, e.g. 10:30:00 -> 10:30, 08:00:00 -> 08:00).
 * Handles standalone times, time ranges (10:30:00–11:00:00 -> 10:30–11:00),
 * and times embedded within text.
 */
fun String?.formatDoctorTime(): String {
    if (this.isNullOrBlank()) return ""
    return this
        // Replace times with seconds: HH:mm:ss or H:mm:ss (with optional fractional seconds) -> HH:mm
        .replace(Regex("\\b(\\d{1,2}:\\d{2}):\\d{2}(?:\\.\\d+)?\\b"), "$1")
        .trim()
}

/**
 * Sanitizes displayed text across all doctor screens:
 * - Replaces corrupted degree Celsius (??C or ?? C) with °C.
 * - Replaces any "??" separators (with optional surrounding whitespace) with " · ".
 * - Formats times with seconds (e.g. 10:30:00 -> 10:30, 08:15:00.000 -> 08:15) to HH:mm without seconds.
 * - Normalizes any multiple adjacent bullet separators into a single " · ".
 * - Cleans leading and trailing bullet separators.
 */
fun String?.sanitizeDoctorText(): String {
    if (this.isNullOrBlank()) return ""
    return this
        // Corrupted degree Celsius: ??C or ?? C -> °C
        .replace(Regex("\\?\\?\\s*C\\b"), "°C")
        // Corrupted bullet/separator: "??" with surrounding whitespace -> " · "
        .replace(Regex("\\s*\\?\\?\\s*"), " · ")
        // Format any times with seconds: 10:30:00 -> 10:30, 08:15:00.000 -> 08:15
        .replace(Regex("\\b(\\d{1,2}:\\d{2}):\\d{2}(?:\\.\\d+)?\\b"), "$1")
        // Collapse multiple bullets like " · · " to " · "
        .replace(Regex("(\\s*·\\s*){2,}"), " · ")
        // Clean leading bullet
        .replace(Regex("^·\\s*"), "")
        // Clean trailing bullet
        .replace(Regex("\\s*·$"), "")
        .trim()
}

/**
 * Formats error messages across all patient, doctor and admin screens.
 * Translates network timeouts and connection errors to "Check your connection"
 * instead of raw exception text.
 */
fun formatErrorMessage(error: String?): String {
    if (error.isNullOrBlank()) return "Something went wrong. Please check your connection and try again."
    val lower = error.lowercase()
    if (lower.contains("timeout") ||
        lower.contains("timed out") ||
        lower.contains("failed to connect") ||
        lower.contains("unable to resolve host") ||
        lower.contains("connection") ||
        lower.contains("sockettimeout") ||
        lower.contains("unknownhost") ||
        lower.contains("connectexception") ||
        lower.contains("no route to host") ||
        lower.contains("network is unreachable") ||
        lower.contains("check your connection")
    ) {
        return "Check your connection"
    }
    if (lower.contains("exception") || lower.contains("http 500") || lower.contains("internal server error")) {
        return "Unable to connect to service. Check your connection."
    }
    return error.trim()
}
