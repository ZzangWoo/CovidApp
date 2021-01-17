package com.doong.covidapp

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.doong.covidapp.Adapter.ListAdapter
import com.doong.covidapp.Dialog.AddDialog
import com.doong.covidapp.Entity.API_Entity.SomeCovidEntity
import com.doong.covidapp.Entity.API_Entity.WorldStats
import com.doong.covidapp.Entity.API_Entity.YesterdayCovidEntity
import com.doong.covidapp.Entity.CountryEntity.CountryName
import com.doong.covidapp.Entity.CountryEntity.ISO2
import com.doong.covidapp.Entity.CountryEntity.Slug
import com.doong.covidapp.Entity.CovidInfo
import com.doong.covidapp.Entity.CovidInfoText
import com.doong.covidapp.Entity.LocaleEntity
import com.doong.covidapp.Manager.LocaleManager
import com.doong.covidapp.Repository.WholeCovidRepo
import com.doong.covidapp.Repository.YesterdayCovidRepo
import com.doong.covidapp.Room.Database.CovidDatabase
import com.doong.covidapp.Room.Entity.Country
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var mAdView : AdView

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

        // 광고
        MobileAds.initialize(this) {}

        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)


        if (App.prefs.locale.isNullOrEmpty()) {
            // 기기 국가 설정
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LocaleEntity.locale = applicationContext.resources.configuration.locales.get(0)
            } else {
                LocaleEntity.locale = applicationContext.resources.configuration.locale
            }
        } else {
        }

        // 핸드폰에 저장된 언어 Spinner에 반영
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

        // 새로고침 버튼 클릭 이벤트
        refreshImageView.setOnClickListener {
            if (covidInfoList != null && covidInfoList.size != 0) {
                Thread {
                    db!!.countryDao().countryUpdate(Country(covidInfoList[0].countryName))
                }.start()
                Toast.makeText(this, getString(R.string.msg_refreshComplete), Toast.LENGTH_SHORT).show()
            }
        }

        // 언어 선택 spinner 이벤트
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
                                    slug,
                                    false,
                                    statsList!![iso2]!!.cases,
                                    statsList!![iso2]!!.casesDelta,
                                    statsList!![iso2]!!.deaths,
                                    statsList!![iso2]!!.deathsDelta))
                            } else {
                                covidInfoList.add(CovidInfo(name.countryName,
                                    "Not Update",
                                    slug,
                                    false,
                                    statsList!![iso2]!!.cases,
                                    statsList!![iso2]!!.casesDelta,
                                    statsList!![iso2]!!.deaths,
                                    statsList!![iso2]!!.deathsDelta))
                            }
                        }
                    }

                    val builderForSome = Retrofit.Builder()
                        .baseUrl("https://api.covid19api.com/")
                        .addConverterFactory(GsonConverterFactory.create())

                    val retrofitForSome = builderForSome.build()
                    val repoForSome = retrofitForSome.create(WholeCovidRepo::class.java)

                    currentDateTime.add(Calendar.DATE,-1)
                    val twoDaysAgo = currentDateTime.time
                    currentDateTime.add(Calendar.DATE,-1)
                    val threeDaysAgo = currentDateTime.time

                    val paramForSome = mutableMapOf<String, String>(
                        "from" to SimpleDateFormat("yyyy-MM-dd").format(threeDaysAgo.time),
                        "to" to SimpleDateFormat("yyyy-MM-dd").format(twoDaysAgo.time)
                    )



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
}
