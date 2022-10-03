package com.awais.raza.car.app.activity

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.awais.raza.car.app.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

//    override fun onBackPressed() {
//        val myQuittingDialogBox: AlertDialog =
//            AlertDialog.Builder(this) // set message, title, and icon
//                .setTitle("Exit")
//                .setMessage("Do you want to Exit")
//                .setPositiveButton(
//                    "Exit",
//                    DialogInterface.OnClickListener { dialog, whichButton -> //your deleting code
//                        dialog.dismiss()
//                        finish()
//                    })
//                .setNegativeButton("cancel",
//                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
//                .create()
//
//        myQuittingDialogBox.show()
//    }
}