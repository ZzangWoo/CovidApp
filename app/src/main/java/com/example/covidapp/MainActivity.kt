package com.example.covidapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import com.example.covidapp.Adapter.ListAdapter
import com.example.covidapp.Dialog.AddDialog
import com.example.covidapp.Entity.CountryEntity.CountryName
import com.example.covidapp.Entity.CovidInfo
import com.example.covidapp.Room.Database.CovidDatabase
import com.example.covidapp.Room.Entity.Country
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_item.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private var adapter: ListAdapter? = null

    private var db: CovidDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        db = CovidDatabase.getInstance(this)

        //testInitalize()

        addFloatingActionButton.setOnClickListener {
            val dlg = AddDialog(this)
            dlg.makeBorderCorner()
            dlg.start("추가할 나라를 선택해주세요.")
        }

        refreshImageView.setOnClickListener {

        }

        db!!.countryDao().countryLiveSelect().observe(this, androidx.lifecycle.Observer {
            var covidInfoList: MutableList<CovidInfo> = mutableListOf()
            for (name in it) {
                val index: Int = CountryName.countryList.indexOf(name.countryName)

                covidInfoList.add(CovidInfo(name.countryName, 0, 0))
            }
            adapter = ListAdapter(this, covidInfoList)
            recyclerView.adapter = adapter
        })
    }

    private fun testInitalize() {
        var countryList: List<Country> = arrayListOf()
        var covidInfoList: MutableList<CovidInfo> = mutableListOf()

        try {
            adapter = ListAdapter(this, covidInfoList)

            GlobalScope.launch(Dispatchers.Default) {
                countryList = db!!.countryDao().countrySelect()

                for (country in countryList) {
                    covidInfoList.add(CovidInfo(country.countryName, 0, 0))
                }

                Log.d("[코로나 정보 넘어왔다]", "" + covidInfoList)
                adapter?.addItem(covidInfoList)
                recyclerView.adapter = adapter
            }
//            countryList = db!!.countryDao().countrySelect()
//
//            for (country in countryList) {
//                covidInfoList.add(CovidInfo(country.countryName, 0, 0))
//            }
//
//            Log.d("[코로나 정보 넘어왔다]", "" + covidInfoList)
//            adapter?.addItem(covidInfoList)
//            recyclerView.adapter = adapter
        } catch (e: Exception) {

        }

    }
}
