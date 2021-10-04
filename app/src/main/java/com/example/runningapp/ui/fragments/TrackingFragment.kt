package com.example.runningapp.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.runningapp.R
import com.example.runningapp.base.BaseFragment
import com.example.runningapp.databinding.FragmentTrackingBinding
import com.example.runningapp.db.Run
import com.example.runningapp.other.Constants
import com.example.runningapp.other.TrackingUtility
import com.example.runningapp.servises.PolyLine
import com.example.runningapp.servises.TrackingServices
import com.example.runningapp.ui.viewModels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*
import java.util.*
import javax.inject.Inject

const val TAG_DIALOG = "CancelDialog"

@Suppress("DEPRECATION")
@AndroidEntryPoint
class TrackingFragment : BaseFragment<FragmentTrackingBinding>(){
    private val viewModel: MainViewModel by viewModels() // мы пишим так потомучто дагер умный и может сам найти какую viewModel использовать для нас и назначает переменную предстовления
    private var map: GoogleMap? = null // гугл карта, а mapView это виюха карты
    private var curTimeMiles = 0L // сколько времени прошло
    private var menu: Menu? = null

    @set:Inject // когда мы вводим данные из эдит текста и сохраняем их при помощи sharedPreference то поле weight преобретает новое значение
    var weight = 80f

    private var isTracking: Boolean = false  // начало отслеживание true или false
    private var pathPoints: MutableList<PolyLine> = mutableListOf() // список наших координат

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentTrackingBinding.inflate(inflater, container, false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)
        if(savedInstanceState != null){
            val trackingCancelDialog = parentFragmentManager.findFragmentByTag(TAG_DIALOG) as TracingFragmentDialog?
            trackingCancelDialog?.setListner { stopRun() }
        }
    //    requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation).visibility = View.GONE
        binding.mapView.getMapAsync { // функция вызывается только тогда когда создается фрагмент, т.е даже когда мы перевернем устройство она занова буде создоваться
            map = it
            addAllPolyline() // функцию которая специально созда для того чтобы рисовать наши точки при повороте устройства
        }
        binding.btnToggleRun.setOnClickListener { toggleRun() }
        binding.btnFinishRun.setOnClickListener {
            zoomToSeeWholeTrack()
            endRunAndSaveToDb()
        }
        subscribeToObserves()
    }

// связь с данными(координатами) из TrackingServices
    private fun subscribeToObserves() {
        TrackingServices.isTracking.observe(viewLifecycleOwner, {trackingState->
            updateTracking(trackingState)
        })
        TrackingServices.pathPoints.observe(viewLifecycleOwner, {polyline->
            pathPoints = polyline
            addLatestPolyline()
            moveCameraToUser()
        })
        TrackingServices.timeRunMiles.observe(viewLifecycleOwner, { timeRun->
            curTimeMiles = timeRun
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(curTimeMiles, true) // функция по форматированию времени
            binding.tvTimer.text = formattedTime
        })
    }

// создание меню которое будет уничтожать сервис при отмене
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) { // функция которая отображает меню если время больше нуля
        super.onPrepareOptionsMenu(menu)
        if(curTimeMiles>0L){
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { // функция благодаря которой мы можем взаимодествовать с элементами меню
        when(item.itemId){
            R.id.cancelTrucking ->{ // название меню
                cancelDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun cancelDialog() {
TracingFragmentDialog().apply {
    setListner {
stopRun()
    }
}.show(parentFragmentManager, TAG_DIALOG)
    }

    @SuppressLint("SetTextI18n")
    private fun stopRun(){
        binding.tvTimer.text = "00:00:00:00"
sendTrackingService(Constants.ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    // рисуем наш путь по карте
    private fun addLatestPolyline(){ // функция которая соединяет последнию точку со второй последней точкой
        if(pathPoints.isNotEmpty() && pathPoints.last().size > 1){ // проверяем наш список не пустой и он больше чем 1 элемент (нам нужно 2 точки т.е 2 элемента)
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2] // последний первый элемент
            val lastLatLng = pathPoints.last().last() // последний второй элемент
            val polylineOptions = PolylineOptions(). // как будет выгледить наша линия
                color(resources.getColor(Constants.POLYLINE_COLOR)) // какого цвета будет наша линия
                .width(Constants.POLYLINE_WIDTH) // какой ширины
                .add(preLastLatLng) // добавляем первую последнюю точку
                .add(lastLatLng) // добавляем второю последнюю точку

            map?.addPolyline(polylineOptions) // добавить линию
        }
    }

    private fun addAllPolyline(){ // функция которая рисует заново линию после переворота устройства
        for(polyline in pathPoints){
            val polylineOptions = PolylineOptions()
                .color(Constants.POLYLINE_COLOR) // какого цвета будет наша линия
                .width(Constants.POLYLINE_WIDTH) // какой ширины
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun moveCameraToUser(){ // перемещение камеры
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),// приближать будем на последнюю последнюю точку
                    Constants.CAMERA_ZOOM // маштабирование
                )
            )
        }
    }

// функция которая взаимодествует с сервесом при нажатии на стоп сервис останавливается при нажатии на старт востанавливается
    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if(!isTracking && curTimeMiles> 0L){
            btnToggleRun.text = resources.getString(R.string.start)
            btnFinishRun.visibility = View.VISIBLE
        }else if(isTracking){
            btnToggleRun.text = resources.getString(R.string.stop)
            menu?.getItem(0)?.isVisible = true
            btnFinishRun.visibility = View.GONE
        }
    }

    // функция которая останавливает сервис или включает его
    private fun toggleRun() {
        if(isTracking){
            menu?.getItem(0)?.isVisible = true // появляется после того как нажали на старт нажали на стоп и нажали на старт тогда еще раз появилась
            sendTrackingService(Constants.ACTION_PAUSE_SERVICE) // если isTracking равен будет false тогда сервис приостановиться
        } else {
            sendTrackingService(Constants.ACTION_START_OR_RESUME_SERVICE) // если isTracking равен будет true тогда сервис заработает
        }
    }

    // функция котрая передает соответствующую команду для нашего сервиса
    private fun sendTrackingService(action: String) =
        Intent(requireContext(), TrackingServices::class.java).also {
            it.action =action
            requireContext().startService(it)
        }


    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.Builder()
        for(polyline in pathPoints){
            for(position in polyline){
                bounds.include(position)
            }
        }
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                binding.mapView.width,
                binding.mapView.height,
                (binding.mapView.height * 0.05f).toInt()
            )
        )
    }

    // сохранение в базу данных
    private fun endRunAndSaveToDb(){
        map?.snapshot { bitmap ->
        var distanceInMetres = 0
            for(polyline in pathPoints){
                distanceInMetres += TrackingUtility.calculatePolylineDictation(polyline).toInt()
            }
            //val avgSpeed = round((distanceInMetres / 1000f) / (curTimeMiles / 1000f / 60 / 60) * 10) / 10f
            val avgSpeed = TrackingUtility.calculateAVGSpeed(distanceInMetres, curTimeMiles)
            val dateTimestamp = Calendar.getInstance().timeInMillis // текущее время
            //val caloriesBurned = ((distanceInMetres / 1000f) * weight).toInt()
            val caloriesBurned = TrackingUtility.calculateCaloriesBurned(weight, distanceInMetres)
            val run = Run(bitmap, dateTimestamp, avgSpeed, distanceInMetres, curTimeMiles, caloriesBurned)
            viewModel.insert(run)
            Toast.makeText(requireContext(), "date save", Toast.LENGTH_SHORT).show()
            stopRun()
        }
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}