package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.security.AuthMode
import com.example.security.BiometricHelper
import com.example.ui.CardViewModel
import com.example.ui.HomeScreen
import com.example.ui.components.AuthScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : FragmentActivity() {

    private var viewModelRef: CardViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                // Persian language RTL support
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val viewModel: CardViewModel = viewModel()
                    viewModelRef = viewModel

                    val isUnlocked by viewModel.isUnlocked.collectAsState()
                    val authMode by viewModel.authMode.collectAsState()
                    val isTwoFactorPinPassed by viewModel.twoFactorPinPassed.collectAsState()

                    val isBiometricAvailable = remember {
                        BiometricHelper.isBiometricAvailable(this@MainActivity)
                    }

                    AnimatedContent(
                        targetState = isUnlocked,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "auth_screen_transition",
                        modifier = Modifier.fillMaxSize()
                    ) { unlocked ->
                        if (unlocked) {
                            HomeScreen(
                                viewModel = viewModel,
                                isBiometricAvailable = isBiometricAvailable,
                                onLockClicked = {
                                    viewModel.lockApp()
                                }
                            )
                        } else {
                            AuthScreen(
                                authMode = authMode,
                                isTwoFactorPinPassed = isTwoFactorPinPassed,
                                isBiometricAvailable = isBiometricAvailable,
                                onVerifyPin = { pin ->
                                    viewModel.verifyPin(pin)
                                },
                                onPinSuccess = {
                                    viewModel.unlockWithPinSuccess()
                                    if (authMode == AuthMode.TWO_FACTOR) {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "کد پین تایید شد، اکنون اثر انگشت خود را اسکن کنید",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                onBiometricSuccess = {
                                    viewModel.unlockWithBiometricSuccess()
                                },
                                onRequestBiometric = {
                                    if (isBiometricAvailable) {
                                        BiometricHelper.showBiometricPrompt(
                                            activity = this@MainActivity,
                                            title = if (authMode == AuthMode.TWO_FACTOR) "مرحله دوم: تایید اثر انگشت" else "ورود به کیف کارت امن",
                                            subtitle = "لطفاً اثر انگشت خود را روی حسگر قرار دهید",
                                            negativeButtonText = if (authMode == AuthMode.TWO_FACTOR) "انصراف" else "ورود با کد پین",
                                            onSuccess = {
                                                viewModel.unlockWithBiometricSuccess()
                                            },
                                            onError = { err ->
                                                Toast.makeText(this@MainActivity, err, Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    } else {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "حسگر اثر انگشت در این دستگاه در دسترس نیست",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // Auto lock when leaving app for security
        viewModelRef?.lockApp()
    }
}
