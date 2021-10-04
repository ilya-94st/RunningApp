package com.example.runningapp.ui.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.runningapp.base.BaseFragment
import com.example.runningapp.databinding.FragmentSettingsBinding
import com.example.runningapp.other.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSettingsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      //  requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation).visibility = View.VISIBLE
        loadDateSharedPref()
        binding.fragmentSettings.setOnClickListener {
            binding.editWeight.hideKeyboard()
        }
        binding.btnApplyChanges.setOnClickListener {
            val success = applyChangesSharedPref()
            if(success){
                toast("ok")
            } else {
                toast("please enter in field")
            }
        }
    }

    private fun loadDateSharedPref() { // функция которая устанавливает по умолчанию занчения в наш editText
        val name = sharedPreferences.getString(Constants.KEY_NAME, "")
        val weight = sharedPreferences.getFloat(Constants.KEY_WEIGHT, 80f)
        binding.editWeight.setText(weight.toString())
        binding.editName.setText(name)
    }

    @SuppressLint("CommitPrefEdits")
   private fun applyChangesSharedPref(): Boolean {
        val nameText = binding.editName.text.toString()
        val weightText = binding.editWeight.text.toString()
        if (nameText.isEmpty() || weightText.isEmpty()){
            return false
        }else {
            sharedPreferences.edit()
                .apply {
                    putString(Constants.KEY_NAME, nameText)
                    putFloat(Constants.KEY_WEIGHT, weightText.toFloat())
                    apply()
                }
            val toolBar = "Let s go ${nameText}" // сохраняем имя в тулБар
            requireActivity().tvToolbarTitle.text = toolBar
        }
        return true
    }

}