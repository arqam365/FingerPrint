package com.nextlevelprogrammers.fingerprint

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class RegistrationActivity : AppCompatActivity() {

    private lateinit var executor: Executor
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        executor = Executors.newSingleThreadExecutor()
        sharedPreferences = getSharedPreferences("FingerprintPrefs", MODE_PRIVATE)

        // Register fingerprint when user clicks a button
        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            registerFingerprint()
        }
    }

    private fun registerFingerprint() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Register Fingerprint")
            .setSubtitle("Place your finger on the sensor to register")
            .setNegativeButtonText("Cancel")
            .build()

        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    runOnUiThread {
                        showToast("Fingerprint registered successfully!")
                        startActivity(Intent(this@RegistrationActivity, HomeActivity::class.java))
                        finish() // Finish RegistrationActivity after successful registration
                    }
                    // Store a flag indicating that fingerprint is registered
                    sharedPreferences.edit().putBoolean("isFingerprintRegistered", true).apply()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    runOnUiThread {
                        showToast("Fingerprint registration failed: $errString")
                    }
                }
            })

        biometricPrompt.authenticate(promptInfo)
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
