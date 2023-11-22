package com.example.padelappproject.Model

data class Match(
    val startDateTime: String,
    val participants: Map<String, String?>,
    val location: String,
    val court: String
) {
}