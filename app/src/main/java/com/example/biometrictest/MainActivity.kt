package com.example.biometrictest

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.biometrictest.ui.theme.BiometricTestTheme

class MainActivity : FragmentActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BiometricTestTheme {
                AuthenticationScreen()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun AuthenticationScreen() {
    var supportsBiometrics by remember { mutableStateOf(false) }
    val context = LocalContext.current as FragmentActivity
    val biometricManager = BiometricManager.from(context)

    supportsBiometrics = when (biometricManager.canAuthenticate(
        BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
        BiometricManager.BIOMETRIC_SUCCESS -> true
        else -> {
            Toast.makeText(context, "Биометрия недоступна", Toast.LENGTH_LONG).show()
            false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            enabled = supportsBiometrics,
            onClick = {
                authenticate(context)
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Аутентификация", fontSize = 22.sp)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
fun authenticate(context: FragmentActivity) {

    val executor = context.mainExecutor
    val biometricPrompt = BiometricPrompt(
        context,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationSucceeded(
                result: BiometricPrompt.AuthenticationResult) {
                Toast.makeText(context, "Аутентифкация пройдена", Toast.LENGTH_LONG).show()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                Toast.makeText(context, "Ошибка при аутентификации: $errString", Toast.LENGTH_LONG).show()
            }

            override fun onAuthenticationFailed() {
                Toast.makeText(context, "Не удалось пройти аутентификацию", Toast.LENGTH_LONG).show()
            }
        })

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Биометрическая аутентификация")
        .setDescription("Используйте отпечаток пальца или камеру для аутентификации")
        .setNegativeButtonText("Отмена")
        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        .build()

    biometricPrompt.authenticate(promptInfo)
}