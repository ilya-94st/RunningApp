package com.example.runningapp.repository

import com.example.runningapp.db.Run
import com.example.runningapp.db.RunDao
import javax.inject.Inject

class MainRepository @Inject constructor(
  var runDao: RunDao
) {
    suspend fun insert(run: Run) = runDao.insert(run)

    suspend fun delete(run: Run) = runDao.delete(run) // suspend fun делает данные асинхронными

    fun getAllRunsSortedByDate() = runDao.getAllRunsSortedByDate() // сдесь нету suspend fun потомучто мы получаем живые данные а они асинхронны(по умолчанию)

    fun getAllRunsSortedByAVGSpeedInKm() = runDao.getAllRunsSortedByAVGSpeedInKm()

    fun getAllRunsDistanceInM() = runDao.getAllRunsDistanceInM()

    fun getAllRunsSortedByTimeMiliseconds() = runDao.getAllRunsSortedByTimeMiliseconds()

    fun getAllRunsSortedByCaloriesBurned() = runDao.getAllRunsSortedByCaloriesBurned()

    fun getTotalSumTimeMiliseconds() = runDao.getTotalSumTimeMiliseconds()

    fun getAllTotalSumDistanse() = runDao.getAllTotalSumDistanse()

    fun getAllTotalSumCalories() = runDao.getAllTotalSumCalories()

    fun getAllTotalAVGSpeed() = runDao.getAllTotalAVGSpeed()
}