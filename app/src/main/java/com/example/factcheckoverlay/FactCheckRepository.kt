package com.example.factcheckoverlay

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class FactCheckRepository(private val preferences: AppPreferences) {
    suspend fun check(text: String): Result<FactCheckResponse> = withContext(Dispatchers.IO) {
        runCatching {
            val payload = JSONObject()
                .put("text", text)

            val connection = (URL(preferences.baseUrl).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 15000
                readTimeout = 30000
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                if (preferences.bearerToken.isNotBlank()) {
                    setRequestProperty("Authorization", "Bearer ${preferences.bearerToken}")
                }
            }

            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(payload.toString())
            }

            val statusCode = connection.responseCode
            val body = if (statusCode in 200..299) {
                connection.inputStream.bufferedReader().use(BufferedReader::readText)
            } else {
                connection.errorStream?.bufferedReader()?.use(BufferedReader::readText).orEmpty()
            }

            if (statusCode !in 200..299) {
                error("Backend returned HTTP $statusCode: $body")
            }

            parseResponse(body)
        }
    }

    private fun parseResponse(body: String): FactCheckResponse {
        val json = JSONObject(body)
        val sourcesJson = json.optJSONArray("sources") ?: JSONArray()
        val sources = buildList {
            for (index in 0 until sourcesJson.length()) {
                val item = sourcesJson.getJSONObject(index)
                add(
                    FactCheckSource(
                        title = item.optString("title"),
                        url = item.optString("url")
                    )
                )
            }
        }

        return FactCheckResponse(
            claim = json.optString("claim"),
            verdict = json.optString("verdict"),
            confidence = json.optDouble("confidence", 0.0),
            summary = json.optString("summary"),
            sources = sources
        )
    }
}
