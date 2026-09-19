package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BankCard
import com.example.security.AuthMode
import com.example.ui.components.AddEditCardDialog
import com.example.ui.components.BankCardItem
import com.example.ui.components.SecuritySettingsDialog
import com.example.util.BankUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: CardViewModel,
    isBiometricAvailable: Boolean,
    onLockClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val displayedCards by viewModel.displayedCards.collectAsState()
    val isPersonalTab by viewModel.isPersonalTab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val totalPersonalCount by viewModel.totalPersonalCount.collectAsState()
    val totalOthersCount by viewModel.totalOthersCount.collectAsState()
    val authMode by viewModel.authMode.collectAsState()

    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingCard by remember { mutableStateOf<BankCard?>(null) }
    var cardToDelete by remember { mutableStateOf<BankCard?>(null) }
    var showSecuritySettingsDialog by remember { mutableStateOf(false) }

    // File selection state for secure backup
    var pendingExportData by remember { mutableStateOf<String?>(null) }
    var importedFileContent by remember { mutableStateOf<String?>(null) }
    var importedFileName by remember { mutableStateOf<String?>(null) }

    // System file picker for creating/saving secure backup file locally
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri: Uri? ->
        if (uri != null && pendingExportData != null) {
            coroutineScope.launch {
                try {
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(pendingExportData!!.toByteArray(Charsets.UTF_8))
                        outputStream.flush()
                    }
                    pendingExportData = null
                    snackbarHostState.showSnackbar("فایل پشتیبان با موفقیت و محرمانگی کامل در گوشی ذخیره شد")
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("خطا در ذخیره فایل: ${e.localizedMessage}")
                }
            }
        }
    }

    // System file picker for importing secure backup file from device storage
    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                try {
                    var displayName = "فایل پشتیبان"
                    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex != -1 && cursor.moveToFirst()) {
                            displayName = cursor.getString(nameIndex)
                        }
                    }
                    val content = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        inputStream.bufferedReader(Charsets.UTF_8).readText()
                    }
                    if (!content.isNullOrBlank()) {
                        importedFileContent = content
                        importedFileName = displayName
                        snackbarHostState.showSnackbar("فایل $displayName آماده رمزگشایی است")
                    } else {
                        snackbarHostState.showSnackbar("فایل انتخاب شده خالی است")
                    }
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("خطا در خواندن فایل پشتیبان: ${e.localizedMessage}")
                }
            }
        }
    }

    val categories = listOf("همه", "اصلی", "کاری", "خرید", "خانواده", "پس‌انداز", "سایر")

    // Helper for copying to clipboard
    val copyToClipboard: (String, String) -> Unit = { label, value ->
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, value)
        clipboard.setPrimaryClip(clip)
        coroutineScope.launch {
            snackbarHostState.showSnackbar("$label با موفقیت در حافظه کپی شد")
        }
    }

    // Helper for safe sharing
    val shareCard: (BankCard) -> Unit = { card ->
        val shareText = buildString {
            appendLine("اطلاعات کارت بانکی:")
            appendLine("بانک: ${card.bankName}")
            appendLine("به نام: ${card.holderName}")
            appendLine("شماره کارت: ${BankUtils.formatCardNumber(card.cardNumber)}")
            if (card.accountNumber.isNotEmpty()) {
                appendLine("شماره حساب: ${card.accountNumber}")
            }
            if (card.iban.isNotEmpty()) {
                appendLine("شماره شبا: ${BankUtils.formatIban(card.iban)}")
            }
        }
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "ارسال مشخصات کارت")
        context.startActivity(shareIntent)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "کیف کارت امن",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "آفلاین • رمزنگاری سخت‌افزاری",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    // Security / 2FA status indicator button
                    IconButton(
                        onClick = { showSecuritySettingsDialog = true },
                        modifier = Modifier.testTag("security_settings_topbar_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (authMode != AuthMode.NONE) {
                                    Badge(containerColor = Color(0xFF10B981)) {
                                        Text("امن", fontSize = 9.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "تنظیمات امنیت و رمزنگاری",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Lock button if security is configured
                    if (authMode != AuthMode.NONE) {
                        IconButton(
                            onClick = onLockClicked,
                            modifier = Modifier.testTag("lock_app_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "قفل فوری برنامه",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.shadow(1.dp)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    editingCard = null
                    showAddEditDialog = true
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("افزودن کارت جدید", fontWeight = FontWeight.SemiBold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_card_fab")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Two-Section Tabs: "کارت‌های شخصی من" vs "کارت‌های دیگران" (Modern Pill Bar)
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 1.dp
            ) {
                TabRow(
                    selectedTabIndex = if (isPersonalTab) 0 else 1,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = {},
                    divider = {},
                    modifier = Modifier.padding(4.dp)
                ) {
                    Tab(
                        selected = isPersonalTab,
                        onClick = { viewModel.onTabChanged(true) },
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isPersonalTab) MaterialTheme.colorScheme.surface
                                else Color.Transparent
                            )
                            .testTag("my_cards_tab")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                modifier = Modifier.size(17.dp),
                                tint = if (isPersonalTab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "کارت‌های من ($totalPersonalCount)",
                                fontWeight = if (isPersonalTab) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (isPersonalTab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Tab(
                        selected = !isPersonalTab,
                        onClick = { viewModel.onTabChanged(false) },
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (!isPersonalTab) MaterialTheme.colorScheme.surface
                                else Color.Transparent
                            )
                            .testTag("others_cards_tab")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(17.dp),
                                tint = if (!isPersonalTab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "کارت‌های دیگران ($totalOthersCount)",
                                fontWeight = if (!isPersonalTab) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (!isPersonalTab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Quick Search Bar (reduced width & centered for elegant layout)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = {
                        Text(
                            text = "جستجوی سریع",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "جستجو",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Text("✕", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .testTag("search_card_input")
                )
            }

            // Category Horizontal Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { viewModel.onCategorySelected(category) },
                        label = { Text(category, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            // Card List Content
            if (displayedCards.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.CreditCard,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "کارتی با این مشخصات یافت نشد" else if (isPersonalTab) "هنوز کارت شخصی ثبت نشده است" else "هنوز کارت دیگری ثبت نشده است",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "برای افزودن، دکمه 'افزودن کارت جدید' را لمس کنید",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 88.dp)
                ) {
                    items(
                        items = displayedCards,
                        key = { it.id }
                    ) { card ->
                        BankCardItem(
                            card = card,
                            onEdit = {
                                editingCard = it
                                showAddEditDialog = true
                            },
                            onDelete = { cardToDelete = it },
                            onCopy = copyToClipboard,
                            onShare = shareCard
                        )
                    }
                }
            }
        }
    }

    // Add / Edit Card Dialog
    if (showAddEditDialog) {
        AddEditCardDialog(
            initialCard = editingCard,
            defaultIsPersonal = isPersonalTab,
            onDismiss = {
                showAddEditDialog = false
                editingCard = null
            },
            onSave = { updatedCard ->
                if (editingCard == null) {
                    viewModel.addCard(updatedCard)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("کارت جدید با موفقیت ذخیره شد")
                    }
                } else {
                    viewModel.updateCard(updatedCard)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("اطلاعات کارت به‌روزرسانی شد")
                    }
                }
                showAddEditDialog = false
                editingCard = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (cardToDelete != null) {
        val card = cardToDelete!!
        AlertDialog(
            onDismissRequest = { cardToDelete = null },
            title = { Text("حذف کامل کارت بانکی", fontWeight = FontWeight.Bold) },
            text = {
                Text("آیا از حذف کارت '${card.bankName}' به نام '${card.holderName}' اطمینان دارید؟ این عملیات غیرقابل بازگشت است.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCard(card)
                        cardToDelete = null
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("کارت با موفقیت حذف گردید")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_delete_card_button")
                ) {
                    Text("حذف دائمی")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { cardToDelete = null }) {
                    Text("انصراف")
                }
            }
        )
    }

    // Security Settings & Encrypted Backup Dialog
    if (showSecuritySettingsDialog) {
        SecuritySettingsDialog(
            securityPrefs = viewModel.securityPrefs,
            isBiometricAvailable = isBiometricAvailable,
            onDismiss = {
                showSecuritySettingsDialog = false
                importedFileContent = null
                importedFileName = null
            },
            onSaveMode = { mode, newPin ->
                viewModel.updateSecuritySettings(mode, newPin)
                showSecuritySettingsDialog = false
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("تنظیمات امنیت با موفقیت ذخیره شد")
                }
            },
            onExportBackup = { password ->
                coroutineScope.launch {
                    try {
                        val encryptedBackup = viewModel.exportEncryptedBackup(password)
                        pendingExportData = encryptedBackup
                        // Also automatically save a backup to internal private app storage as an extra safety layer
                        val internalFileName = "cards_backup_${System.currentTimeMillis()}.enc"
                        context.openFileOutput(internalFileName, Context.MODE_PRIVATE).use { output ->
                            output.write(encryptedBackup.toByteArray(Charsets.UTF_8))
                        }
                        // Launch system file saver so user chooses their desired secure local storage location
                        val defaultFileName = "bank_cards_backup_${System.currentTimeMillis()}.enc"
                        createDocumentLauncher.launch(defaultFileName)
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar("خطا در آماده‌سازی پشتیبان: ${e.localizedMessage}")
                    }
                }
            },
            onSelectBackupFile = {
                openDocumentLauncher.launch(arrayOf("*/*"))
            },
            onImportBackup = { backupJson, password ->
                coroutineScope.launch {
                    val result = viewModel.importEncryptedBackup(backupJson, password)
                    if (result.isSuccess) {
                        val count = result.getOrNull() ?: 0
                        showSecuritySettingsDialog = false
                        importedFileContent = null
                        importedFileName = null
                        snackbarHostState.showSnackbar("$count کارت با موفقیت بازیابی شد")
                    } else {
                        val err = result.exceptionOrNull()?.localizedMessage ?: "رمز عبور اشتباه است یا فایل دستکاری شده"
                        snackbarHostState.showSnackbar("خطا در بازیابی: $err")
                    }
                }
            },
            importedFileContent = importedFileContent,
            importedFileName = importedFileName
        )
    }
}
