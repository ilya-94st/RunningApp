package com.example.runningapp.tools

import android.app.AlertDialog
import android.content.Context


fun Context.showAlertDialog(title: String, message: String) {
    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
    builder.setTitle(title)
        .setMessage(message)
        .setPositiveButton(
            "OK"
        ) { dialog, _ ->
            dialog.cancel()
        }
    builder.create().show()
}