package com.c242_ps246.mentalq.ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object Utils {
    private const val MAXIMAL_SIZE = 500000
    private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    private val timeStamp: String =
        SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())

    fun formatDate(dateString: String): String {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
                .withLocale(Locale.getDefault())
                .withZone(ZoneId.systemDefault())

            if (dateString.contains("T")) {
                val instant = Instant.parse(dateString)
                formatter.format(instant)
            } else {
                val localDate =
                    LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                localDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
            }
        } catch (e: Exception) {
            Log.d("Utils", "Error parsing date", e)
            dateString
        }
    }

    fun createCustomTempFile(context: Context): File {
        val filesDir = context.externalCacheDir
        return File.createTempFile(timeStamp, ".jpeg", filesDir)
    }

    fun uriToFile(imageUri: Uri, context: Context): File {
        val myFile = createCustomTempFile(context)
        val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
        val outputStream = FileOutputStream(myFile)
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(
            buffer,
            0,
            length
        )
        outputStream.close()
        inputStream.close()
        return myFile
    }

    fun File.compressImageSize(): File {
        val file = this
        val bitmap = getBitmapWithCorrectRotation(file.path)
        var compressQuality = 60
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 10
        } while (streamLength > MAXIMAL_SIZE)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    private fun getBitmapWithCorrectRotation(imagePath: String): Bitmap? {
        val options = BitmapFactory.Options()
        var bitmap = BitmapFactory.decodeFile(imagePath, options)

        try {
            val exif = ExifInterface(imagePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            bitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
                else -> bitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return bitmap
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    fun fetchServerTime(
        onTimeFetched: (LocalDateTime) -> Unit,
        onError: ((String) -> Unit)? = null
    ) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://worldtimeapi.org/api/timezone/Asia/Jakarta")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ServerTime", "Error fetching server time", e)
                Handler(Looper.getMainLooper()).post {
                    onError?.invoke(e.message ?: "Unknown error")
                    onTimeFetched(LocalDateTime.now())
                }
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    if (!response.isSuccessful) {
                        Log.e("ServerTime", "Unsuccessful response: ${response.code}")
                        Handler(Looper.getMainLooper()).post {
                            onError?.invoke("Unsuccessful response")
                            onTimeFetched(LocalDateTime.now())
                        }
                        return
                    }

                    response.body?.string()?.let { responseData ->
                        val json = JSONObject(responseData)
                        val dateTime = json.getString("datetime")
                        val serverTime = LocalDateTime.parse(dateTime.substring(0, 19))

                        Handler(Looper.getMainLooper()).post {
                            onTimeFetched(serverTime)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ServerTime", "Error parsing server time", e)
                    Handler(Looper.getMainLooper()).post {
                        onError?.invoke(e.message ?: "Parsing error")
                        onTimeFetched(LocalDateTime.now())
                    }
                }
            }
        })
    }
}