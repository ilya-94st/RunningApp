package com.example.runningapp.di

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.runningapp.R
import com.example.runningapp.other.Constants
import com.example.runningapp.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

// создаем руководство для даггера по сервисам

@Module
@InstallIn(ServiceComponent::class) // в данном случае дагер будет жить до тех пор пока живы сервесы
object ServiceModule {

    @Provides
    @ServiceScoped // на протяженни всей службы сервеса будет создан только один экземпляр класа
    @SuppressLint("VisibleForTests")
    fun providerFusedLocationProviderClient(
        @ApplicationContext app: Context
    ) = FusedLocationProviderClient(app)

    @Provides
    @ServiceScoped
    fun MainActivityPendingIntent(
        @ApplicationContext app: Context
    ) =  PendingIntent.getActivity( // функция которая при нажатии на уведомление делает что то
        app,
        0,
        Intent(app, MainActivity::class.java).also {
            it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT // по этой константе мы и будем переходить в наш фрагмент она ключевое звено!!!
        },
        PendingIntent.FLAG_UPDATE_CURRENT // потверждает что будет только один pendingIntent одно уведомление и один PendingIntent и потом перезаписывается обновляется
    )

    @Provides
    @ServiceScoped
    fun provideBaseNotificationBuilder(
        @ApplicationContext app: Context,
        pendingIntent: PendingIntent
    ) = NotificationCompat.Builder(app, Constants.ID_CHANNEL)
        .setAutoCancel(false) // чтобы уведомление было всегда активно
        .setOngoing(true) // уведомление нельзя удалить
        .setContentTitle("Running app")
        .setContentText("00:00:00")
        .setSmallIcon(R.drawable.ic_baseline_moped_24)
        .setContentIntent(pendingIntent) // прикосновение к уведомлению
}