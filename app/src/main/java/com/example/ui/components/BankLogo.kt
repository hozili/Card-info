package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.util.BankInfo
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Renders the authentic bank logo badge for Iranian banks.
 */
@Composable
fun BankLogoBadge(
    bankInfo: BankInfo,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    elevation: Dp = 2.dp
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        shadowElevation = elevation,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.8f)),
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(10.dp))
    ) {
        Box(
            modifier = Modifier
                .padding(3.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            bankInfo.primaryColor,
                            bankInfo.secondaryColor
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            BankLogoEmblem(
                bankCode = bankInfo.code,
                modifier = Modifier
                    .size(size * 0.72f)
                    .padding(2.dp),
                tint = Color.White
            )
        }
    }
}

/**
 * Large subtle watermark emblem rendered on the background of the bank card.
 */
@Composable
fun BankLogoWatermark(
    bankInfo: BankInfo,
    modifier: Modifier = Modifier,
    alpha: Float = 0.10f
) {
    BankLogoEmblem(
        bankCode = bankInfo.code,
        modifier = modifier,
        tint = Color.White.copy(alpha = alpha)
    )
}

/**
 * Compact circular logo for chips and lists
 */
@Composable
fun BankLogoMini(
    bankInfo: BankInfo,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(bankInfo.primaryColor),
        contentAlignment = Alignment.Center
    ) {
        BankLogoEmblem(
            bankCode = bankInfo.code,
            modifier = Modifier.size(size * 0.75f),
            tint = Color.White
        )
    }
}

/**
 * Draws the vector emblem geometry corresponding to each bank code.
 */
@Composable
fun BankLogoEmblem(
    bankCode: String,
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val radius = minOf(w, h) / 2f

        when (bankCode) {
            // بانک ملی ایران (Melli) - Iconic arch and sunburst crest
            "603799" -> drawMelliLogo(tint, cx, cy, radius)

            // بانک ملت (Mellat) - Iconic geometric loop & square knot
            "610433" -> drawMellatLogo(tint, cx, cy, radius)

            // بانک سپه (Sepah) - Octagonal shield with laurel & central sword/helm
            "589210" -> drawSepahLogo(tint, cx, cy, radius)

            // بانک تجارت (Tejarat) - Iconic stylized sailing ship
            "627353" -> drawTejaratLogo(tint, cx, cy, radius)

            // بانک سامان (Saman) - Iconic dual vortex swirl
            "621986" -> drawSamanLogo(tint, cx, cy, radius)

            // بانک پاسارگاد (Pasargad) - Persepolis Achaemenid winged capital
            "502229" -> drawPasargadLogo(tint, cx, cy, radius)

            // بانک پارسیان (Parsian) - Persepolis 12-petal lotus rosette
            "622106" -> drawParsianLogo(tint, cx, cy, radius)

            // بانک کشاورزی (Keshavarzi) - Golden wheat ears and gear
            "603770" -> drawKeshavarziLogo(tint, cx, cy, radius)

            // بلوبانک (Blu) - Minimalist modern lowercase 'b' emblem
            "861980" -> drawBluLogo(tint, cx, cy, radius)

            // بانک شهر (Shahr) - Shield with stylized city gateway and wings
            "504706" -> drawShahrLogo(tint, cx, cy, radius)

            // بانک مسکن (Maskan) - Shelter roof with key
            "628023" -> drawMaskanLogo(tint, cx, cy, radius)

            // بانک رفاه کارگران (Refah) - Hand and cogwheel flower
            "589463" -> drawRefahLogo(tint, cx, cy, radius)

            // بانک اقتصاد نوین (Eghtesad Novin) - Geometric EN monogram
            "627412" -> drawEnLogo(tint, cx, cy, radius)

            // بانک سینا (Sina) - 8-pointed star medallion
            "639346" -> drawSinaLogo(tint, cx, cy, radius)

            // بانک کارآفرین (Karafarin) - Diamond with seedling sprout
            "627488" -> drawKarafarinLogo(tint, cx, cy, radius)

            // بانک آینده (Ayandeh) - Geometric interlocking infinity triangle
            "636214" -> drawAyandehLogo(tint, cx, cy, radius)

            // بانک قرض‌الحسنه مهر ایران (Mehr) - Protective hands forming heart sprout
            "606373" -> drawMehrLogo(tint, cx, cy, radius)

            // بانک قرض‌الحسنه رسالت (Resalat) - Sunrise and golden wheat
            "505801" -> drawResalatLogo(tint, cx, cy, radius)

            // بانک گردشگری (Gardeshgari) - Compass star
            "505416" -> drawGardeshgariLogo(tint, cx, cy, radius)

            // بانک دی (Dey) - Sunburst flower
            "502938" -> drawDeyLogo(tint, cx, cy, radius)

            // پست بانک (Post Bank) - Post horn & speed wing
            "627760" -> drawPostBankLogo(tint, cx, cy, radius)

            // بانک توسعه تعاون (Tose'e Ta'avon) - Cooperative loop
            "502908" -> drawTaavonLogo(tint, cx, cy, radius)

            // بانک ایران زمین (Iran Zamin) - Ring and cedar tree
            "505785" -> drawIranZaminLogo(tint, cx, cy, radius)

            // بانک سرمایه (Sarmayeh) - Double S swoop
            "639607" -> drawSarmayehLogo(tint, cx, cy, radius)

            // Default card emblem
            else -> drawDefaultCardLogo(tint, cx, cy, radius)
        }
    }
}

/* -------------------------------------------------------------
 * Precise vector drawing functions for Iranian bank logos
 * ------------------------------------------------------------- */

// 1. بانک ملی ایران (Melli)
private fun DrawScope.drawMelliLogo(color: Color, cx: Float, cy: Float, r: Float) {
    val strokeWidth = r * 0.12f
    // Outer circular crest
    drawCircle(
        color = color,
        radius = r * 0.88f,
        center = Offset(cx, cy),
        style = Stroke(width = strokeWidth)
    )
    // Central arch
    val archPath = Path().apply {
        moveTo(cx - r * 0.45f, cy + r * 0.5f)
        lineTo(cx - r * 0.45f, cy - r * 0.1f)
        cubicTo(
            cx - r * 0.45f, cy - r * 0.6f,
            cx + r * 0.45f, cy - r * 0.6f,
            cx + r * 0.45f, cy - r * 0.1f
        )
        lineTo(cx + r * 0.45f, cy + r * 0.5f)
    }
    drawPath(archPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))

    // Inner crown/sun rays
    for (i in -2..2) {
        val angle = (90 + i * 22) * (PI / 180f)
        val startR = r * 0.15f
        val endR = r * 0.42f
        drawLine(
            color = color,
            start = Offset(cx + cos(angle).toFloat() * startR, (cy - r * 0.05f) - sin(angle).toFloat() * startR),
            end = Offset(cx + cos(angle).toFloat() * endR, (cy - r * 0.05f) - sin(angle).toFloat() * endR),
            strokeWidth = strokeWidth * 0.85f,
            cap = StrokeCap.Round
        )
    }
    // Base pedestal
    drawLine(
        color = color,
        start = Offset(cx - r * 0.55f, cy + r * 0.5f),
        end = Offset(cx + r * 0.55f, cy + r * 0.5f),
        strokeWidth = strokeWidth * 1.2f,
        cap = StrokeCap.Round
    )
}

// 2. بانک ملت (Mellat)
private fun DrawScope.drawMellatLogo(color: Color, cx: Float, cy: Float, r: Float) {
    val stroke = r * 0.16f
    // Mellat 4-ring interconnected geometric loops
    val offsets = listOf(
        Offset(cx - r * 0.32f, cy - r * 0.32f),
        Offset(cx + r * 0.32f, cy - r * 0.32f),
        Offset(cx + r * 0.32f, cy + r * 0.32f),
        Offset(cx - r * 0.32f, cy + r * 0.32f)
    )
    val ringR = r * 0.42f
    offsets.forEach { center ->
        drawCircle(
            color = color,
            radius = ringR,
            center = center,
            style = Stroke(width = stroke)
        )
    }
    // Center diamond knot
    val centerDiamond = Path().apply {
        moveTo(cx, cy - r * 0.22f)
        lineTo(cx + r * 0.22f, cy)
        lineTo(cx, cy + r * 0.22f)
        lineTo(cx - r * 0.22f, cy)
        close()
    }
    drawPath(centerDiamond, color = color, style = Fill)
}

// 3. بانک سپه (Sepah)
private fun DrawScope.drawSepahLogo(color: Color, cx: Float, cy: Float, r: Float) {
    val stroke = r * 0.12f
    // Octagon boundary
    val octPath = Path()
    for (i in 0 until 8) {
        val angle = (i * 45f + 22.5f) * (PI / 180f)
        val x = cx + cos(angle).toFloat() * r * 0.88f
        val y = cy + sin(angle).toFloat() * r * 0.88f
        if (i == 0) octPath.moveTo(x, y) else octPath.lineTo(x, y)
    }
    octPath.close()
    drawPath(octPath, color = color, style = Stroke(width = stroke))

    // Central ancient sword & helm axis
    drawLine(
        color = color,
        start = Offset(cx, cy - r * 0.55f),
        end = Offset(cx, cy + r * 0.55f),
        strokeWidth = stroke * 1.3f,
        cap = StrokeCap.Round
    )
    // Crossbar
    drawLine(
        color = color,
        start = Offset(cx - r * 0.35f, cy - r * 0.18f),
        end = Offset(cx + r * 0.35f, cy - r * 0.18f),
        strokeWidth = stroke,
        cap = StrokeCap.Round
    )
    // Laurel branches
    val leftArc = Path().apply {
        moveTo(cx - r * 0.4f, cy + r * 0.35f)
        quadraticTo(cx - r * 0.5f, cy, cx - r * 0.15f, cy - r * 0.4f)
    }
    drawPath(leftArc, color = color, style = Stroke(width = stroke * 0.9f, cap = StrokeCap.Round))
    val rightArc = Path().apply {
        moveTo(cx + r * 0.4f, cy + r * 0.35f)
        quadraticTo(cx + r * 0.5f, cy, cx + r * 0.15f, cy - r * 0.4f)
    }
    drawPath(rightArc, color = color, style = Stroke(width = stroke * 0.9f, cap = StrokeCap.Round))
}

// 4. بانک تجارت (Tejarat)
private fun DrawScope.drawTejaratLogo(color: Color, cx: Float, cy: Float, r: Float) {
    val stroke = r * 0.12f
    // Outer shield
    val shield = Path().apply {
        moveTo(cx, cy - r * 0.9f)
        lineTo(cx + r * 0.8f, cy - r * 0.5f)
        lineTo(cx + r * 0.7f, cy + r * 0.3f)
        quadraticTo(cx, cy + r * 0.95f, cx, cy + r * 0.95f)
        quadraticTo(cx - r * 0.7f, cy + r * 0.3f, cx - r * 0.7f, cy + r * 0.3f)
        lineTo(cx - r * 0.8f, cy - r * 0.5f)
        close()
    }
    drawPath(shield, color = color, style = Stroke(width = stroke, join = StrokeJoin.Round))

    // Sailing ship sails
    val mainSail = Path().apply {
        moveTo(cx, cy - r * 0.45f)
        lineTo(cx, cy + r * 0.25f)
        quadraticTo(cx + r * 0.45f, cy, cx, cy - r * 0.45f)
        close()
    }
    drawPath(mainSail, color = color, style = Fill)

    val frontSail = Path().apply {
        moveTo(cx - r * 0.08f, cy - r * 0.35f)
        lineTo(cx - r * 0.08f, cy + r * 0.2f)
        quadraticTo(cx - r * 0.45f, cy + r * 0.05f, cx - r * 0.08f, cy - r * 0.35f)
        close()
    }
    drawPath(frontSail, color = color, style = Fill)

    // Ship hull (waves)
    val hull = Path().apply {
        moveTo(cx - r * 0.45f, cy + r * 0.32f)
        lineTo(cx + r * 0.48f, cy + r * 0.32f)
        quadraticTo(cx + r * 0.25f, cy + r * 0.55f, cx - r * 0.35f, cy + r * 0.5f)
        close()
    }
    drawPath(hull, color = color, style = Fill)
}

// 5. بانک سامان (Saman)
private fun DrawScope.drawSamanLogo(color: Color, cx: Float, cy: Float, r: Float) {
    val stroke = r * 0.16f
    // Two interlocking galactic spirals
    val spiral1 = Path().apply {
        moveTo(cx, cy)
        cubicTo(
            cx + r * 0.8f, cy - r * 0.2f,
            cx + r * 0.4f, cy - r * 0.85f,
            cx, cy - r * 0.85f
        )
        cubicTo(
            cx - r * 0.6f, cy - r * 0.85f,
            cx - r * 0.85f, cy - r * 0.3f,
            cx - r * 0.85f, cy
        )
    }
    drawPath(spiral1, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round))

    val spiral2 = Path().apply {
        moveTo(cx, cy)
        cubicTo(
            cx - r * 0.8f, cy + r * 0.2f,
            cx - r * 0.4f, cy + r * 0.85f,
            cx, cy + r * 0.85f
        )
        cubicTo(
            cx + r * 0.6f, cy + r * 0.85f,
            cx + r * 0.85f, cy + r * 0.3f,
            cx + r * 0.85f, cy
        )
    }
    drawPath(spiral2, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round))

    drawCircle(color = color, radius = r * 0.18f, center = Offset(cx, cy))
}

// 6. بانک پاسارگاد (Pasargad)
private fun DrawScope.drawPasargadLogo(color: Color, cx: Float, cy: Float, r: Float) {
    val stroke = r * 0.11f
    // Persepolis Capital: Two back-to-back winged bull capitals
    val columnPath = Path().apply {
        // Base column lines
        moveTo(cx - r * 0.25f, cy + r * 0.6f)
        lineTo(cx - r * 0.25f, cy - r * 0.1f)
        moveTo(cx + r * 0.25f, cy + r * 0.6f)
        lineTo(cx + r * 0.25f, cy - r * 0.1f)
        moveTo(cx, cy + r * 0.6f)
        lineTo(cx, cy - r * 0.1f)
    }
    drawPath(columnPath, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round))

    // Left Bull head & horn scroll
    val leftScroll = Path().apply {
        moveTo(cx, cy - r * 0.1f)
        cubicTo(
            cx - r * 0.4f, cy - r * 0.1f,
            cx - r * 0.8f, cy - r * 0.35f,
            cx - r * 0.6f, cy - r * 0.65f
        )
        cubicTo(
            cx - r * 0.4f, cy - r * 0.65f,
            cx - r * 0.2f, cy - r * 0.45f,
            cx - r * 0.15f, cy - r * 0.25f
        )
    }
    drawPath(leftScroll, color = color, style = Stroke(width = stroke * 1.2f, cap = StrokeCap.Round))

    // Right Bull head & horn scroll
    val rightScroll = Path().apply {
        moveTo(cx, cy - r * 0.1f)
        cubicTo(
            cx + r * 0.4f, cy - r * 0.1f,
            cx + r * 0.8f, cy - r * 0.35f,
            cx + r * 0.6f, cy - r * 0.65f
        )
        cubicTo(
            cx + r * 0.4f, cy - r * 0.65f,
            cx + r * 0.2f, cy - r * 0.45f,
            cx + r * 0.15f, cy - r * 0.25f
        )
    }
    drawPath(rightScroll, color = color, style = Stroke(width = stroke * 1.2f, cap = StrokeCap.Round))

    // Top beam block
    drawLine(
        color = color,
        start = Offset(cx - r * 0.65f, cy - r * 0.65f),
        end = Offset(cx + r * 0.65f, cy - r * 0.65f),
        strokeWidth = stroke * 1.4f,
        cap = StrokeCap.Round
    )
}

// 7. بانک پارسیان (Parsian)
private fun DrawScope.drawParsianLogo(color: Color, cx: Float, cy: Float, r: Float) {
    // 12-petal Achaemenid lotus flower
    val numPetals = 12
    val petalLen = r * 0.75f
    val innerR = r * 0.24f
    for (i in 0 until numPetals) {
        val angle = (i * 360f / numPetals) * (PI / 180f)
        val x1 = cx + cos(angle).toFloat() * petalLen
        val y1 = cy + sin(angle).toFloat() * petalLen
        val angleLeft = angle - 0.15f
        val angleRight = angle + 0.15f
        val x2 = cx + cos(angleLeft).toFloat() * (petalLen * 0.6f)
        val y2 = cy + sin(angleLeft).toFloat() * (petalLen * 0.6f)
        val x3 = cx + cos(angleRight).toFloat() * (petalLen * 0.6f)
        val y3 = cy + sin(angleRight).toFloat() * (petalLen * 0.6f)

        val petalPath = Path().apply {
            moveTo(cx + cos(angle).toFloat() * innerR, cy + sin(angle).toFloat() * innerR)
            lineTo(x2, y2)
            lineTo(x1, y1)
            lineTo(x3, y3)
            close()
        }
        drawPath(petalPath, color = color, style = Fill)
    }
    drawCircle(color = color, radius = innerR * 1.1f, center = Offset(cx, cy))
}

// 8. بانک کشاورزی (Keshavarzi)
private fun DrawScope.drawKeshavarziLogo(color: Color, cx: Float, cy: Float, r: Float) {
    val stroke = r * 0.12f
    // Central stem
    drawLine(
        color = color,
        start = Offset(cx, cy + r * 0.65f),
        end = Offset(cx, cy - r * 0.65f),
        strokeWidth = stroke,
        cap = StrokeCap.Round
    )
    // Grains of wheat branching outwards
    for (i in 0..3) {
        val yOffset = cy - r * 0.45f + i * r * 0.26f
        // Left grain
        val leftGrain = Path().apply {
            moveTo(cx, yOffset)
            quadraticTo(cx - r * 0.42f, yOffset - r * 0.15f, cx - r * 0.4f, yOffset - r * 0.25f)
            quadraticTo(cx - r * 0.18f, yOffset - r * 0.22f, cx, yOffset - r * 0.08f)
            close()
        }
        drawPath(leftGrain, color = color, style = Fill)

        // Right grain
        val rightGrain = Path().apply {
            moveTo(cx, yOffset)
            quadraticTo(cx + r * 0.42f, yOffset - r * 0.15f, cx + r * 0.4f, yOffset - r * 0.25f)
            quadraticTo(cx + r * 0.18f, yOffset - r * 0.22f, cx, yOffset - r * 0.08f)
            close()
        }
        drawPath(rightGrain, color = color, style = Fill)
    }
    // Top spike
    val topSpike = Path().apply {
        moveTo(cx, cy - r * 0.5f)
        lineTo(cx - r * 0.12f, cy - r * 0.8f)
        lineTo(cx, cy - r * 0.9f)
        lineTo(cx + r * 0.12f, cy - r * 0.8f)
        close()
    }
    drawPath(topSpike, color = color, style = Fill)
}

// 9. بلوبانک (Blu)
private fun DrawScope.drawBluLogo(color: Color, cx: Float, cy: Float, r: Float) {
    val stroke = r * 0.22f
    // Distinctive 'b' logo
    // Vertical stem of 'b'
    drawLine(
        color = color,
        start = Offset(cx - r * 0.35f, cy - r * 0.75f),
        end = Offset(cx - r * 0.35f, cy + r * 0.65f),
        strokeWidth = stroke,
        cap = StrokeCap.Round
    )
    // Circle loop of 'b'
    drawCircle(
        color = color,
        radius = r * 0.48f,
        center = Offset(cx + r * 0.12f, cy + r * 0.17f),
        style = Stroke(width = stroke)
    )
}

// 10. بانک شهر (Shahr)
private fun DrawScope.drawShahrLogo(color: Color, cx: Float, cy: Float, r: Float) {
    val stroke = r * 0.13f
    // Outer shield
    drawCircle(color = color, radius = r * 0.85f, center = Offset(cx, cy), style = Stroke(width = stroke))
    // Stylized city arch/gate
    val arch = Path().apply {
        moveTo(cx - r * 0.45f, cy + r * 0.5f)
        lineTo(cx - r * 0.45f, cy - r * 0.1f)
        quadraticTo(cx, cy - r * 0.6f, cx + r * 0.45f, cy - r * 0.1f)
        lineTo(cx + r * 0.45f, cy + r * 0.5f)
        lineTo(cx + r * 0.2f, cy + r * 0.5f)
        lineTo(cx + r * 0.2f, cy)
        quadraticTo(cx, cy - r * 0.25f, cx - r * 0.2f, cy)
        lineTo(cx - r * 0.2f, cy + r * 0.5f)
        close()
    }
    drawPath(arch, color = color, style = Fill)
}

// 11. بانک مسکن (Maskan)
private fun DrawScope.drawMaskanLogo(color: Color, cx: Float, cy: Float, r: Float) {
    val stroke = r * 0.14f
    // Slanted house gable roof
    val roof = Path().apply {
        moveTo(cx, cy - r * 0.65f)
        lineTo(cx + r * 0.75f, cy - r * 0.05f)
        lineTo(cx + r * 0.55f, cy + r * 0.1f)
        lineTo(cx, cy - r * 0.35f)
        lineTo(cx - r * 0.55f, cy + r * 0.1f)
        lineTo(cx - r * 0.75f, cy - r * 0.05f)
        close()
    }
    drawPath(roof, color = color, style = Fill)

    // Key / foundation below roof
    drawLine(
        color = color,
        start = Offset(cx, cy - r * 0.2f),
        end = Offset(cx, cy + r * 0.65f),
        strokeWidth = stroke * 1.2f,
        cap = StrokeCap.Round
    )
    drawCircle(
        color = color,
        radius = r * 0.22f,
        center = Offset(cx, cy + r * 0.45f),
        style = Stroke(width = stroke)
    )
}

// 12. بانک رفاه (Refah)
private fun DrawScope.drawRefahLogo(color: Color, cx: Float, cy: Float, r: Float) {
    val stroke = r * 0.13f
    // Rounded diamond ring
    drawCircle(color = color, radius = r * 0.85f, center = Offset(cx, cy), style = Stroke(width = stroke))
    // Floral worker gear inside
    for (i in 0 until 6) {
        val angle = (i * 60f) * (PI / 180f)
        val x = cx + cos(angle).toFloat() * r * 0.45f
        val y = cy + sin(angle).toFloat() * r * 0.45f
        drawCircle(color = color, radius = r * 0.18f, center = Offset(x, y), style = Fill)
    }
    drawCircle(color = color, radius = r * 0.2f, center = Offset(cx, cy), style = Fill)
}

// 13. بانک اقتصاد نوین (EN)
private fun DrawScope.drawEnLogo(color: Color, cx: Float, cy: Float, r: Float) {
    val stroke = r * 0.16f
    // Interlocking geometric E and N
    // Outer square
    val sq = Path().apply {
        moveTo(cx - r * 0.7f, cy - r * 0.7f)
        lineTo(cx + r * 0.7f, cy - r * 0.7f)
        lineTo(cx + r * 0.7f, cy + r * 0.7f)
        lineTo(cx - r * 0.7f, cy + r * 0.7f)
        close()
    }
    drawPath(sq, color = color, style = Stroke(width = stroke, join = StrokeJoin.Miter))

    // Diagonal N bar
    drawLine(
        color = color,
        start = Offset(cx - r * 0.45f, cy - r * 0.45f),
        end = Offset(cx + r * 0.45f, cy + r * 0.45f),
        strokeWidth = stroke,
        cap = StrokeCap.Square
    )
}

// 14. بانک سینا (Sina)
private fun DrawScope.drawSinaLogo(color: Color, cx: Float, cy: Float, r: Float) {
    val stroke = r * 0.12f
    // 8-pointed star
    val starPath = Path()
    for (i in 0 until 16) {
        val angle = (i * 22.5f) * (PI / 180f)
        val d = if (i % 2 == 0) r * 0.85f else r * 0.45f
        val x = cx + cos(angle).toFloat() * d
        val y = cy + sin(angle).toFloat() * d
        if (i == 0) starPath.moveTo(x, y) else starPath.lineTo(x, y)
    }
    starPath.close()
    drawPath(starPath, color = color, style = Stroke(width = stroke, join = StrokeJoin.Round))
    drawCircle(color = color, radius = r * 0.22f, center = Offset(cx, cy), style = Fill)
}

// 15. بانک کارآفرین (Karafarin)
private fun DrawScope.drawKarafarinLogo(color: Color, cx: Float, cy: Float, r: Float) {
    val stroke = r * 0.13f
    // Diamond boundary
    val diamond = Path().apply {
        moveTo(cx, cy - r * 0.85f)
        lineTo(cx + r * 0.85f, cy)
        lineTo(cx, cy + r * 0.85f)
        lineTo(cx - r * 0.85f, cy)
        close()
    }
    drawPath(diamond, color = color, style = Stroke(width = stroke, join = StrokeJoin.Round))

    // Sprout leaf
    val leaf = Path().apply {
        moveTo(cx, cy + r * 0.35f)
        quadraticTo(cx - r * 0.4f, cy - r * 0.1f, cx, cy - r * 0.5f)
        quadraticTo(cx + r * 0.4f, cy - r * 0.1f, cx, cy + r * 0.35f)
        close()
    }
    drawPath(leaf, color = color, style = Fill)
}

// 16. بانک آینده (Ayandeh)
private fun DrawScope.drawAyandehLogo(color: Color, cx: Float, cy: Float, r: Float) {
    val stroke = r * 0.15f
    // Two interlocking triangles forming dynamic infinity loop
    val t1 = Path().apply {
        moveTo(cx, cy - r * 0.75f)
        lineTo(cx + r * 0.65f, cy + r * 0.45f)
        lineTo(cx - r * 0.65f, cy + r * 0.45f)
        close()
    }
    drawPath(t1, color = color, style = Stroke(width = stroke, join = StrokeJoin.Round))

    val t2 = Path().apply {
        moveTo(cx, cy + r * 0.75f)
        lineTo(cx - r * 0.65f, cy - r * 0.45f)
        lineTo(cx + r * 0.65f, cy - r * 0.45f)
        close()
    }
    drawPath(t2, color = color, style = Stroke(width = stroke, join = StrokeJoin.Round))
}

// 17. بانک قرض‌الحسنه مهر ایران (Mehr)
private fun DrawScope.drawMehrLogo(color: Color, cx: Float, cy: Float, r: Float) {
    val stroke = r * 0.14f
    // Heart-shaped cupped hands holding a seedling
    val heart = Path().apply {
        moveTo(cx, cy + r * 0.7f)
        cubicTo(cx - r * 0.9f, cy + r * 0.2f, cx - r * 0.8f, cy - r * 0.6f, cx, cy - r * 0.2f)
        cubicTo(cx + r * 0.8f, cy - r * 0.6f, cx + r * 0.9f, cy + r * 0.2f, cx, cy + r * 0.7f)
    }
    drawPath(heart, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round))

    // Seedling inside
    drawLine(
        color = color,
        start = Offset(cx, cy + r * 0.4f),
        end = Offset(cx, cy - r * 0.45f),
        strokeWidth = stroke * 0.9f,
        cap = StrokeCap.Round
    )
}

// 18. بانک قرض‌الحسنه رسالت (Resalat)
private fun DrawScope.drawResalatLogo(color: Color, cx: Float, cy: Float, r: Float) {
    val stroke = r * 0.12f
    // Circular boundary
    drawCircle(color = color, radius = r * 0.85f, center = Offset(cx, cy), style = Stroke(width = stroke))
    // Sun rays
    for (i in -3..3) {
        val angle = (90 + i * 20) * (PI / 180f)
        val x1 = cx + cos(angle).toFloat() * r * 0.25f
        val y1 = cy - sin(angle).toFloat() * r * 0.25f
        val x2 = cx + cos(angle).toFloat() * r * 0.65f
        val y2 = cy - sin(angle).toFloat() * r * 0.65f
        drawLine(color = color, start = Offset(x1, y1), end = Offset(x2, y2), strokeWidth = stroke * 0.8f, cap = StrokeCap.Round)
    }
    // Open book horizon
    val book = Path().apply {
        moveTo(cx - r * 0.55f, cy + r * 0.35f)
        quadraticTo(cx - r * 0.25f, cy + r * 0.15f, cx, cy + r * 0.3f)
        quadraticTo(cx + r * 0.25f, cy + r * 0.15f, cx + r * 0.55f, cy + r * 0.35f)
    }
    drawPath(book, color = color, style = Stroke(width = stroke * 1.2f, cap = StrokeCap.Round))
}

// 19. بانک گردشگری (Gardeshgari)
private fun DrawScope.drawGardeshgariLogo(color: Color, cx: Float, cy: Float, r: Float) {
    // 6-pointed compass rose
    val points = 6
    for (i in 0 until points) {
        val angle = (i * 60f) * (PI / 180f)
        val tipX = cx + cos(angle).toFloat() * r * 0.85f
        val tipY = cy + sin(angle).toFloat() * r * 0.85f
        val leftAngle = angle - 0.25f
        val rightAngle = angle + 0.25f
        val baseX1 = cx + cos(leftAngle).toFloat() * r * 0.28f
        val baseY1 = cy + sin(leftAngle).toFloat() * r * 0.28f
        val baseX2 = cx + cos(rightAngle).toFloat() * r * 0.28f
        val baseY2 = cy + sin(rightAngle).toFloat() * r * 0.28f

        val tri = Path().apply {
            moveTo(cx, cy)
            lineTo(baseX1, baseY1)
            lineTo(tipX, tipY)
            lineTo(baseX2, baseY2)
            close()
        }
        drawPath(tri, color = color, style = Fill)
    }
}

// 20. بانک دی (Dey)
private fun DrawScope.drawDeyLogo(color: Color, cx: Float, cy: Float, r: Float) {
    val stroke = r * 0.14f
    drawCircle(color = color, radius = r * 0.85f, center = Offset(cx, cy), style = Stroke(width = stroke))
    // Stylized flower petals
    for (i in 0 until 4) {
        val angle = (i * 90f + 45f) * (PI / 180f)
        val px = cx + cos(angle).toFloat() * r * 0.4f
        val py = cy + sin(angle).toFloat() * r * 0.4f
        drawCircle(color = color, radius = r * 0.22f, center = Offset(px, py), style = Fill)
    }
}

// 21. پست بانک ایران (Post Bank)
private fun DrawScope.drawPostBankLogo(color: Color, cx: Float, cy: Float, r: Float) {
    val stroke = r * 0.13f
    // Postal horn coil
    drawCircle(color = color, radius = r * 0.42f, center = Offset(cx, cy), style = Stroke(width = stroke * 1.2f))
    // Horn bell
    val bell = Path().apply {
        moveTo(cx + r * 0.2f, cy - r * 0.42f)
        lineTo(cx + r * 0.75f, cy - r * 0.7f)
        lineTo(cx + r * 0.75f, cy - r * 0.15f)
        close()
    }
    drawPath(bell, color = color, style = Fill)
    // Speed wing line
    drawLine(
        color = color,
        start = Offset(cx - r * 0.65f, cy + r * 0.2f),
        end = Offset(cx + r * 0.65f, cy + r * 0.2f),
        strokeWidth = stroke,
        cap = StrokeCap.Round
    )
}

// 22. بانک توسعه تعاون (Tose'e Ta'avon)
private fun DrawScope.drawTaavonLogo(color: Color, cx: Float, cy: Float, r: Float) {
    val stroke = r * 0.14f
    // Circular interlocking cooperative nodes
    drawCircle(color = color, radius = r * 0.82f, center = Offset(cx, cy), style = Stroke(width = stroke))
    for (i in 0 until 3) {
        val angle = (i * 120f) * (PI / 180f)
        val nx = cx + cos(angle).toFloat() * r * 0.42f
        val ny = cy + sin(angle).toFloat() * r * 0.42f
        drawCircle(color = color, radius = r * 0.2f, center = Offset(nx, ny), style = Fill)
    }
}

// 23. بانک ایران زمین (Iran Zamin)
private fun DrawScope.drawIranZaminLogo(color: Color, cx: Float, cy: Float, r: Float) {
    val stroke = r * 0.12f
    drawCircle(color = color, radius = r * 0.85f, center = Offset(cx, cy), style = Stroke(width = stroke))
    // Stylized cypress / cedar tree
    val tree = Path().apply {
        moveTo(cx, cy - r * 0.65f)
        lineTo(cx + r * 0.35f, cy + r * 0.35f)
        lineTo(cx - r * 0.35f, cy + r * 0.35f)
        close()
    }
    drawPath(tree, color = color, style = Fill)
    drawLine(
        color = color,
        start = Offset(cx, cy + r * 0.35f),
        end = Offset(cx, cy + r * 0.65f),
        strokeWidth = stroke * 1.5f,
        cap = StrokeCap.Round
    )
}

// 24. بانک سرمایه (Sarmayeh)
private fun DrawScope.drawSarmayehLogo(color: Color, cx: Float, cy: Float, r: Float) {
    val stroke = r * 0.16f
    // Dynamic modern double S swoosh
    val s1 = Path().apply {
        moveTo(cx + r * 0.5f, cy - r * 0.65f)
        cubicTo(cx - r * 0.6f, cy - r * 0.65f, cx - r * 0.4f, cy, cx, cy)
        cubicTo(cx + r * 0.4f, cy, cx + r * 0.6f, cy + r * 0.65f, cx - r * 0.5f, cy + r * 0.65f)
    }
    drawPath(s1, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round))
}

// Default fallback
private fun DrawScope.drawDefaultCardLogo(color: Color, cx: Float, cy: Float, r: Float) {
    val stroke = r * 0.13f
    val cardRect = Rect(cx - r * 0.75f, cy - r * 0.5f, cx + r * 0.75f, cy + r * 0.5f)
    drawRoundRect(
        color = color,
        topLeft = cardRect.topLeft,
        size = cardRect.size,
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.15f, r * 0.15f),
        style = Stroke(width = stroke)
    )
    // Magnetic strip
    drawLine(
        color = color,
        start = Offset(cx - r * 0.75f, cy - r * 0.18f),
        end = Offset(cx + r * 0.75f, cy - r * 0.18f),
        strokeWidth = stroke * 1.5f
    )
    // Chip
    drawRect(
        color = color,
        topLeft = Offset(cx - r * 0.5f, cy + r * 0.05f),
        size = Size(r * 0.3f, r * 0.25f),
        style = Fill
    )
}
