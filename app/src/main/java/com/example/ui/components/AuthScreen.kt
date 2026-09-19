package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.security.AuthMode

@Composable
fun AuthScreen(
    authMode: AuthMode,
    isTwoFactorPinPassed: Boolean,
    isBiometricAvailable: Boolean,
    onVerifyPin: (String) -> Boolean,
    onPinSuccess: () -> Unit,
    onBiometricSuccess: () -> Unit,
    onRequestBiometric: () -> Unit,
    modifier: Modifier = Modifier
) {
    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    // User can switch to PIN mode as fallback if biometric fails or isn't desired
    var forceUsePinFallback by remember { mutableStateOf(false) }

    // If Biometric or Two-factor (step 2), trigger prompt automatically on launch unless user chose PIN fallback
    LaunchedEffect(authMode, isTwoFactorPinPassed, forceUsePinFallback) {
        if (!forceUsePinFallback) {
            if (authMode == AuthMode.BIOMETRIC) {
                onRequestBiometric()
            } else if (authMode == AuthMode.TWO_FACTOR && isTwoFactorPinPassed) {
                onRequestBiometric()
            }
        }
    }

    val isWaitingForBiometric = !forceUsePinFallback && (
            (authMode == AuthMode.BIOMETRIC) ||
            (authMode == AuthMode.TWO_FACTOR && isTwoFactorPinPassed)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B),
                        Color(0xFF0B132B)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Shield Icon
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .size(80.dp)
                    .shadow(12.dp, CircleShape)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isWaitingForBiometric) Icons.Default.Fingerprint else Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "کیف کارت امن",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Step info for 2FA
            if (authMode == AuthMode.TWO_FACTOR) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                ) {
                    Text(
                        text = if (!isTwoFactorPinPassed) "احراز هویت دومرحله‌ای (مرحله ۱ از ۲: کد پین)" else "احراز هویت دومرحله‌ای (مرحله ۲ از ۲: اثر انگشت)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = when {
                    isWaitingForBiometric -> "لطفاً برای دسترسی، انگشت خود را روی حسگر قرار دهید"
                    else -> "لطفاً کد پین امنیتی خود را وارد نمایید"
                },
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.75f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (!isWaitingForBiometric) {
                // PIN Dots Display
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 4) {
                        val isFilled = i < enteredPin.length
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(if (isFilled) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.2f))
                                .border(
                                    1.dp,
                                    if (isFilled) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.5f),
                                    CircleShape
                                )
                        )
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Custom Sleek Numeric Keypad
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val keyRows = listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf("bio", "0", "del")
                    )

                    keyRows.forEach { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            row.forEach { key ->
                                when (key) {
                                    "del" -> {
                                        Surface(
                                            onClick = {
                                                if (enteredPin.isNotEmpty()) {
                                                    enteredPin = enteredPin.dropLast(1)
                                                    errorMessage = null
                                                }
                                            },
                                            shape = CircleShape,
                                            color = Color.White.copy(alpha = 0.08f),
                                            modifier = Modifier.size(68.dp).testTag("keypad_del")
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Backspace,
                                                    contentDescription = "پاک کردن",
                                                    tint = Color.White.copy(alpha = 0.9f),
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                    }
                                    "bio" -> {
                                        if (isBiometricAvailable && authMode != AuthMode.PIN) {
                                            Surface(
                                                onClick = {
                                                    forceUsePinFallback = false
                                                    onRequestBiometric()
                                                },
                                                shape = CircleShape,
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                                modifier = Modifier.size(68.dp).testTag("keypad_bio")
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = Icons.Default.Fingerprint,
                                                        contentDescription = "اثر انگشت",
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(28.dp)
                                                    )
                                                }
                                            }
                                        } else {
                                            Box(modifier = Modifier.size(68.dp))
                                        }
                                    }
                                    else -> {
                                        Surface(
                                            onClick = {
                                                if (enteredPin.length < 6) {
                                                    val newPinVal = enteredPin + key
                                                    enteredPin = newPinVal
                                                    errorMessage = null

                                                    // If reached 4 digits, check if valid
                                                    if (newPinVal.length >= 4) {
                                                        if (onVerifyPin(newPinVal)) {
                                                            onPinSuccess()
                                                            enteredPin = ""
                                                        } else if (newPinVal.length == 6) {
                                                            errorMessage = "کد پین اشتباه است"
                                                            enteredPin = ""
                                                        }
                                                    }
                                                }
                                            },
                                            shape = CircleShape,
                                            color = Color.White.copy(alpha = 0.12f),
                                            modifier = Modifier.size(68.dp).testTag("keypad_$key")
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = key,
                                                    fontSize = 24.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Biometric Waiting View
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 20.dp)
                ) {
                    IconButton(
                        onClick = onRequestBiometric,
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .testTag("auth_biometric_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "لمس حسگر اثر انگشت",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(54.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onRequestBiometric,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("اسکن مجدد اثر انگشت")
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Fallback to PIN option
                    OutlinedButton(
                        onClick = { forceUsePinFallback = true },
                        modifier = Modifier.testTag("switch_to_pin_fallback_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ورود با کد پین", color = Color.White)
                    }
                }
            }
        }
    }
}
