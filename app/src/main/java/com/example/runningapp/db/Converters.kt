package com.example.runningapp.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converters {
    @TypeConverter
    fun fromBitmap(imgBitmap: Bitmap): ByteArray {
        val outPutStream = ByteArrayOutputStream()
        imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, outPutStream)
        return outPutStream.toByteArray()
    }
    @TypeConverter
    fun toBitmap(bytes: ByteArray): Bitmap{
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}