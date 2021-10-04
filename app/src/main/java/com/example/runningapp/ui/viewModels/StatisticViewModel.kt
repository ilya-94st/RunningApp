package com.example.runningapp.ui.viewModels

import androidx.lifecycle.ViewModel
import com.example.runningapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor(mainRepository: MainRepository): ViewModel() {

    val getTotalSumTimeMiliseconds = mainRepository.getTotalSumTimeMiliseconds()
    val getAllTotalSumDistanse = mainRepository.getAllTotalSumDistanse()
    val getAllTotalSumCalories = mainRepository.getAllTotalSumCalories()
    val getAllTotalAVGSpeed = mainRepository.getAllTotalAVGSpeed()

    val runssortedByDate = mainRepository.getAllRunsSortedByDate()
}
