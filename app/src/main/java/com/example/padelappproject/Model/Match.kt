package com.example.padelappproject.Model

import java.io.Serializable

data class Match(
    val startDateTime: String="",
    val participants: Map<String, String?>,
    val location: String="",
    val court: String="",
    val titel: String=""
):Serializable {
}