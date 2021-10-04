package com.example.runningapp.ui.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.runningapp.R
import com.example.runningapp.base.BaseFragment
import com.example.runningapp.databinding.FragmentSetupBinding
import com.example.runningapp.other.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : BaseFragment<FragmentSetupBinding>(){

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @set: Inject // потому что булеон притмитивный тип данных а не объект поэтому используем конструкцию @set: Inject
    var isFirstAppOpen = true

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSetupBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation).visibility = View.GONE
        if(!isFirstAppOpen){
                val navOptions = NavOptions.Builder() // очищаем стек
                    .setPopUpTo(R.id.setupFragment, true)
                    .build()
                findNavController().navigate(R.id.action_setupFragment_to_runFragment,
                    savedInstanceState, // чтобы передать наш экземпляр класа sharedPref
                    navOptions
                )
        }
        binding.tvContinue.setOnClickListener {
            val successes = writePersonDateShared()
            if (successes){
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            } else {
                toast("Please written all field")
            }
        }
        binding.fragmentSetup.setOnClickListener {
            binding.editWeight.hideKeyboard()
        }
    }
    @SuppressLint("CommitPrefEdits")
    private fun writePersonDateShared(): Boolean {
        val name = binding.editName.text.toString()
        val weight = binding.editWeight.text.toString()
        if(name.isEmpty() || weight.isEmpty()){
            return false
        }
        val editor = sharedPreferences.edit()
        editor.apply{
            putString(Constants.KEY_NAME, name)
            putFloat(Constants.KEY_WEIGHT, weight.toFloat())
            putBoolean(Constants.FIRST_TIME_TOGGLE, false)
            apply()
        }
        val toolBar = "Let s go $name" // сохраняем имя в тулБар
        requireActivity().tvToolbarTitle.text = toolBar
        return true
    }
}