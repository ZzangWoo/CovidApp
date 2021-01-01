package com.example.covidapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import com.example.covidapp.Adapter.ListAdapter
import com.example.covidapp.Dialog.AddDialog
import com.example.covidapp.Entity.CovidInfo
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_item.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        testInitalize()

        addFloatingActionButton.setOnClickListener {
            var toast = Toast.makeText(this, "Test", Toast.LENGTH_SHORT)
            toast.show()

            val dlg = AddDialog(this)
            dlg.start("추가할 나라를 선택해주세요.")
        }
    }

    private fun testInitalize() {
        var covidInfo1: CovidInfo = CovidInfo("한국", 100, 1)
        var covidInfo2: CovidInfo = CovidInfo("미국", 10000, 100)
        var covidInfo3: CovidInfo = CovidInfo("중국", 100000, 900)
        var covidInfo4: CovidInfo = CovidInfo("일본", 4000, 450)
        var covidInfo5: CovidInfo = CovidInfo("태국", 14000, 450)
        var covidInfo6: CovidInfo = CovidInfo("홍콩", 454000, 450)
        var covidInfo7: CovidInfo = CovidInfo("호주", 444000, 450)

        var covidInfoList: ArrayList<CovidInfo> = arrayListOf()
        covidInfoList.add(covidInfo1)
        covidInfoList.add(covidInfo2)
        covidInfoList.add(covidInfo3)
        covidInfoList.add(covidInfo4)
        covidInfoList.add(covidInfo5)
        covidInfoList.add(covidInfo6)
        covidInfoList.add(covidInfo7)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val adapter = ListAdapter(this, covidInfoList)
        recyclerView.adapter = adapter
    }
}
