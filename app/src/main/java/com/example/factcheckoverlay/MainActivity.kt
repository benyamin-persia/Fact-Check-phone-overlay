package com.example.factcheckoverlay

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.factcheckoverlay.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferences: AppPreferences
    private lateinit var repository: FactCheckRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = AppPreferences(this)
        repository = FactCheckRepository(preferences)

        binding.sourcesText.movementMethod = LinkMovementMethod.getInstance()
        binding.tweetInput.setText(readIncomingText(intent))

        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.overlayPermissionButton.setOnClickListener {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        }

        binding.accessibilityPermissionButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        binding.checkButton.setOnClickListener {
            val input = binding.tweetInput.text?.toString().orEmpty().trim()
            if (input.isBlank()) {
                Toast.makeText(this, "Paste or share some text first.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (preferences.baseUrl.isBlank()) {
                Toast.makeText(this, "Add your backend URL in Settings first.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (preferences.openAiApiKey.isBlank()) {
                Toast.makeText(this, "Add your OpenAI API key in Settings first.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            runFactCheck(input)
        }

        updateSetupStatus()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val sharedText = readIncomingText(intent)
        if (sharedText.isNotBlank()) {
            binding.tweetInput.setText(sharedText)
        }
    }

    override fun onResume() {
        super.onResume()
        updateSetupStatus()
    }

    private fun runFactCheck(input: String) {
        setLoading(true, "Checking live sources...")
        lifecycleScope.launch {
            val result = repository.check(input)
            setLoading(false, "")
            result.onSuccess(::renderResult)
                .onFailure { error ->
                    binding.resultCard.visibility = View.GONE
                    binding.statusText.text = error.message ?: "Fact check failed."
                }
        }
    }

    private fun renderResult(response: FactCheckResponse) {
        binding.resultCard.visibility = View.VISIBLE
        binding.statusText.text = "Live fact check complete."
        binding.verdictText.text = "Verdict: ${response.verdict}"
        binding.confidenceText.text = "Confidence: ${(response.confidence * 100).toInt()}%"
        binding.summaryText.text = response.summary
        binding.sourcesText.text = response.sources.joinToString("\n\n") { source ->
            "${source.title}\n${source.url}"
        }
    }

    private fun setLoading(isLoading: Boolean, status: String) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.checkButton.isEnabled = !isLoading
        binding.statusText.text = status
    }

    private fun readIncomingText(intent: Intent?): String {
        if (intent?.action != Intent.ACTION_SEND) {
            return ""
        }
        return intent.getStringExtra(Intent.EXTRA_TEXT).orEmpty()
    }

    private fun updateSetupStatus() {
        val overlayReady = Settings.canDrawOverlays(this)
        val accessibilityEnabled = isAccessibilityServiceEnabled()
        binding.statusText.text = buildString {
            append("Overlay: ")
            append(if (overlayReady) "enabled" else "disabled")
            append(" | Accessibility: ")
            append(if (accessibilityEnabled) "enabled" else "disabled")
            append(" | API key: ")
            append(if (preferences.openAiApiKey.isNotBlank()) "saved" else "missing")
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ).orEmpty()
        val expected = "$packageName/${ScreenFactCheckAccessibilityService::class.java.name}"
        return enabledServices.contains(expected, ignoreCase = true)
    }
}
