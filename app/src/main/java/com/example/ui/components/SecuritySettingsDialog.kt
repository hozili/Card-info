package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.security.AuthMode
import com.example.security.SecurityPreferences

@Composable
fun SecuritySettingsDialog(
    securityPrefs: SecurityPreferences,
    isBiometricAvailable: Boolean,
    onDismiss: () -> Unit,
    onSaveMode: (AuthMode, String?) -> Unit,
    onExportBackup: (password: String) -> Unit,
    onSelectBackupFile: () -> Unit,
    onImportBackup: (backupJson: String, password: String) -> Unit,
    importedFileContent: String? = null,
    importedFileName: String? = null
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = امنیت ورود, 1 = پشتیبان‌گیری
    var currentMode by remember { mutableStateOf(securityPrefs.authMode) }
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var isPinVisible by remember { mutableStateOf(false) }
    var pinErrorMessage by remember { mutableStateOf<String?>(null) }

    // Backup state
    var exportPassword by remember { mutableStateOf("") }
    var importPassword by remember { mutableStateOf("") }
    var importDataText by remember { mutableStateOf("") }
    var backupMessage by remember { mutableStateOf<String?>(null) }

    // Sync importedFileContent if picked via system picker
    androidx.compose.runtime.LaunchedEffect(importedFileContent) {
        if (!importedFileContent.isNullOrBlank()) {
            importDataText = importedFileContent
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "امنیت و پشتیبان‌گیری رمزنگاری شده",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Offline Assurance Banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "۱۰۰٪ آفلاین و بدون دسترسی اینترنت\nرمزنگاری سخت‌افزاری AES-256 محلی فعال است.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 16.sp
                        )
                    }
                }

                // Tabs: Security vs Backup
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("احراز هویت و قفل", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("پشتیبان‌گیری رمزدار", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (selectedTab == 0) {
                    // Auth Mode Selector
                    Text(
                        text = "روش ورود و احراز هویت:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    AuthModeOptionItem(
                        title = "بدون رمز (غیرفعال)",
                        description = "ورود مستقیم بدون درخواست رمز",
                        icon = Icons.Default.LockOpen,
                        isSelected = currentMode == AuthMode.NONE,
                        onClick = { currentMode = AuthMode.NONE }
                    )

                    AuthModeOptionItem(
                        title = "فقط کد پین (PIN)",
                        description = "ورود با رمز عبور ۴ تا ۶ رقمی",
                        icon = Icons.Default.Key,
                        isSelected = currentMode == AuthMode.PIN,
                        onClick = { currentMode = AuthMode.PIN }
                    )

                    AuthModeOptionItem(
                        title = "فقط اثر انگشت (بیومتریک)",
                        description = if (isBiometricAvailable) "ورود سریع با سنسور اثر انگشت" else "سنسور اثر انگشت در این دستگاه یافت نشد",
                        icon = Icons.Default.Fingerprint,
                        isSelected = currentMode == AuthMode.BIOMETRIC,
                        enabled = isBiometricAvailable,
                        onClick = { currentMode = AuthMode.BIOMETRIC }
                    )

                    AuthModeOptionItem(
                        title = "احراز هویت دو مرحله‌ای (2FA)",
                        description = if (isBiometricAvailable) "بالاترین سطح امنیت: پین + اثر انگشت" else "نیاز به سنسور اثر انگشت دارد",
                        icon = Icons.Default.Security,
                        isSelected = currentMode == AuthMode.TWO_FACTOR,
                        enabled = isBiometricAvailable,
                        onClick = { currentMode = AuthMode.TWO_FACTOR }
                    )

                    // If PIN or 2FA selected, show PIN setup fields
                    if (currentMode == AuthMode.PIN || currentMode == AuthMode.TWO_FACTOR) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (securityPrefs.isPinSet()) "تغییر یا تایید کد پین:" else "تنظیم کد پین جدید:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (pinErrorMessage != null) {
                            Text(
                                text = pinErrorMessage ?: "",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = newPin,
                            onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) newPin = it },
                            label = { Text("کد پین (۴ تا ۶ رقم)") },
                            visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            trailingIcon = {
                                IconButton(onClick = { isPinVisible = !isPinVisible }) {
                                    Icon(
                                        imageVector = if (isPinVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("new_pin_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = confirmPin,
                            onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) confirmPin = it },
                            label = { Text("تکرار کد پین") },
                            visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("confirm_pin_input")
                        )
                    }
                } else {
                    // Tab 1: Encrypted Backup & Restore
                    Text(
                        text = "پشتیبان‌گیری محلی با فرمت رمزنگاری شده:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "اطلاعات کارت‌ها با الگوریتم قدرتمند AES-256-GCM و با رمز انتخابی شما رمزگذاری شده و فایل به صورت امن ذخیره می‌شود.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    // Export Section
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Save, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ذخیره امن فایل پشتیبان در گوشی", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "فایل با پسوند .enc و به شکل کاملاً رمزگذاری شده بدون ارسال به اینترنت یا سایر برنامه‌ها، در حافظه امن گوشی ذخیره می‌شود.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = exportPassword,
                                onValueChange = { exportPassword = it },
                                label = { Text("رمز عبور برای رمزگذاری خروجی") },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth().testTag("export_password_input")
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    if (exportPassword.length < 4) {
                                        backupMessage = "رمز عبور خروجی باید حداقل ۴ کاراکتر باشد"
                                    } else {
                                        onExportBackup(exportPassword)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().testTag("start_export_button")
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ذخیره محلی فایل رمزدار (.enc)")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Import Section
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Upload, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("بازیابی اطلاعات از فایل پشتیبان", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = onSelectBackupFile,
                                modifier = Modifier.fillMaxWidth().testTag("select_backup_file_button")
                            ) {
                                Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (importedFileName != null) "فایل انتخاب شده: $importedFileName" else "انتخاب فایل پشتیبان از حافظه (.enc)")
                            }
                            if (importedFileName != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "فایل $importedFileName با موفقیت بارگذاری شد",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = importPassword,
                                onValueChange = { importPassword = it },
                                label = { Text("رمز عبور فایل پشتیبان") },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth().testTag("import_password_input")
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    if (importDataText.isBlank() || importPassword.isBlank()) {
                                        backupMessage = "لطفاً ابتدا فایل پشتیبان را انتخاب کرده و رمز عبور را وارد کنید"
                                    } else {
                                        onImportBackup(importDataText.trim(), importPassword)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().testTag("start_import_button")
                            ) {
                                Text("رمزگشایی و بازیابی کارت‌ها")
                            }
                        }
                    }

                    if (backupMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = backupMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (currentMode == AuthMode.PIN || currentMode == AuthMode.TWO_FACTOR) {
                        if (!securityPrefs.isPinSet() && newPin.isEmpty()) {
                            pinErrorMessage = "لطفاً یک کد پین تعیین کنید"
                            return@Button
                        }
                        if (newPin.isNotEmpty()) {
                            if (newPin.length < 4) {
                                pinErrorMessage = "کد پین باید حداقل ۴ رقم باشد"
                                return@Button
                            }
                            if (newPin != confirmPin) {
                                pinErrorMessage = "کد پین و تکرار آن یکسان نیستند"
                                return@Button
                            }
                        }
                    }
                    onSaveMode(currentMode, if (newPin.isNotEmpty()) newPin else null)
                },
                modifier = Modifier.testTag("save_security_settings_button")
            ) {
                Text("تایید و اعمال")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("انصراف")
            }
        }
    )
}

@Composable
private fun AuthModeOptionItem(
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }
        }
    }
}
