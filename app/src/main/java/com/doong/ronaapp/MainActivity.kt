package com.doong.ronaapp

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.doong.ronaapp.Adapter.ListAdapter
import com.doong.ronaapp.Dialog.AddDialog
import com.doong.ronaapp.Dialog.LoadingActivity
import com.doong.ronaapp.Entity.API_Entity.SomeCovidEntity
import com.doong.ronaapp.Entity.API_Entity.WorldStats
import com.doong.ronaapp.Entity.API_Entity.YesterdayCovidEntity
import com.doong.ronaapp.Entity.CountryEntity.CountryName
import com.doong.ronaapp.Entity.CountryEntity.ISO2
import com.doong.ronaapp.Entity.CountryEntity.Slug
import com.doong.ronaapp.Entity.CovidInfo
import com.doong.ronaapp.Entity.CovidInfoText
import com.doong.ronaapp.Entity.LocaleEntity
import com.doong.ronaapp.Manager.LocaleManager
import com.doong.ronaapp.Repository.WholeCovidRepo
import com.doong.ronaapp.Repository.YesterdayCovidRepo
import com.doong.ronaapp.Room.Database.CovidDatabase
import com.doong.ronaapp.Room.Entity.ApiLog
import com.doong.ronaapp.Room.Entity.Country
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MainActivity : AppCompatActivity() {

    // 로딩 다이얼로그
//    lateinit var loading: LoadingActivity

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
            var loading = LoadingActivity(this)
            loading.show()

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
                    GlobalScope.launch {
                        var isNoDataCountry: Boolean = false
                        var noDataCountryName: String = ""

                        val apiResult = response.body()
                        statsList = apiResult?.stats
                        val updatesList = apiResult?.updates

                        val currentDateTime = Calendar.getInstance()
                        currentDateTime.time = Date()
                        currentDateTime.add(Calendar.DATE, -1)

                        var dateFormat = SimpleDateFormat("yyyy-MM-dd").format(currentDateTime.time)

                        currentDateTime.add(Calendar.DATE, -1)
                        val twoDaysAgo = currentDateTime.time
                        currentDateTime.add(Calendar.DATE, -1)
                        val threeDaysAgo = currentDateTime.time

                        val paramForSome = mutableMapOf<String, String>(
                            "from" to SimpleDateFormat("yyyy-MM-dd").format(threeDaysAgo.time),
                            "to" to SimpleDateFormat("yyyy-MM-dd").format(twoDaysAgo.time)
                        )

                        GlobalScope.launch(Dispatchers.IO) {
                            for (name in it) {
                                val index: Int =
                                    CountryName.countryList.indexOf(name.countryName)
                                val countryName = name.countryName
                                val slug = Slug.slugList[index]
                                val iso2 = ISO2.iso2List[index]

                                Log.e("[데이터 확인]", "${countryName} : ${slug}")

                                var data: List<SomeCovidEntity>? = null
                                var resultForSome: List<SomeCovidEntity>? = null
                                var isUpper: Boolean = true

                                // 가져온 데이터에서 DB에 있는 나라있는지 확인
                                if (statsList!![iso2] != null) {
                                    Log.e("[가져온 데이터]", "${updatesList!!}")
                                    // 최근 데이터 있는지 확인
                                    if (updatesList!!.any { it.country == iso2 }) {

                                        // DB에 2일전 정보 저장되어있는지 확인
                                        // 없으면 Insert 있으면 Select
                                        var apiLogInfo = db!!.logDao().logSelectCount(slug, paramForSome["to"]!!)
//                                        var apiLogInfo = db!!.logDao().logSelect()
                                        Log.e("[DB 테스트]", "${slug} 데이터 : ${apiLogInfo}")

                                        var difference: Int = 0 // 2일전, 1일전 확진자 수 차이

                                        if (apiLogInfo == null) {
                                            resultForSome = getCovidData(slug, paramForSome)
                                            Log.e("Listener", "나라 정보(밖) : ${resultForSome}")

                                            if (resultForSome!!.size != 0) {
                                                val casesTwoDaysAgo: Int = resultForSome[1].cases - resultForSome[0].cases
                                                difference = statsList!![iso2]!!.casesDelta - casesTwoDaysAgo

                                                if (difference >= 0) {
                                                    isUpper = true
                                                } else {
                                                    isUpper = false
                                                }

                                                Log.e("[나라 case 차이(저장X)]", "${slug} : ${difference}\n2일전 : ${casesTwoDaysAgo} | 하루전 : ${statsList!![iso2]!!.casesDelta}")

                                                db!!.logDao().logInsert(ApiLog(slug, casesTwoDaysAgo, resultForSome[1].date.substring(0, 10)))
                                            }
                                        } else {
                                            difference = statsList!![iso2]!!.casesDelta - apiLogInfo.casesTwoDaysAgo

                                            if (difference >= 0) {
                                                isUpper = true
                                            } else {
                                                isUpper = false
                                            }

                                            Log.e("[나라 case 차이(저장O)]", "${slug} : ${difference}\n2일전 : ${apiLogInfo.casesTwoDaysAgo} | 하루전 : ${statsList!![iso2]!!.casesDelta}")
                                        }

                                        covidInfoList.add(
                                            CovidInfo(
                                                name.countryName,
                                                dateFormat,
                                                isUpper,
                                                statsList!![iso2]!!.cases,
                                                statsList!![iso2]!!.casesDelta,
                                                statsList!![iso2]!!.deaths,
                                                statsList!![iso2]!!.deathsDelta
                                            )
                                        )
                                    } else {
                                        covidInfoList.add(
                                            CovidInfo(
                                                name.countryName,
                                                dateFormat,
                                                isUpper,
                                                statsList!![iso2]!!.cases,
                                                statsList!![iso2]!!.casesDelta,
                                                statsList!![iso2]!!.deaths,
                                                statsList!![iso2]!!.deathsDelta
                                            )
                                        )
                                    }
                                } else {
                                    db!!.countryDao().countryDelete(name)

                                    isNoDataCountry = true
                                    noDataCountryName = name.countryName
                                }

                            }
                        }.join()

                        GlobalScope.launch(Dispatchers.Main) {
                            Log.e("[UI 갱신해라]", "얍얍얍얍얍")
                            adapter = ListAdapter(
                                applicationContext, covidInfoList, db!!, CovidInfoText(
                                    getString(R.string.lbl_wholeConfirmedCount),
                                    getString(R.string.lbl_recentConfimedCount),
                                    getString(R.string.lbl_wholeDeathCount),
                                    getString(R.string.lbl_recentDeathCount),
                                    getString(R.string.msg_deleteComplete)
                                )
                            )
                            recyclerView.adapter = adapter

                            if (isNoDataCountry) {
                                Toast.makeText(this@MainActivity, "${noDataCountryName} doesn't suggest data", Toast.LENGTH_SHORT).show()
                            }

                            if (loading != null && loading.isShowing()) {
                                loading.dismiss()
                            }
                        }
                    }
                }
            })
        })
    }

    suspend fun getCovidData(slug: String, param: Map<String, String>): List<SomeCovidEntity>? = suspendCoroutine { continuation ->


        val builderForSome = Retrofit.Builder()
            .baseUrl("https://api.covid19api.com/")
            .addConverterFactory(GsonConverterFactory.create())

        val retrofitForSome = builderForSome.build()
        val repoForSome = retrofitForSome.create(WholeCovidRepo::class.java)
        val callForSome = repoForSome.getStatus(slug, param)

        callForSome.enqueue(object: Callback<List<SomeCovidEntity>> {
            override fun onFailure(call: Call<List<SomeCovidEntity>>, t: Throwable) {
                Log.e("[API 에러발생]", "${t}")

                continuation.resumeWithException(t)
            }

            override fun onResponse(
                call: Call<List<SomeCovidEntity>>,
                response: Response<List<SomeCovidEntity>>
            ) {
                val result = response.body()

                Log.e("Listener", "나라 정보(안) : ${result}")

                continuation.resume(result)
            }

        })
    }
}
