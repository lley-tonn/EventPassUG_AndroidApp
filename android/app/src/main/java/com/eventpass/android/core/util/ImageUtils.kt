package com.eventpass.android.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * Image utilities.
 * Migrated from iOS Core/Utilities/ImageCompressor.swift and ImageValidator.swift
 */
object ImageUtils {

    /**
     * Maximum image dimension for uploads.
     */
    const val MAX_DIMENSION = 2048

    /**
     * Maximum file size in bytes (5MB).
     */
    const val MAX_FILE_SIZE = 5 * 1024 * 1024

    /**
     * Compress image to meet size requirements.
     *
     * @param bitmap The bitmap to compress
     * @param maxWidth Maximum width (default: MAX_DIMENSION)
     * @param maxHeight Maximum height (default: MAX_DIMENSION)
     * @param quality Compression quality 0-100 (default: 85)
     * @return Compressed bitmap
     */
    fun compressImage(
        bitmap: Bitmap,
        maxWidth: Int = MAX_DIMENSION,
        maxHeight: Int = MAX_DIMENSION,
        quality: Int = 85
    ): Bitmap {
        var width = bitmap.width
        var height = bitmap.height

        // Calculate scale factor
        val scaleFactor = when {
            width > maxWidth && height > maxHeight -> {
                minOf(maxWidth.toFloat() / width, maxHeight.toFloat() / height)
            }
            width > maxWidth -> maxWidth.toFloat() / width
            height > maxHeight -> maxHeight.toFloat() / height
            else -> 1f
        }

        if (scaleFactor < 1f) {
            width = (width * scaleFactor).toInt()
            height = (height * scaleFactor).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    /**
     * Convert bitmap to byte array.
     */
    fun bitmapToByteArray(
        bitmap: Bitmap,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        quality: Int = 85
    ): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(format, quality, stream)
        return stream.toByteArray()
    }

    /**
     * Validate image dimensions.
     */
    fun validateImageDimensions(
        bitmap: Bitmap,
        minWidth: Int = 100,
        minHeight: Int = 100,
        maxWidth: Int = MAX_DIMENSION,
        maxHeight: Int = MAX_DIMENSION
    ): ImageValidationResult {
        return when {
            bitmap.width < minWidth || bitmap.height < minHeight -> {
                ImageValidationResult.TooSmall(
                    "Image must be at least ${minWidth}x${minHeight} pixels"
                )
            }
            bitmap.width > maxWidth || bitmap.height > maxHeight -> {
                ImageValidationResult.TooLarge(
                    "Image must be at most ${maxWidth}x${maxHeight} pixels"
                )
            }
            else -> ImageValidationResult.Valid
        }
    }

    /**
     * Load and decode bitmap from URI with size constraints.
     */
    fun loadBitmapFromUri(
        context: Context,
        uri: Uri,
        maxWidth: Int = MAX_DIMENSION,
        maxHeight: Int = MAX_DIMENSION
    ): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)

            // First, decode bounds only
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            // Calculate sample size
            options.inSampleSize = calculateInSampleSize(
                options.outWidth,
                options.outHeight,
                maxWidth,
                maxHeight
            )
            options.inJustDecodeBounds = false

            // Decode with sample size
            val newInputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(newInputStream, null, options)
            newInputStream?.close()

            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Calculate sample size for efficient bitmap loading.
     */
    private fun calculateInSampleSize(
        width: Int,
        height: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while ((halfHeight / inSampleSize) >= reqHeight &&
                (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    /**
     * Save bitmap to cache directory.
     */
    fun saveBitmapToCache(
        context: Context,
        bitmap: Bitmap,
        fileName: String,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        quality: Int = 85
    ): File? {
        return try {
            val cacheDir = context.cacheDir
            val file = File(cacheDir, fileName)
            val outputStream = FileOutputStream(file)
            bitmap.compress(format, quality, outputStream)
            outputStream.flush()
            outputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    sealed class ImageValidationResult {
        object Valid : ImageValidationResult()
        data class TooSmall(val message: String) : ImageValidationResult()
        data class TooLarge(val message: String) : ImageValidationResult()
        data class InvalidFormat(val message: String) : ImageValidationResult()
    }
}
