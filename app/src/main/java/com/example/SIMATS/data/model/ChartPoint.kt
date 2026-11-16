package com.example.SIMATS.data.model

import com.google.gson.annotations.SerializedName

data class ChartPoint(
    @SerializedName("first") val first: Float = 0f,
    @SerializedName("second") val second: Float = 0f
) {
    fun toPair(): Pair<Float, Float> = Pair(first, second)
}

fun List<ChartPoint>?.toPairList(): List<Pair<Float, Float>> =
    this?.map { it.first to it.second } ?: emptyList()

