package com.example.factcheckoverlay

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.factcheckoverlay.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var preferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = AppPreferences(this)
        binding.baseUrlInput.setText(preferences.baseUrl)
        binding.apiKeyInput.setText(preferences.openAiApiKey)

        binding.saveButton.setOnClickListener {
            val baseUrl = binding.baseUrlInput.text?.toString().orEmpty()
            val apiKey = binding.apiKeyInput.text?.toString().orEmpty()

            if (baseUrl.isBlank()) {
                Toast.makeText(this, R.string.backend_url_required, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (apiKey.isBlank()) {
                Toast.makeText(this, R.string.api_key_required, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            preferences.baseUrl = baseUrl
            preferences.openAiApiKey = apiKey
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
