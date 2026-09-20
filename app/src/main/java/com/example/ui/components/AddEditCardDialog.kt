package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BankCard
import com.example.util.BankUtils

@Composable
fun AddEditCardDialog(
    initialCard: BankCard? = null,
    defaultIsPersonal: Boolean = true,
    onDismiss: () -> Unit,
    onSave: (BankCard) -> Unit
) {
    var bankName by remember { mutableStateOf(initialCard?.bankName ?: "") }
    var holderName by remember { mutableStateOf(initialCard?.holderName ?: "") }
    var cardNumber by remember { mutableStateOf(initialCard?.cardNumber ?: "") }
    var accountNumber by remember { mutableStateOf(initialCard?.accountNumber ?: "") }
    var iban by remember { mutableStateOf(initialCard?.iban ?: "") }
    var cvv2 by remember { mutableStateOf(initialCard?.cvv2 ?: "") }
    var expiryMonth by remember { mutableStateOf(initialCard?.expiryMonth ?: "") }
    var expiryYear by remember { mutableStateOf(initialCard?.expiryYear ?: "") }
    var isPersonal by remember { mutableStateOf(initialCard?.isPersonal ?: defaultIsPersonal) }
    var category by remember { mutableStateOf(initialCard?.category ?: "عادی") }
    var notes by remember { mutableStateOf(initialCard?.notes ?: "") }

    var isCvvVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = listOf("عادی", "اصلی", "کاری", "خرید", "خانواده", "سایر")
    val quickBanks = listOf("بانک ملی", "بانک ملت", "بانک سپه", "بانک تجارت", "بانک سامان", "بانک پاسارگاد", "بلوبانک", "بانک کشاورزی")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialCard == null) "افزودن کارت بانکی جدید" else "ویرایش اطلاعات کارت",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (errorMessage != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                // Ownership Segmented Toggle: کارت من / کارت دیگران
                Text(
                    text = "نوع مالکیت کارت:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    SegmentedButton(
                        selected = isPersonal,
                        onClick = { isPersonal = true },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Text("کارت شخصی من")
                    }
                    SegmentedButton(
                        selected = !isPersonal,
                        onClick = { isPersonal = false },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Text("کارت دیگران")
                    }
                }

                // 1. Card Number (16 digits) with auto-bank detection
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { input ->
                        val digits = BankUtils.normalizeDigits(input).filter { it.isDigit() }.take(16)
                        cardNumber = digits
                        // Auto detect bank name if empty or default
                        if (digits.length >= 6) {
                            val detected = BankUtils.detectBankFromCardNumber(digits)
                            if (detected.code != "000000") {
                                bankName = detected.name
                            }
                        }
                    },
                    label = { Text("شماره کارت (۱۶ رقمی)") },
                    leadingIcon = {
                        Icon(Icons.Default.CreditCard, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_number_input")
                )
                if (cardNumber.isNotEmpty()) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text(
                            text = BankUtils.formatCardNumber(cardNumber),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            style = androidx.compose.ui.text.TextStyle(
                                textDirection = androidx.compose.ui.text.style.TextDirection.Ltr
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 2. Bank Name
                OutlinedTextField(
                    value = bankName,
                    onValueChange = { bankName = it },
                    label = { Text("نام بانک") },
                    leadingIcon = {
                        Icon(Icons.Default.AccountBalance, contentDescription = null)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bank_name_input")
                )

                // Quick bank selection chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickBanks.forEach { bName ->
                        FilterChip(
                            selected = bankName == bName,
                            onClick = { bankName = bName },
                            label = { Text(bName, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 3. Holder Name
                OutlinedTextField(
                    value = holderName,
                    onValueChange = { holderName = it },
                    label = { Text("نام دارنده حساب") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("holder_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 4. CVV2 with Eye toggle icon
                OutlinedTextField(
                    value = cvv2,
                    onValueChange = { input ->
                        cvv2 = BankUtils.normalizeDigits(input).filter { it.isDigit() }.take(4)
                    },
                    label = { Text("CVV2") },
                    leadingIcon = {
                        Icon(Icons.Default.Security, contentDescription = null)
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { isCvvVisible = !isCvvVisible },
                            modifier = Modifier.testTag("dialog_cvv_eye_button")
                        ) {
                            Icon(
                                imageVector = if (isCvvVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "نمایش / مخفی‌سازی CVV2"
                            )
                        }
                    },
                    visualTransformation = if (isCvvVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cvv2_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 5. Expiration Date (Month / Year)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = expiryMonth,
                        onValueChange = { input ->
                            val clean = BankUtils.normalizeDigits(input).filter { it.isDigit() }.take(2)
                            expiryMonth = clean
                        },
                        label = { Text("ماه انقضا (MM)") },
                        leadingIcon = {
                            Icon(Icons.Default.DateRange, contentDescription = null)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("expiry_month_input")
                    )

                    OutlinedTextField(
                        value = expiryYear,
                        onValueChange = { input ->
                            val clean = BankUtils.normalizeDigits(input).filter { it.isDigit() }.take(2)
                            expiryYear = clean
                        },
                        label = { Text("سال انقضا (YY)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("expiry_year_input")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 6. Account Number (شماره حساب)
                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { input ->
                        accountNumber = BankUtils.normalizeDigits(input).filter { it.isDigit() || it == '-' }
                    },
                    label = { Text("شماره حساب (اختیاری)") },
                    leadingIcon = {
                        Icon(Icons.Default.Numbers, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("account_number_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 7. IBAN / Sheba (شماره شبا)
                OutlinedTextField(
                    value = iban,
                    onValueChange = { input ->
                        val norm = BankUtils.normalizeDigits(input).uppercase().filter { it.isLetterOrDigit() }
                        iban = if (!norm.startsWith("IR") && norm.isNotEmpty()) "IR$norm" else norm
                    },
                    label = { Text("شماره شبا (با IR)") },
                    placeholder = { Text("IR000000000000000000000000") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("iban_input")
                )
                if (iban.isNotEmpty()) {
                    Text(
                        text = BankUtils.formatIban(iban),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 8. Category Selector
                Text(
                    text = "دسته‌بندی کارت:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 9. Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("یادداشت (اختیاری)") },
                    leadingIcon = {
                        Icon(Icons.Default.Notes, contentDescription = null)
                    },
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("notes_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (bankName.isBlank()) {
                        errorMessage = "لطفاً نام بانک را وارد کنید"
                        return@Button
                    }
                    if (holderName.isBlank()) {
                        errorMessage = "لطفاً نام دارنده حساب را وارد کنید"
                        return@Button
                    }
                    if (cardNumber.length < 16) {
                        errorMessage = "شماره کارت باید ۱۶ رقم باشد"
                        return@Button
                    }

                    val updatedCard = (initialCard ?: BankCard(
                        bankName = bankName,
                        holderName = holderName,
                        cardNumber = cardNumber
                    )).copy(
                        bankName = bankName.trim(),
                        holderName = holderName.trim(),
                        cardNumber = cardNumber.trim(),
                        accountNumber = accountNumber.trim(),
                        iban = iban.trim(),
                        cvv2 = cvv2.trim(),
                        expiryMonth = expiryMonth.trim(),
                        expiryYear = expiryYear.trim(),
                        isPersonal = isPersonal,
                        category = category,
                        notes = notes.trim()
                    )
                    onSave(updatedCard)
                },
                modifier = Modifier.testTag("save_card_button")
            ) {
                Text("ذخیره کارت")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_card_button")
            ) {
                Text("انصراف")
            }
        }
    )
}
