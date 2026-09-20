package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BankCard
import com.example.util.BankUtils

@Composable
fun BankCardItem(
    card: BankCard,
    onEdit: (BankCard) -> Unit,
    onDelete: (BankCard) -> Unit,
    onCopy: (label: String, value: String) -> Unit,
    onShare: (BankCard) -> Unit,
    modifier: Modifier = Modifier
) {
    var isCvvRevealed by remember { mutableStateOf(false) }
    var isExpandedDetails by remember { mutableStateOf(false) }

    val bankInfo = remember(card.cardNumber, card.bankName) {
        val detected = BankUtils.detectBankFromCardNumber(card.cardNumber)
        if (detected.code != "000000") detected else BankUtils.getBankByName(card.bankName)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Physical Card Visual Surface
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                bankInfo.primaryColor,
                                bankInfo.secondaryColor.copy(alpha = 0.95f),
                                bankInfo.primaryColor.copy(alpha = 0.85f)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                // Background decorative pattern
                Canvas(modifier = Modifier.matchParentSize()) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.05f),
                        radius = size.width * 0.45f,
                        center = Offset(size.width * 0.9f, size.height * 0.1f)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.04f),
                        radius = size.width * 0.35f,
                        center = Offset(size.width * 0.1f, size.height * 0.9f)
                    )
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header: Bank Name & Category Tag
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.CreditCard,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = card.bankName.ifEmpty { bankInfo.name },
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (card.category.isNotEmpty()) {
                                    Text(
                                        text = card.category,
                                        color = Color.White.copy(alpha = 0.75f),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        // Ownership badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (card.isPersonal) Color(0xFF10B981).copy(alpha = 0.25f) else Color(0xFF3B82F6).copy(alpha = 0.25f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (card.isPersonal) Color(0xFF10B981) else Color(0xFF60A5FA)
                            )
                        ) {
                            Text(
                                text = if (card.isPersonal) "کارت من" else "کارت دیگران",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Chip & Contactless indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Gold EMV Chip
                        Box(
                            modifier = Modifier
                                .width(42.dp)
                                .height(30.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Color(0xFFFFDF79), Color(0xFFD4AF37), Color(0xFFECC440))
                                    )
                                )
                                .border(1.dp, Color(0xFFB8860B), RoundedCornerShape(6.dp))
                        ) {
                            // Chip lines
                            Canvas(modifier = Modifier.matchParentSize()) {
                                drawLine(
                                    color = Color(0xFF8B6508),
                                    start = Offset(size.width * 0.35f, 0f),
                                    end = Offset(size.width * 0.35f, size.height),
                                    strokeWidth = 1f
                                )
                                drawLine(
                                    color = Color(0xFF8B6508),
                                    start = Offset(size.width * 0.65f, 0f),
                                    end = Offset(size.width * 0.65f, size.height),
                                    strokeWidth = 1f
                                )
                                drawLine(
                                    color = Color(0xFF8B6508),
                                    start = Offset(0f, size.height * 0.5f),
                                    end = Offset(size.width, size.height * 0.5f),
                                    strokeWidth = 1f
                                )
                            }
                        }

                        // CVV2 with Eye toggle button inside the card visual
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.28f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "CVV2: ",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isCvvRevealed) card.cvv2.ifEmpty { "---" } else if (card.cvv2.isNotEmpty()) "••••" else "---",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            // The Eye button requested by user
                            IconButton(
                                onClick = { isCvvRevealed = !isCvvRevealed },
                                modifier = Modifier
                                    .size(26.dp)
                                    .testTag("cvv_toggle_button")
                            ) {
                                Icon(
                                    imageVector = if (isCvvRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "نمایش یا پنهان‌سازی CVV2",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 16-digit Card Number Display (Strict LTR 4x4 layout: e.g. 1705 6061 1828 0062)
                    val cardChunks = BankUtils.getCardNumberChunks(card.cardNumber)
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            cardChunks.forEachIndexed { index, chunk ->
                                if (index > 0) {
                                    Spacer(modifier = Modifier.width(12.dp))
                                }
                                Text(
                                    text = chunk,
                                    color = Color.White,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.5.sp,
                                    style = androidx.compose.ui.text.TextStyle(
                                        textDirection = androidx.compose.ui.text.style.TextDirection.Ltr
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Bottom Row: Holder Name & Expiration Date
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "دارنده کارت",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 10.sp
                            )
                            Text(
                                text = card.holderName.ifEmpty { "---" },
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "انقضا",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 10.sp
                            )
                            val expDate = when {
                                card.expiryMonth.isNotEmpty() && card.expiryYear.isNotEmpty() ->
                                    "${card.expiryMonth} / ${card.expiryYear}"
                                card.expiryMonth.isNotEmpty() -> card.expiryMonth
                                else -> "-- / --"
                            }
                            Text(
                                text = expDate,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Action Bar & Quick Copy Buttons Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                // Quick Copy Buttons Row (شماره کارت، شماره حساب، شماره شبا)
                // Equal width and balanced distribution
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Copy Card Number Button
                    CopyChipButton(
                        label = "شماره کارت",
                        value = card.cardNumber,
                        testTag = "copy_card_number_${card.id}",
                        modifier = Modifier.weight(1f),
                        onClick = { onCopy("شماره کارت", card.cardNumber) }
                    )

                    // Copy Account Number Button
                    if (card.accountNumber.isNotEmpty()) {
                        CopyChipButton(
                            label = "شماره حساب",
                            value = card.accountNumber,
                            testTag = "copy_account_number_${card.id}",
                            modifier = Modifier.weight(1f),
                            onClick = { onCopy("شماره حساب", card.accountNumber) }
                        )
                    }

                    // Copy IBAN Button
                    if (card.iban.isNotEmpty()) {
                        CopyChipButton(
                            label = "شماره شبا",
                            value = card.iban,
                            testTag = "copy_iban_${card.id}",
                            modifier = Modifier.weight(1f),
                            onClick = { onCopy("شماره شبا", card.iban) }
                        )
                    }
                }

                // Expandable Account & IBAN Details section
                if (card.accountNumber.isNotEmpty() || card.iban.isNotEmpty() || card.notes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExpandedDetails = !isExpandedDetails }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isExpandedDetails) "بستن مشخصات حساب و شبا ▲" else "مشاهده جزئیات حساب و شبا ▼",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    AnimatedVisibility(visible = isExpandedDetails) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(10.dp)
                        ) {
                            if (card.accountNumber.isNotEmpty()) {
                                DetailRowItem(
                                    label = "شماره حساب:",
                                    value = card.accountNumber,
                                    onCopy = { onCopy("شماره حساب", card.accountNumber) }
                                )
                            }
                            if (card.iban.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                DetailRowItem(
                                    label = "شماره شبا:",
                                    value = BankUtils.formatIban(card.iban),
                                    onCopy = { onCopy("شماره شبا", card.iban) }
                                )
                            }
                            if (card.notes.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row {
                                    Text(
                                        text = "یادداشت: ",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = card.notes,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                // Bottom card tools: Edit, Delete, Share
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Share button (safe share: name, bank, card number, sheba)
                    IconButton(
                        onClick = { onShare(card) },
                        modifier = Modifier.size(36.dp).testTag("share_card_${card.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "اشتراک‌گذاری اطلاعات کارت",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Edit button
                    IconButton(
                        onClick = { onEdit(card) },
                        modifier = Modifier.size(36.dp).testTag("edit_card_${card.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "ویرایش کارت",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Delete button
                    IconButton(
                        onClick = { onDelete(card) },
                        modifier = Modifier.size(36.dp).testTag("delete_card_${card.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف کارت",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CopyChipButton(
    label: String,
    value: String,
    testTag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
        modifier = modifier.testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 7.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ContentCopy,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "کپی $label",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun DetailRowItem(
    label: String,
    value: String,
    onCopy: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(
            onClick = onCopy,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ContentCopy,
                contentDescription = "کپی",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
