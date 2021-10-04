package com.example.runningapp.other

import android.annotation.SuppressLint
import android.content.Context
import com.example.runningapp.R
import com.example.runningapp.db.Run
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.marker_view.view.*
import java.text.SimpleDateFormat
import java.util.*
// класс котрый будет заполнять наше всплывающее окно
@SuppressLint("ViewConstructor")
class CustomMarkerView(
    var runs: List<Run>, // данные из нашей базы данных
    c: Context,
    layotId: Int
): MarkerView(c, layotId) {

    override fun getOffset(): MPPointF { // устанавливаем смещение всплывающего окна
        return MPPointF(-width/2f, -height.toFloat()) // данные взяты из документации

    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if(e==null){
          return
        }
        val curRunId = e.x.toInt() // получаем текущий индекс run
        val run = runs[curRunId] // передаем curRunId в наш список

        // наши дынные из макета

        val calendar = Calendar.getInstance().apply {
            timeInMillis = run.timeStamp
        }
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())

        tvDate.text = dateFormat.format(calendar.time)

        val avgSpeed = "${run.avgSpeedInKM}km/h"
        tvAvgSpeed.text = avgSpeed

        val distanceInKm = "${run.distanceInM / 1000f}km"
        tvDistance.text = distanceInKm

        tvDuration.text = TrackingUtility.getFormattedStopWatchTime(run.timeMiliseconds)

        val caloriesBurned = "${run.caloriesBurned}kcal"
        tvCaloriesBurned.text = caloriesBurned
    }

}