package com.example.runningapp.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.runningapp.db.RunningDatabase
import com.example.runningapp.other.Constants
import com.example.runningapp.other.Constants.DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
// создаем руководство для Дагера по созданию базы данных и это руководство нужно дагеру чтобы создать Dao
@Module
@InstallIn(SingletonComponent::class)

object AppModule {


    @Provides // предостовляем нашему dagger hilt что сдесь создаем базу данных
    @Singleton // каждый раз когда наша база данных будет внедряться будет создан один экземпляр класса
    fun provideRunningDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(app, RunningDatabase::class.java, DB_NAME).build()

    @Provides
    @Singleton
    fun getRunningDao(db: RunningDatabase) = db.getRunningDao() // как раз сдесь мы говорим нашем дагеру чтобы создать объект Dao

    @Provides
    @Singleton
    fun provideSharedPreference(@ApplicationContext app: Context) =
        app.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideName(sharedPreferences: SharedPreferences) = sharedPreferences.getString(Constants.KEY_NAME, "")?: ""

    @Provides
    @Singleton
    fun provideWeight(sharedPreferences: SharedPreferences) = sharedPreferences.getFloat(Constants.KEY_WEIGHT, 80f)

    @Provides
    @Singleton
    fun provideTimeToggle(sharedPreferences: SharedPreferences) = sharedPreferences.getBoolean(Constants.FIRST_TIME_TOGGLE, true)
}
