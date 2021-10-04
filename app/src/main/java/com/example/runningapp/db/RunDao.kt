package com.example.runningapp.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RunDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(run: Run)

    @Delete
    suspend fun delete(run: Run)

    @Query("select * from running_app order by timeStamp desc") // получить данные из наши базы и отсортировать их по времени timeStamp по убыванию
    fun getAllRunsSortedByDate() :LiveData<List<Run>>

    @Query("select * from running_app order by avgSpeedInKM desc")
    fun getAllRunsSortedByAVGSpeedInKm(): LiveData<List<Run>>

    @Query("select * from running_app order by distanceInM desc")
    fun getAllRunsDistanceInM(): LiveData<List<Run>>

    @Query("select * from running_app order by timeMiliseconds desc")
    fun getAllRunsSortedByTimeMiliseconds(): LiveData<List<Run>>

    @Query("select * from running_app order by caloriesBurned desc")
    fun getAllRunsSortedByCaloriesBurned(): LiveData<List<Run>>

    @Query("select sum(timeMiliseconds) from running_app") // получаем из нашей базы данных сумму времениСекундах возвращаем их в Long и наблюдаем за их изменениями в реальном времени
    fun getTotalSumTimeMiliseconds(): LiveData<Long>

    @Query("select sum(distanceInM) from running_app")
    fun getAllTotalSumDistanse(): LiveData<Int>

    @Query("select sum(caloriesBurned) from running_app")
    fun getAllTotalSumCalories(): LiveData<Int>

    @Query("select avg(avgSpeedInKM) from running_app")
    fun getAllTotalAVGSpeed(): LiveData<Float>

}