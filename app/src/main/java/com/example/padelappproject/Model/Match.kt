package com.example.padelappproject.Model

import java.io.Serializable

data class Match(
    val startDay: String="",
    val startTime: String="",
    val participants: Map<String, String?> = emptyMap(),
    val court: String="",
    var titel: String=""
): Serializable