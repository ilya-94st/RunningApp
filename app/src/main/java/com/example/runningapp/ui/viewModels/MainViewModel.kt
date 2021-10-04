package com.example.runningapp.ui.viewModels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runningapp.db.Run
import com.example.runningapp.repository.MainRepository
import com.example.runningapp.sorted.SortedRuns
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository): ViewModel() {
  private val runsSortedByDate = mainRepository.getAllRunsSortedByDate()
  private val runsAllRunsSortedByAVGSpeedInKm = mainRepository.getAllRunsSortedByAVGSpeedInKm()
  private val runsAllRunsDistanceInM = mainRepository.getAllRunsDistanceInM()
  private val runsAllRunsSortedByTimeMilliseconds = mainRepository.getAllRunsSortedByTimeMiliseconds()
  private val runsAllRunsSortedByCaloriesBurned = mainRepository.getAllRunsSortedByCaloriesBurned()

    val runs = MediatorLiveData<List<Run>>() // MediatorLiveData используется для объединения нескольких LiveDate

    var sortType = SortedRuns.DATE

    init { // в данном init блоке мы объеденяем  поле runs данные из DB
        runs.addSource(runsSortedByDate){result->
            if(sortType == SortedRuns.DATE){
                result?.let {runs.value = it}
            }
        }
        runs.addSource(runsAllRunsSortedByAVGSpeedInKm){result->
            if(sortType == SortedRuns.AVG_SPEED){
                result?.let {runs.value = it}
            }
        }
        runs.addSource(runsAllRunsDistanceInM){result->
            if(sortType == SortedRuns.DISTANCE){
                result?.let {runs.value = it}
            }
        }
        runs.addSource(runsAllRunsSortedByTimeMilliseconds){ result->
            if(sortType == SortedRuns.RUNNING_TIME){
                result?.let {runs.value = it}
            }
        }
        runs.addSource(runsAllRunsSortedByCaloriesBurned){result->
            if(sortType == SortedRuns.CALORIES_BURNED){
                result?.let {runs.value = it}
            }
        }
    }

    fun sortDate(sortedRuns: SortedRuns) = when(sortedRuns){
        SortedRuns.DATE -> runsSortedByDate.value?.let { runs.value =it }
        SortedRuns.DISTANCE -> runsAllRunsDistanceInM.value?.let { runs.value =it }
        SortedRuns.RUNNING_TIME -> runsAllRunsSortedByTimeMilliseconds.value?.let { runs.value =it }
        SortedRuns.AVG_SPEED -> runsAllRunsSortedByAVGSpeedInKm.value?.let { runs.value =it }
        SortedRuns.CALORIES_BURNED -> runsAllRunsSortedByCaloriesBurned.value?.let { runs.value =it }.also {
            this.sortType = sortedRuns
        }
    }

    fun insert(run: Run) = viewModelScope.launch {
        mainRepository.insert(run)
    }

    fun delete(run: Run) = viewModelScope.launch {
        mainRepository.delete(run)
    }

}