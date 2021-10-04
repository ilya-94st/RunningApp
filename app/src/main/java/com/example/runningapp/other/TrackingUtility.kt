package com.example.runningapp.other

import android.content.Context
import android.location.Location
import android.os.Build
import com.example.runningapp.servises.PolyLine
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit
import kotlin.math.round


object TrackingUtility {
    fun hasLocationPermission(context: Context) = // функция которая возращает true or false если у нас есть такие разрешения
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){ // не надо запрашивать разрешиние в фоновом потоке
            EasyPermissions.hasPermissions(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
        }else {
            EasyPermissions.hasPermissions(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION // надо запрашивать разрешение на работу в фоновом потоке при version.code.Q
            )
        }

    fun calculateAVGSpeed(distanceInMetres: Int, curTimeMiles: Long): Float {
        return round((distanceInMetres / 1000f) / (curTimeMiles / 1000f / 60 / 60) * 10) / 10f
    }

    fun calculateCaloriesBurned(weight: Float, distanceInMetres: Int) : Int {
        return ((distanceInMetres / 1000f) * weight).toInt()
    }

    fun calculatePolylineDictation(polyLine: PolyLine): Float{ // функция для расчета пройденного расстояния!!!
        var distance = 0f
        for(i in 0..polyLine.size-2){
           val position1 = polyLine[i]
            val position2 = polyLine[i+1]
            val result = FloatArray(1)
            Location.distanceBetween(position1.latitude,position1.longitude,
            position2.latitude, position2.longitude,result
                )
            distance += result[0]
        }
return distance
    }

    fun getFormattedStopWatchTime(ms: Long, includeMillis: Boolean = false): String { // функция которое срезает точное время это функция реализована в видео Implementing the Stop Watch - MVVM Running Tracker App - Part 15 Philip Lanckner
        var milliseconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        if(!includeMillis) {
            return "${if(hours < 10) "0" else ""}$hours:" +
                    "${if(minutes < 10) "0" else ""}$minutes:" +
                    "${if(seconds < 10) "0" else ""}$seconds"
        }
        milliseconds -= TimeUnit.SECONDS.toMillis(seconds)
        milliseconds /= 10
        return "${if(hours < 10) "0" else ""}$hours:" +
                "${if(minutes < 10) "0" else ""}$minutes:" +
                "${if(seconds < 10) "0" else ""}$seconds:" +
                "${if(milliseconds < 10) "0" else ""}$milliseconds"
    }
}