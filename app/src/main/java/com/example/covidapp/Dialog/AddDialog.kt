package com.example.covidapp.Dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window
import android.widget.*
import com.example.covidapp.Entity.CountryEntity.CountryName
import com.example.covidapp.R
import com.example.covidapp.Room.Database.CovidDatabase
import com.example.covidapp.Room.Entity.Country
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class AddDialog (context: Context) {

    private val dlg = Dialog(context)
    private lateinit var titleTextView: TextView
    private lateinit var listSpinner: Spinner
    private lateinit var leftButton: Button
    private lateinit var rightButton: Button
    private lateinit var listener: AddDialogOKClickedListener

    private val context = context

    fun makeBorderCorner() {
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dlg.window?.requestFeature(Window.FEATURE_NO_TITLE)
    }

    fun start(content: String) {
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(R.layout.add_dialog)
        dlg.setCancelable(false)

        // DB 객체 생성
        val db = CovidDatabase.getInstance(context)

        titleTextView = dlg.findViewById(R.id.titleTextView)
        titleTextView.text = content

        listSpinner = dlg.findViewById(R.id.listSpinner)
        listSpinner.adapter = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, CountryName.countryList)

        leftButton = dlg.findViewById(R.id.leftButton)
        leftButton.text = context.getString(R.string.lbl_cancel)
        leftButton.setOnClickListener {



            dlg.dismiss()
        }

        rightButton = dlg.findViewById(R.id.rightButton)
        rightButton.text = context.getString(R.string.lbl_confirm)
        rightButton.setOnClickListener {

            try {
                GlobalScope.launch(Dispatchers.IO) {
                    db!!.countryDao().countryInsert(Country(listSpinner.selectedItem.toString()))

                    Log.d("[DB 성공]", "Country : ${listSpinner.selectedItem.toString()}")
                }
            } catch (e: Exception) {
                Log.e("[에러발생]", e.toString())
            }

            dlg.dismiss()
        }

        dlg.show()
    }

    interface AddDialogOKClickedListener {
        fun onOKClicked(content: String)
    }
}


