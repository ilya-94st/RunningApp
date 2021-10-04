package com.example.runningapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.runningapp.R
import com.example.runningapp.databinding.ActivityMainBinding
import com.example.runningapp.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
   // private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        navigateToTrackingFragmentIfNeeded(intent)
        createMenu()
        setSupportActionBar(binding.toolbar) // это функция нужна чтобы на нашем кастомном toolBar отображался крестик нашего меню!!!
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?) {
        if (intent?.action == ACTION_SHOW_TRACKING_FRAGMENT) { // когда запускается активити то он проверяет есть схож ли наш Интент действие с констаной, если да то мы переходим на соответсвующий фрагмент
           // val navHostFragment =supportFragmentManager.findFragmentById((R.id.navHostFragment)) as NavHostFragment // обязательно нужно обвернуть в NavHostFragment иначе наш navControler не найдет его
            navHostFragment.findNavController().navigate(R.id.action_global_trackingFragment)
        }
    }
    private fun createMenu() {
        binding.bottomNavigation.setupWithNavController(navHostFragment.findNavController())
            navHostFragment.findNavController()
            .addOnDestinationChangedListener { _, destination, _ ->
                when(destination.id) {
                    R.id.settingsFragment, R.id.runFragment, R.id.satisticsFragment ->
                        binding.bottomNavigation.visibility = View.VISIBLE
                    else -> binding.bottomNavigation.visibility = View.GONE
                }
            }

        //val navHostFragment =supportFragmentManager.findFragmentById((R.id.navHostFragment)) as NavHostFragment
      //  navController = navHostFragment.navController
     //   binding.bottomNavigation.setupWithNavController(navController)
     //   binding.bottomNavigation.setOnNavigationItemReselectedListener { /* NO-OP */ }
    //    binding.bottomNavigation.visibility = View.GONE
    }
}