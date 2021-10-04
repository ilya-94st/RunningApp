package com.example.runningapp.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.runningapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TracingFragmentDialog(): DialogFragment() {

    private var yesListner: (()->Unit) ? = null

    fun setListner(listner: () -> Unit){
        yesListner = listner
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Cancel Run?")
            .setMessage("Are you sure to cancel the current run and delete all date?")

            .setIcon(R.drawable.ic_baseline_delete_24)
            .setPositiveButton(
                "Yes"
            ) { _ , _ ->
                yesListner?.let {yes->
                    yes()
                }
            }
            .setNegativeButton("No"){
                    dialog,_ ->
                dialog.cancel()
            }
            .create()
        builder.window?.setBackgroundDrawableResource(R.color.blue)
        return builder
    }
}