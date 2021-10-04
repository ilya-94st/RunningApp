package com.example.runningapp.servises

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getService
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.runningapp.R
import com.example.runningapp.other.Constants
import com.example.runningapp.other.Constants.ID_CHANNEL
import com.example.runningapp.other.Constants.NOTIFIED_ID
import com.example.runningapp.other.Constants.TIME_DELAY
import com.example.runningapp.other.TrackingUtility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias PolyLine = MutableList<LatLng>
typealias PolyLines = MutableList<PolyLine>

@AndroidEntryPoint
class TrackingServices: LifecycleService() {
    private var isServiceKilled = false
    private var isTamerEnabled = false // время
    private var lapTime: Long = 0L // время с самого начало когда таймер запустился
    private var timeRun:Long = 0L // общее время прохождение
    private var timeStarted: Long = 0L // время когда таймер запустили
    private var lastSecondTimestamp: Long = 0L
    private var isStarted: Boolean = true
    private var timeRunSeconds: MutableLiveData<Long> = MutableLiveData() // это время будет для уведомления

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient // клиент поставщика местоположения

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    lateinit var curNotificationBuilder: NotificationCompat.Builder

    @SuppressLint("VisibleForTests")
    override fun onCreate() {
        super.onCreate()
        curNotificationBuilder = baseNotificationBuilder
        postInitialValue()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        isTracking.observe(this, {
           updateLocationTracking(it)
            updateNotificationTrackingState(it)
        })
    }
// создаем отслеживание по карте
    companion object { // создаем наш синглтон
        var timeRunMiles: MutableLiveData<Long> = MutableLiveData() // это время будет для фрагмента
       val isTracking = MutableLiveData<Boolean>() // начало отслеживание true значет началось false значет закончилось
       //    val pathPoints = MutableLiveData<MutableList<MutableList<LatLng>>>() // LatLng это специальный класс который будет в себе содержать долготу и ширину координат
       val pathPoints =  MutableLiveData<PolyLines>() // pathPoints точка на нашем пути
   }

    private fun postInitialValue() { // инитилизируем наши LiveData
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunMiles.postValue(0L)
        timeRunSeconds.postValue(0L)
    }

    private fun addEmptyPolyline() = pathPoints.value?.apply { // нам нужен сначала пустой изменяемый список чтобы потом снова добавлять новые координаты
        add(mutableListOf())
        pathPoints.postValue(this) // и надо уведомить фрагмент об новых данных
    }?: pathPoints.postValue(mutableListOf(mutableListOf())) // если прилити null то тогда мы опубликуем пустую patchPoints

    private fun addPathPoint(location: Location?){ // полследние координы pathPoints
        location?.let { // проверяем на наличие null
            val position = LatLng(location.latitude, location.longitude) // получаем ширину latitude и долготу longitude
            pathPoints.value?.apply {
                last().add(position)
                pathPoints.postValue(this) // и надо уведомить фрагмент об новых данных наша PolyLines
            }
        }
    }

    private val locationCallback  = object: LocationCallback(){ // будем использовать для запроса обнавления  местоположение и получать фактическое местоположение в итоге
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result) // получае новое местоположение
            if(isTracking.value!!){
                result.locations.let {locations->
                    for(location in locations){
                        addPathPoint(location) // полученное местоположение мы добавляем к последнем данным pathPoints
                        Timber.d("NEW LOCATION ${location.latitude} || ${location.longitude}")
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean){ // функция которая активирует отслеживание нашего местополженя
        if (isTracking){
            if(TrackingUtility.hasLocationPermission(this)){ // если пользователь дал доступ(Permission) то начинаем отслеживание иначе нет
                val request = LocationRequest().apply {
                    interval = Constants.LOCATION_UPDATE_INTERVAL // как часто мы будем получать обнавление нашего местоположения
                    fastestInterval = Constants.FASTER_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY // мы хотим получить максимально точные координаты местоположения
                }
                fusedLocationProviderClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper()) // передаем наш запрос на местоположение
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback) // мы просто удалим запрос так как пользователь не предоставли разрешение на отслеживание
        }
    }

// создаем  сервис
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                Constants.ACTION_START_OR_RESUME_SERVICE ->
                    if(isStarted){
                        startForegroundService()
                        isStarted = false
                    } else {
                        timerStart()
                        Timber.d("Resuming services..")
                    }
                Constants.ACTION_PAUSE_SERVICE ->{
                    pauseService()
                    Timber.d("stop service")
                }
                Constants.ACTION_STOP_SERVICE -> {
                    killService()
                    Timber.d("stop service")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun pauseService(){
        isTracking.postValue(false)
        isTamerEnabled = false
    }

    private fun killService(){
        isServiceKilled = true
        isStarted = true
        postInitialValue()
        pauseService()
        stopForeground(true)
        stopSelf()
    }

// создаем  уведомление
    private fun updateNotificationTrackingState(isTracking: Boolean){ // update уведомление
        val notificationActionText = if(isTracking) "Pause" else "Resume"
        val pendingIntent = if (isTracking){
         val pauseIntent = Intent(this, TrackingServices::class.java).apply {
             action = Constants.ACTION_PAUSE_SERVICE
         }
            getService(this, 1, pauseIntent, FLAG_UPDATE_CURRENT)
        } else {
            val resumeIntent = Intent(this, TrackingServices::class.java).apply {
                action = Constants.ACTION_START_OR_RESUME_SERVICE
            }
            getService(this, 2, resumeIntent, FLAG_UPDATE_CURRENT)
        }
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    curNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
        isAccessible = true
        set(curNotificationBuilder, ArrayList<NotificationCompat.Action>())
    }
    if(!isServiceKilled){
        curNotificationBuilder = baseNotificationBuilder.
        addAction(R.drawable.ic_baseline_pause_24,notificationActionText, pendingIntent)
        notificationManager.notify(NOTIFIED_ID, curNotificationBuilder.build())
    }
    }

    private fun startForegroundService() {
        timerStart()
        isTracking.postValue(true)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChanel(notificationManager)
        startForeground(NOTIFIED_ID, baseNotificationBuilder.build())

        timeRunSeconds.observe(this, { seconds->
            if(!isServiceKilled){
                val notification = curNotificationBuilder
                    .setContentText(TrackingUtility.getFormattedStopWatchTime(seconds * 1000))
                notificationManager.notify(NOTIFIED_ID, notification.build())
            }
        })
    }

    private fun createNotificationChanel(notificationManager: NotificationManager) {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val channel = NotificationChannel(ID_CHANNEL, Constants.NOTIFIED_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }
    }
    // Таймер
    private fun timerStart() {
        addEmptyPolyline() // когда мы запускаем нашу сервесную службы мы хоти оказаться с пустой pathPoints т.е с пустыми координатами
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTamerEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
         while (isTracking.value!!){
            lapTime = System.currentTimeMillis() - timeStarted // разница времени между временем настоящем(времни системы) и времени начавшимся
             timeRunMiles.postValue(timeRun + lapTime) // новое время круга
             if(timeRunMiles.value!! >= lastSecondTimestamp + 1000L){ // нам нужна это проверка чтобы обновлять данные через 1 секунду
                 timeRunSeconds.postValue(timeRunSeconds.value!! + 1)
                 lastSecondTimestamp += 1000L
             }
             delay(TIME_DELAY) // нам нужна задержка в 50 с для того чтобы не потерять производительность
         }
          timeRun += lapTime  // мы хотим сложить время последнего круга с общим временем
        }
    }
}