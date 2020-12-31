package com.example.covidapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import com.example.covidapp.Adapter.ListAdapter
import com.example.covidapp.Entity.CovidInfo
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_item.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var covidInfo1: CovidInfo = CovidInfo("한국", 100, 1)
        var covidInfo2: CovidInfo = CovidInfo("미국", 10000, 100)
        var covidInfo3: CovidInfo = CovidInfo("중국", 100000, 900)
        var covidInfo4: CovidInfo = CovidInfo("일본", 4000, 450)

        var covidInfoList: ArrayList<CovidInfo> = arrayListOf()
        covidInfoList.add(covidInfo1)
        covidInfoList.add(covidInfo2)
        covidInfoList.add(covidInfo3)
        covidInfoList.add(covidInfo4)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val adapter = ListAdapter(this, covidInfoList)
        recyclerView.adapter = adapter
    }
}
