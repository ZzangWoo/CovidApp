package com.example.covidapp.Dialog

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.*
import com.example.covidapp.R

class AddDialog (context: Context) {

    private val dlg = Dialog(context)
    private lateinit var titleTextView: TextView
    private lateinit var listSpinner: Spinner
    private lateinit var leftButton: Button
    private lateinit var rightButton: Button
    private lateinit var listener: AddDialogOKClickedListener

    fun start(content: String) {
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(R.layout.add_dialog)
        dlg.setCancelable(false)

        titleTextView = dlg.findViewById(R.id.titleTextView)
        titleTextView.text = content

        listSpinner = dlg.findViewById(R.id.listSpinner)

        leftButton = dlg.findViewById(R.id.leftButton)
        leftButton.text = "확인"
        leftButton.setOnClickListener {

            dlg.dismiss()
        }

        rightButton = dlg.findViewById(R.id.rightButton)
        rightButton.text = "취소"
        rightButton.setOnClickListener {

            dlg.dismiss()
        }

        dlg.show()
    }

    interface AddDialogOKClickedListener {
        fun onOKClicked(content: String)
    }
}


