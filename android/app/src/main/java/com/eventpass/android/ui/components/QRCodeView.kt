package com.eventpass.android.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eventpass.android.core.util.QRCodeGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * QR code display component.
 * Migrated from iOS UI/Components/QRCodeView.swift
 *
 * Uses ZXing library for QR code generation.
 */
@Composable
fun QRCodeView(
    data: String,
    size: Dp = 200.dp,
    modifier: Modifier = Modifier,
    foregroundColor: Int = android.graphics.Color.BLACK,
    backgroundColor: Int = android.graphics.Color.WHITE,
    padding: Dp = 16.dp
) {
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Generate QR code on background thread
    LaunchedEffect(data, foregroundColor, backgroundColor) {
        qrBitmap = withContext(Dispatchers.Default) {
            QRCodeGenerator.generateQRCode(
                data = data,
                size = size.value.toInt() * 2, // Generate at 2x for quality
                foregroundColor = foregroundColor,
                backgroundColor = backgroundColor
            )
        }
    }

    Box(
        modifier = modifier
            .size(size + (padding * 2))
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        qrBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier.size(size)
            )
        } ?: run {
            // Loading placeholder
            SkeletonBox(
                modifier = Modifier.size(size)
            )
        }
    }
}

/**
 * Ticket QR code with ticket number.
 */
@Composable
fun TicketQRCode(
    ticketId: String,
    ticketNumber: String,
    size: Dp = 200.dp,
    modifier: Modifier = Modifier
) {
    val qrData = QRCodeGenerator.generateTicketQRData(ticketId)

    androidx.compose.foundation.layout.Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        QRCodeView(
            data = qrData,
            size = size
        )

        androidx.compose.foundation.layout.Spacer(
            modifier = Modifier.size(8.dp)
        )

        androidx.compose.material3.Text(
            text = ticketNumber,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Scanner pairing QR code.
 */
@Composable
fun PairingQRCode(
    sessionId: String,
    eventId: String,
    pairingCode: String,
    size: Dp = 250.dp,
    modifier: Modifier = Modifier
) {
    val qrData = QRCodeGenerator.generatePairingQRData(sessionId, eventId)

    androidx.compose.foundation.layout.Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        QRCodeView(
            data = qrData,
            size = size
        )

        androidx.compose.foundation.layout.Spacer(
            modifier = Modifier.size(16.dp)
        )

        androidx.compose.material3.Text(
            text = "Or enter code:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        androidx.compose.foundation.layout.Spacer(
            modifier = Modifier.size(8.dp)
        )

        androidx.compose.material3.Text(
            text = pairingCode,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 4.sp
        )
    }
}

private val Int.sp: androidx.compose.ui.unit.TextUnit
    get() = androidx.compose.ui.unit.TextUnit(this.toFloat(), androidx.compose.ui.unit.TextUnitType.Sp)
