package com.codewithfk.expensetracker.android

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.codewithfk.expensetracker.android.ui.theme.ExpenseTrackerAndroidTheme
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                openHomeScreen()
            } else {
                // Authentication failed at device credential level.  Consider a more robust handling.
                Toast.makeText(this, "Device authentication failed", Toast.LENGTH_SHORT).show()
                finish() // Or handle differently, e.g., show an error screen.
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authenticateUser()
    }

    private fun authenticateUser() {
        val biometricManager = BiometricManager.from(this)
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                showBiometricPrompt()
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // No biometrics enrolled, but device might still have PIN/password.
                if (keyguardManager.isDeviceSecure) {
                    showDeviceCredentialPrompt()
                } else {
                    openHomeScreen() // No security at all - proceed.  Review this for your security needs.
                }
            }
            else -> {
                // Biometrics not available or other errors.  Fall back to device credential if secure.
                if (keyguardManager.isDeviceSecure) {
                    showDeviceCredentialPrompt()
                } else {
                    openHomeScreen() // No security at all - proceed.  Review this for your security needs.
                }
            }
        }
    }

    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                openHomeScreen()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.d("Biometric", "Authentication failed") // Add some logging
                // Decide if you want to proceed to device credential on biometric failure, or just fail.
                showDeviceCredentialPrompt()
                //  Or:
                // Toast.makeText(this@MainActivity, "Biometric authentication failed", Toast.LENGTH_SHORT).show()
                // finish()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Log.e("Biometric", "Authentication error: $errString (code $errorCode)") // More logging
                // Similar decision as onAuthenticationFailed: proceed to device credential or fail.
                showDeviceCredentialPrompt()
                // Or:
                // Toast.makeText(this@MainActivity, "Biometric authentication error: $errString", Toast.LENGTH_SHORT).show()
                // finish()
            }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock ExpenseTracker")
            .setSubtitle("Use fingerprint or PIN")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun showDeviceCredentialPrompt() {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val intent = keyguardManager.createConfirmDeviceCredentialIntent("Unlock", "Enter your PIN or password")

        if (intent != null) {
            activityResultLauncher.launch(intent) // Use the launcher correctly
        } else {
            // This should ideally never happen if the device is secure (checked earlier).  But handle it.
            Toast.makeText(this, "Device is secure but no lock screen method available?", Toast.LENGTH_LONG).show()
            openHomeScreen() // Or, finish() if you want to enforce security.
        }
    }


    private fun openHomeScreen() {
        setContent {
            ExpenseTrackerAndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHostScreen()
                }
            }
        }
    }
}