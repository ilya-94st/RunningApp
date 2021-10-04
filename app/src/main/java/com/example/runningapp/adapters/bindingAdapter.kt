package com.example.runningapp.adapters

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.runningapp.other.TrackingUtility
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("loadImage")
fun ImageView.loadImage(image: Bitmap?){
    Glide.with(this).load(image).into(this)
}

@SuppressLint("SetTextI18n")
@BindingAdapter("avgSpeed")
fun TextView.avgSpeed(avgSpeed: Float){
    val speed = "${avgSpeed}km/h"
    text = speed
}

@SuppressLint("SetTextI18n")
@BindingAdapter("caloriesBurned")
fun TextView.caloriesBurned(calories: Int) {
    val caloriesBurned = "${calories}kcal"
    text = caloriesBurned
}

@BindingAdapter("tvTime")
fun TextView.tvTime(tvTime: Long){
    text = TrackingUtility.getFormattedStopWatchTime(tvTime)
}

@SuppressLint("SetTextI18n")
@BindingAdapter("distance")
fun TextView.distance(distance: Int){
    val distanceInKm = "${distance / 1000f}km"
    text = distanceInKm
}

@BindingAdapter("tvDate")
fun TextView.tvDate(timeStamp: Long){
    val calender = Calendar.getInstance().apply {
        timeInMillis = timeStamp
    }
    val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
    text = dateFormat.format(calender.time)
}