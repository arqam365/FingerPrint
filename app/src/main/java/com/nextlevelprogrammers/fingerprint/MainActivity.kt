package com.nextlevelprogrammers.fingerprint

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        executor = Executors.newSingleThreadExecutor()
        sharedPreferences = getSharedPreferences("FingerprintPrefs", MODE_PRIVATE)

        if (isFingerprintRegistered()) {
            authenticateWithFingerprint()
        } else {
            startActivity(Intent(this, RegistrationActivity::class.java))
            finish() // Finish MainActivity to prevent user from going back to it after registration
        }
    }

    private fun isFingerprintRegistered(): Boolean {
        return sharedPreferences.getBoolean("isFingerprintRegistered", false)
    }

    private fun authenticateWithFingerprint() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authenticate with Fingerprint")
            .setSubtitle("Place your finger on the sensor to authenticate")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    runOnUiThread {
                        showToast("Fingerprint authentication succeeded! Data matched.")
                    }
                    startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                    finish() // Finish MainActivity after successful authentication
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    runOnUiThread {
                        showToast("Fingerprint authentication failed: $errString")
                    }
                }
            })

        biometricPrompt.authenticate(promptInfo)
    }


    private fun showToast(message: String) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
