package com.example.util

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class BankInfo(
    val code: String,
    val name: String,
    val shortName: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val textColor: Color = Color.White
)

object BankUtils {

    val predefinedBanks = listOf(
        BankInfo("603799", "بانک ملی ایران", "ملی", Color(0xFF132F4C), Color(0xFF1E4976)),
        BankInfo("610433", "بانک ملت", "ملت", Color(0xFF9E1B2C), Color(0xFFCF283F)),
        BankInfo("589210", "بانک سپه", "سپه", Color(0xFF1E3A2F), Color(0xFF2E5D4B)),
        BankInfo("627353", "بانک تجارت", "تجارت", Color(0xFF0F3B66), Color(0xFF1D5A96)),
        BankInfo("621986", "بانک سامان", "سامان", Color(0xFF0277BD), Color(0xFF4FC3F7)),
        BankInfo("502229", "بانک پاسارگاد", "پاسارگاد", Color(0xFF1A1A1A), Color(0xFFB8860B)),
        BankInfo("622106", "بانک پارسیان", "پارسیان", Color(0xFF8B2500), Color(0xFFD35400)),
        BankInfo("603770", "بانک کشاورزی", "کشاورزی", Color(0xFF1B5E20), Color(0xFF4CAF50)),
        BankInfo("627412", "بانک اقتصاد نوین", "اقتصاد نوین", Color(0xFF37474F), Color(0xFF546E7A)),
        BankInfo("639346", "بانک سینا", "سینا", Color(0xFF1565C0), Color(0xFF42A5F5)),
        BankInfo("627488", "بانک کارآفرین", "کارآفرین", Color(0xFF263238), Color(0xFF455A64)),
        BankInfo("502908", "بانک توسعه تعاون", "توسعه تعاون", Color(0xFF004D40), Color(0xFF00897B)),
        BankInfo("502938", "بانک دی", "دی", Color(0xFF3E2723), Color(0xFF6D4C41)),
        BankInfo("639607", "بانک سرمایه", "سرمایه", Color(0xFF0D47A1), Color(0xFF1976D2)),
        BankInfo("505785", "بانک ایران زمین", "ایران زمین", Color(0xFF4A148C), Color(0xFF7B1FA2)),
        BankInfo("636214", "بانک آینده", "آینده", Color(0xFF5D4037), Color(0xFF8D6E63)),
        BankInfo("505416", "بانک گردشگری", "گردشگری", Color(0xFF880E4F), Color(0xFFC2185B)),
        BankInfo("606373", "بانک مهر ایران", "مهر ایران", Color(0xFF004D40), Color(0xFF00796B)),
        BankInfo("505801", "بانک رسالت", "رسالت", Color(0xFF1B5E20), Color(0xFF2E7D32)),
        BankInfo("861980", "بلوبانک", "بلو", Color(0xFF0052FF), Color(0xFF387BFF)),
        BankInfo("504706", "بانک شهر", "شهر", Color(0xFFB71C1C), Color(0xFFE53935)),
        BankInfo("628023", "بانک مسکن", "مسکن", Color(0xFFE65100), Color(0xFFF57C00)),
        BankInfo("589463", "بانک رفاه کارگران", "رفاه", Color(0xFF0D47A1), Color(0xFF1E88E5)),
        BankInfo("627760", "پست بانک ایران", "پست بانک", Color(0xFF2E7D32), Color(0xFF4CAF50))
    )

    private val defaultBank = BankInfo("000000", "کارت بانکی", "بانک", Color(0xFF1A237E), Color(0xFF283593))

    fun detectBankFromCardNumber(number: String): BankInfo {
        val cleanNumber = normalizeDigits(number).filter { it.isDigit() }
        if (cleanNumber.length >= 6) {
            val bin = cleanNumber.take(6)
            return predefinedBanks.firstOrNull { it.code == bin } ?: defaultBank
        }
        return defaultBank
    }

    fun getBankByName(name: String): BankInfo {
        return predefinedBanks.firstOrNull { it.name.contains(name) || name.contains(it.name) || name.contains(it.shortName) }
            ?: defaultBank
    }

    /**
     * Converts Persian and Arabic digits to standard ASCII digits (0-9)
     */
    fun normalizeDigits(input: String): String {
        return input
            .replace('۰', '0').replace('۱', '1').replace('۲', '2').replace('۳', '3').replace('۴', '4')
            .replace('۵', '5').replace('۶', '6').replace('۷', '7').replace('۸', '8').replace('۹', '9')
            .replace('٠', '0').replace('١', '1').replace('٢', '2').replace('٣', '3').replace('٤', '4')
            .replace('٥', '5').replace('٦', '6').replace('٧', '7').replace('٨', '8').replace('٩', '9')
    }

    /**
     * Splits 16-digit card number into 4 groups (e.g. ["1705", "6061", "1828", "0062"])
     * Always returns 4 chunks for consistent LTR UI rendering.
     */
    fun getCardNumberChunks(rawNumber: String): List<String> {
        val digits = normalizeDigits(rawNumber).filter { it.isDigit() }.take(16)
        if (digits.isEmpty()) return listOf("----", "----", "----", "----")
        val chunks = digits.chunked(4).toMutableList()
        while (chunks.size < 4) {
            chunks.add("----")
        }
        return chunks
    }

    /**
     * Formats 16 digits into: 1705 - 6061 - 1828 - 0062
     * Protected with LRM (\u200E) around delimiters so that in RTL environments (Persian/Arabic)
     * and when sharing via messengers, the chunks are strictly ordered from left to right:
     * e.g. 1705 - 6061 - 1828 - 0062 and NEVER reversed to 0062 - 1828 - 6061 - 1705.
     */
    fun formatCardNumber(rawNumber: String): String {
        val digits = normalizeDigits(rawNumber).filter { it.isDigit() }.take(16)
        if (digits.isEmpty()) return ""
        val chunks = digits.chunked(4)
        return "\u200E" + chunks.joinToString("\u200E - \u200E") + "\u200E"
    }

    /**
     * Plain format without invisible LTR control characters
     */
    fun formatCardNumberPlain(rawNumber: String): String {
        val digits = normalizeDigits(rawNumber).filter { it.isDigit() }.take(16)
        if (digits.isEmpty()) return ""
        return digits.chunked(4).joinToString(" - ")
    }

    /**
     * Formats IBAN / Sheba: IR12 3456 7890 1234 5678 9012 34
     * Protected with LRM to prevent visual flipping in Persian RTL context.
     */
    fun formatIban(rawIban: String): String {
        var clean = normalizeDigits(rawIban).uppercase().filter { it.isLetterOrDigit() }
        if (clean.startsWith("IR")) {
            clean = clean.removePrefix("IR")
        }
        val chunks = clean.take(24).chunked(4)
        return if (chunks.isNotEmpty()) "\u200EIR " + chunks.joinToString(" ") + "\u200E" else ""
    }

    fun getBankGradient(bankInfo: BankInfo): Brush {
        return Brush.linearGradient(
            colors = listOf(bankInfo.primaryColor, bankInfo.secondaryColor)
        )
    }
}
