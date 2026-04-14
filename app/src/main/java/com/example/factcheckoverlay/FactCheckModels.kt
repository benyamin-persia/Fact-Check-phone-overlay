package com.example.factcheckoverlay

data class FactCheckRequest(
    val text: String
)

data class FactCheckResponse(
    val claim: String,
    val verdict: String,
    val confidence: Double,
    val summary: String,
    val sources: List<FactCheckSource>
)

data class FactCheckSource(
    val title: String,
    val url: String
)
