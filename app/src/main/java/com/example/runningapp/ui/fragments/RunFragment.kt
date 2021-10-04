package com.example.runningapp.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.runningapp.R
import com.example.runningapp.adapters.RunAdapter
import com.example.runningapp.base.BaseFragment
import com.example.runningapp.databinding.FragmentRunBinding
import com.example.runningapp.other.Constants
import com.example.runningapp.other.TrackingUtility
import com.example.runningapp.sorted.SortedRuns
import com.example.runningapp.ui.viewModels.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@Suppress("DEPRECATION")
@AndroidEntryPoint
class RunFragment : BaseFragment<FragmentRunBinding>(), EasyPermissions.PermissionCallbacks{
   private val viewModel: MainViewModel by viewModels()
    lateinit var adapter: RunAdapter

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentRunBinding.inflate(inflater,container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions()
//        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation).visibility = View.VISIBLE
        initAdapter()
        deleteRun()
        when(viewModel.sortType){
            SortedRuns.DATE -> binding.spFilter.setSelection(0)
            SortedRuns.CALORIES_BURNED -> binding.spFilter.setSelection(1)
            SortedRuns.DISTANCE -> binding.spFilter.setSelection(2)
            SortedRuns.RUNNING_TIME -> binding.spFilter.setSelection(3)
            SortedRuns.AVG_SPEED -> binding.spFilter.setSelection(4)
        }
        viewModel.runs.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }
        binding.spFilter.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
               when(position){
                   0 -> viewModel.sortDate(SortedRuns.DATE)
                   1 -> viewModel.sortDate(SortedRuns.CALORIES_BURNED)
                   2 -> viewModel.sortDate(SortedRuns.DISTANCE)
                   3 -> viewModel.sortDate(SortedRuns.RUNNING_TIME)
                   4 -> viewModel.sortDate(SortedRuns.AVG_SPEED)
               }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }
    }

    private fun initAdapter(){
        adapter = RunAdapter()
        binding.rvRuns.adapter = adapter
    }

    private fun deleteRun() {
        val itemTouchHelperCallBack = object: ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN
            , ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }
            @SuppressLint("ShowToast")
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val runDate = adapter.differ.currentList[position]
                viewModel.delete(runDate)
                Snackbar.make(requireView(), "aricle successfull delete", Snackbar.LENGTH_LONG).apply { // прописываем снак для того чтобы у пользоваля было время передумать удалять статью и нажать Undo
                    setAction("Undo"){
                        viewModel.insert(runDate) // сново сохраняем данные
                    }
                    show()
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(binding.rvRuns)
        }
    }

    // Permissions
    private fun requestPermissions() {
        if(TrackingUtility.hasLocationPermission(requireContext())) {
            return
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                resources.getString(R.string.warning),
                Constants.REQUSET_CODE_PERMISION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                resources.getString(R.string.warning),
                Constants.REQUSET_CODE_PERMISION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}