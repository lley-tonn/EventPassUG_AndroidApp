package com.eventpass.android.core.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

/**
 * QR Code generator utility.
 * Migrated from iOS Core/Utilities/QRCodeGenerator.swift
 *
 * Uses ZXing library for QR code generation.
 */
object QRCodeGenerator {

    /**
     * Generate QR code bitmap from string data.
     *
     * @param data The string data to encode
     * @param size The size of the QR code in pixels
     * @param foregroundColor The color of the QR code modules (default: black)
     * @param backgroundColor The background color (default: white)
     * @return Bitmap containing the QR code, or null if generation fails
     */
    fun generateQRCode(
        data: String,
        size: Int = 512,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE
    ): Bitmap? {
        return try {
            val hints = hashMapOf<EncodeHintType, Any>(
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H,
                EncodeHintType.MARGIN to 1,
                EncodeHintType.CHARACTER_SET to "UTF-8"
            )

            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size, hints)

            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(
                        x, y,
                        if (bitMatrix[x, y]) foregroundColor else backgroundColor
                    )
                }
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Generate ticket QR code data string.
     */
    fun generateTicketQRData(ticketId: String): String {
        return "TICKET:$ticketId"
    }

    /**
     * Generate pairing QR code data string.
     */
    fun generatePairingQRData(sessionId: String, eventId: String): String {
        return "eventpass://pair?session=$sessionId&event=$eventId"
    }

    /**
     * Parse ticket ID from QR code data.
     */
    fun parseTicketQRData(data: String): String? {
        return if (data.startsWith("TICKET:")) {
            data.removePrefix("TICKET:")
        } else {
            null
        }
    }

    /**
     * Parse pairing data from QR code.
     */
    fun parsePairingQRData(data: String): PairingQRData? {
        if (!data.startsWith("eventpass://pair?")) return null

        val params = data.removePrefix("eventpass://pair?")
            .split("&")
            .mapNotNull { param ->
                val parts = param.split("=")
                if (parts.size == 2) parts[0] to parts[1] else null
            }
            .toMap()

        val sessionId = params["session"] ?: return null
        val eventId = params["event"] ?: return null

        return PairingQRData(sessionId, eventId)
    }

    data class PairingQRData(
        val sessionId: String,
        val eventId: String
    )
}
