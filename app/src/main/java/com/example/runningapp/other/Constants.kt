package com.example.runningapp.other

import android.graphics.Color
import androidx.annotation.ColorRes
import com.example.runningapp.R

object Constants {
   const val DB_NAME = "running.db"
    const val REQUSET_CODE_PERMISION = 0

    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

 const val LOCATION_UPDATE_INTERVAL = 5000L
 const val FASTER_LOCATION_INTERVAL = 3000L

    const val POLYLINE_COLOR = R.color.blue
    const val POLYLINE_WIDTH = 14f

    const val CAMERA_ZOOM = 15f

    const val TIME_DELAY = 50L

    const val ID_CHANNEL = "id_tracking"
    const val NOTIFIED_CHANNEL_NAME = "NOTIFIC_CHANEL_TRACKING"
    const val NOTIFIED_ID = 1

    const val SHARED_PREFERENCE_NAME = "shared"

    const val FIRST_TIME_TOGGLE = "FIRST_TIME_TOGGLE"

    const val KEY_WEIGHT = "KEY_WEIGHT"

    const val KEY_NAME = "KEY_NAME"
}