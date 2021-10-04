package com.example.runningapp.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_app")
data class Run(
    var img: Bitmap? = null,
    var timeStamp: Long = 0L, // временная отметка
    var avgSpeedInKM: Float =0F, // средняя скорость в км
    var distanceInM: Int = 0, // дистнация в м
    var timeMiliseconds: Long = 0L, // время в милисекундай
     var caloriesBurned: Int=0

) {
    @PrimaryKey(autoGenerate = true) // генерация нашего уникального id для каждого поля в базу данных
    var id: Int? = null
}