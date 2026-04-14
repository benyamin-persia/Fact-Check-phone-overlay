package com.example.factcheckoverlay

import android.content.Context

class AppPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("fact_check_prefs", Context.MODE_PRIVATE)

    var baseUrl: String
        get() = prefs.getString(KEY_BASE_URL, DEFAULT_BASE_URL).orEmpty()
        set(value) = prefs.edit().putString(KEY_BASE_URL, value.trim()).apply()

    var openAiApiKey: String
        get() = prefs.getString(KEY_OPENAI_API_KEY, "").orEmpty()
        set(value) = prefs.edit().putString(KEY_OPENAI_API_KEY, value.trim()).apply()

    companion object {
        private const val KEY_BASE_URL = "base_url"
        private const val KEY_OPENAI_API_KEY = "openai_api_key"
        private const val DEFAULT_BASE_URL = ""
    }
}
