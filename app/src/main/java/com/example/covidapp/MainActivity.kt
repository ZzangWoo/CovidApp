package com.example.covidapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import com.example.covidapp.Adapter.ListAdapter
import com.example.covidapp.Dialog.AddDialog
import com.example.covidapp.Entity.API_Entity.WorldStats
import com.example.covidapp.Entity.API_Entity.YesterdayCovidEntity
import com.example.covidapp.Entity.CountryEntity.CountryName
import com.example.covidapp.Entity.CountryEntity.ISO2
import com.example.covidapp.Entity.CountryEntity.Slug
import com.example.covidapp.Entity.CovidInfo
import com.example.covidapp.Entity.CovidInfoText
import com.example.covidapp.Entity.LocaleEntity
import com.example.covidapp.Manager.LocaleManager
import com.example.covidapp.Repository.YesterdayCovidRepo
import com.example.covidapp.Room.Database.CovidDatabase
import com.example.covidapp.Room.Entity.Country
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_item.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private var adapter: ListAdapter? = null
    private var covidInfoList: MutableList<CovidInfo> = mutableListOf()
    private var mainContext: Context = this

    private var db: CovidDatabase? = null

    private var localNumber: Int = 0

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleManager.setLocale(newBase!!))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        db = CovidDatabase.getInstance(this)


        if (App.prefs.locale.isNullOrEmpty()) {
            // 기기 국가 설정
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LocaleEntity.locale = applicationContext.resources.configuration.locales.get(0)
            } else {
                LocaleEntity.locale = applicationContext.resources.configuration.locale
            }
        } else {
//            val res = resources
//            val dm = res.displayMetrics
//            val conf = res.configuration
//
//            if (App.prefs.locale == "kr") {
//                conf.locale = Locale.KOREA
//            } else if (App.prefs.locale == "ja") {
//                conf.locale = Locale.JAPAN
//            } else {
//                conf.locale = Locale.US
//            }
//            resources.updateConfiguration(conf, dm)
        }

        if (App.prefs.locale == "ko") {
            localNumber = 1;
//            App.prefs.locale = "kr"
        } else if (App.prefs.locale == "ja") {
            localNumber = 2;
//            App.prefs.locale = "ja"
        } else {
            localNumber = 0;
//            App.prefs.locale = "en"
        }

        languageSelectSpinner.setSelection(localNumber)


        addFloatingActionButton.setOnClickListener {
            val dlg = AddDialog(this)
            dlg.makeBorderCorner()
            dlg.start(getString(R.string.msg_selectCountry))
        }

        refreshImageView.setOnClickListener {
            if (covidInfoList != null && covidInfoList.size != 0) {
                Thread {
                    db!!.countryDao().countryUpdate(Country(covidInfoList[0].countryName))
                }.start()
                Toast.makeText(this, getString(R.string.msg_refreshComplete), Toast.LENGTH_SHORT).show()
            }
        }

        languageSelectSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                languageSelectSpinner.setSelection(localNumber)
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != localNumber) {
                    var language: String = ""

                    when (position) {
                        0 -> language = "en"
                        1 -> language = "ko"
                        2 -> language = "ja"
                    }

                    App.prefs.locale = language

                    LocaleManager.setNewLocale(mainContext, language)
                    recreate()

//                    val res = resources
//                    val dm = res.displayMetrics
//                    val conf = res.configuration
//
//                    if (App.prefs.locale == "kr") {
//                        conf.locale = Locale.KOREA
//                    } else if (App.prefs.locale == "ja") {
//                        conf.locale = Locale.JAPAN
//                    } else {
//                        conf.locale = Locale.US
//                    }
//                    resources.updateConfiguration(conf, dm)
//
//                    val intent = baseContext.packageManager
//                        .getLaunchIntentForPackage(baseContext.packageName)
//                    intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                    intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    finish()
//                    startActivity(intent)

                }
            }

        }

        // db 변동 감지 후 나라
        db!!.countryDao().countryLiveSelect().observe(this, androidx.lifecycle.Observer {
            covidInfoList = mutableListOf()
            var statsList: Map<String, WorldStats>? = null

            val builder = Retrofit.Builder()
                .baseUrl("https://apiv2.corona-live.com/")
                .addConverterFactory(GsonConverterFactory.create())

            val retrofit = builder.build()

            val repo = retrofit.create(YesterdayCovidRepo::class.java)

            val param = mutableMapOf<String, String>(
                "timestamp" to System.currentTimeMillis().toString()
            )
            val call = repo.getStatus(param)

            call.enqueue(object: Callback<YesterdayCovidEntity> {
                override fun onFailure(call: Call<YesterdayCovidEntity>, t: Throwable) {
                    Log.e("Listener", "API GET 방식 통신 실패 : " + t)
                }

                override fun onResponse(call: Call<YesterdayCovidEntity>, response: Response<YesterdayCovidEntity>) {
                    val apiResult = response.body()
                    statsList = apiResult?.stats
                    val updatesList = apiResult?.updates

                    val currentDateTime = Calendar.getInstance()
                    currentDateTime.time = Date()
                    currentDateTime.add(Calendar.DATE, -1)

                    var dateFormat = SimpleDateFormat("yyyy-MM-dd").format(currentDateTime.time)

                    for (name in it) {
                        val index: Int = CountryName.countryList.indexOf(name.countryName)
                        val countryName = name.countryName
                        val slug = Slug.slugList[index]
                        val iso2 = ISO2.iso2List[index]

                        if (statsList!![iso2] != null) {
                            if (updatesList!!.any { it.country == iso2}) {
                                covidInfoList.add(CovidInfo(name.countryName,
                                    dateFormat,
                                    statsList!![iso2]!!.cases,
                                    statsList!![iso2]!!.casesDelta,
                                    statsList!![iso2]!!.deaths,
                                    statsList!![iso2]!!.deathsDelta))
                            } else {
                                covidInfoList.add(CovidInfo(name.countryName,
                                    "Not Update",
                                    statsList!![iso2]!!.cases,
                                    statsList!![iso2]!!.casesDelta,
                                    statsList!![iso2]!!.deaths,
                                    statsList!![iso2]!!.deathsDelta))
                            }

                        }

                    }

                    adapter = ListAdapter(applicationContext, covidInfoList, db!!, CovidInfoText(
                        getString(R.string.lbl_wholeConfirmedCount),
                        getString(R.string.lbl_recentConfimedCount),
                        getString(R.string.lbl_wholeDeathCount),
                        getString(R.string.lbl_recentDeathCount),
                        getString(R.string.msg_deleteComplete)
                    ))
                    recyclerView.adapter = adapter
                }
            })
        })
    }

//    private fun testInitalize() {
//        var countryList: List<Country> = arrayListOf()
//        var covidInfoList: MutableList<CovidInfo> = mutableListOf()
//
//        try {
//            adapter = ListAdapter(this, covidInfoList, db!!)
//
//            GlobalScope.launch(Dispatchers.Default) {
//                countryList = db!!.countryDao().countrySelect()
//
//                for (country in countryList) {
//                    covidInfoList.add(CovidInfo(country.countryName, 0, 0))
//                }
//
//                Log.d("[코로나 정보 넘어왔다]", "" + covidInfoList)
//                adapter?.addItem(covidInfoList)
//                recyclerView.adapter = adapter
//            }
////            countryList = db!!.countryDao().countrySelect()
////
////            for (country in countryList) {
////                covidInfoList.add(CovidInfo(country.countryName, 0, 0))
////            }
////
////            Log.d("[코로나 정보 넘어왔다]", "" + covidInfoList)
////            adapter?.addItem(covidInfoList)
////            recyclerView.adapter = adapter
//        } catch (e: Exception) {
//
//        }
//
//    }
}
