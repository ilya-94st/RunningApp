package com.example.runningapp.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.runningapp.R
import com.example.runningapp.base.BaseFragment
import com.example.runningapp.databinding.FragmentSatisticsBinding
import com.example.runningapp.other.CustomMarkerView
import com.example.runningapp.other.TrackingUtility
import com.example.runningapp.ui.viewModels.StatisticViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round

@AndroidEntryPoint
class StatisticsFragment : BaseFragment<FragmentSatisticsBinding>() {
   private val viewModel: StatisticViewModel by viewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSatisticsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      //  requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation).visibility = View.VISIBLE
        subScribeObserver()
        barChart()
    }
private fun subScribeObserver() {
 viewModel.getAllTotalAVGSpeed.observe(viewLifecycleOwner, Observer {
     it?.let {
         val totalAVGSpeed = round(it * 10f) / 10f
         binding.tvAverageSpeed.text = "${totalAVGSpeed}km|h"
     }
 })
    viewModel.getAllTotalSumDistanse.observe(viewLifecycleOwner, Observer {
        it?.let {
            val km = it / 1000f
            val totalDistance = round(km * 10f) / 10f
            val totalDistanceString = "${totalDistance}km"
            binding.tvTotalDistance.text = totalDistanceString
        }
    })
    viewModel.getTotalSumTimeMiliseconds.observe(viewLifecycleOwner, Observer {
        it?.let {
            val timeRun = TrackingUtility.getFormattedStopWatchTime(it)
            binding.tvTotalTime.text = timeRun
        }
    })
    viewModel.getAllTotalSumCalories.observe(viewLifecycleOwner, Observer {
        it?.let {
            binding.tvTotalCalories.text = "${it}kcal"
        }
    })
    viewModel.runssortedByDate.observe(viewLifecycleOwner, Observer {
        it?.let{
            val avgAllSpeed = it.indices.map { i-> BarEntry(i.toFloat(), it[i].avgSpeedInKM) } // получаем среднюю скорость для гистограммы
            val bardateSet = BarDataSet(avgAllSpeed, "Avg speed over time").apply { // наши столбики в гистограмме
                valueTextColor = Color.WHITE
                color = ContextCompat.getColor(requireContext(), R.color.yelow) // цвет столбиков
            }
            binding.barChart.data = BarData(bardateSet)
            binding.barChart.marker = CustomMarkerView(it.reversed(),requireContext(), R.layout.marker_view)
            binding.barChart.invalidate() // обновляем дату
        }
    })
}
    private fun barChart() { // функция для каректировки нашей гистограммы , по бокам гистрограмы будут белые линии с цифрами указывающее среднюю скорость
        binding.barChart.xAxis.apply { // устанавливаем позиции снизу
            position = XAxis.XAxisPosition.BOTTOM // устанавливаем снизу
            setDrawLabels(false) // чтобы нельзя было нажать на график
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false) // отключить сетку
        }
        binding.barChart.axisLeft.apply { // слева
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        binding.barChart.axisRight.apply { // справа
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        binding.barChart.apply {
            description.text = "Avg speed over time" // показываем что на диограме
            legend.isEnabled = false // отключить легенду
        }
    }
}