package com.doong.ronaapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.doong.ronaapp.Entity.CountryEntity.CountryName
import com.doong.ronaapp.Entity.CountryEntity.ISO2
import com.doong.ronaapp.Entity.CountryEntity.Slug
import com.doong.ronaapp.Entity.CovidInfo
import com.doong.ronaapp.Entity.CovidInfoText
import com.doong.ronaapp.R
import com.doong.ronaapp.Room.Database.CovidDatabase
import com.doong.ronaapp.Room.Entity.Country
import kotlinx.android.synthetic.main.list_item.view.*
import java.text.DecimalFormat

class ListAdapter (val context: Context, var covidInfoList: List<CovidInfo>, val db: CovidDatabase, val covidInfoText: CovidInfoText)
    : RecyclerView.Adapter<ListAdapter.CardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter.CardViewHolder {
        var inflater = LayoutInflater.from(context)
        var view = inflater.inflate(R.layout.list_item, parent, false)
        return CardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return covidInfoList.size
    }

    override fun onBindViewHolder(holder: ListAdapter.CardViewHolder, position: Int) {
        holder.bind(covidInfoList!!.get(position), position)

//        holder.countryName.text = covidInfoList[position].countryName
//        holder.confirmedCount.text = covidInfoList[position].confirmedCount.toString()
//        holder.deathCount.text = covidInfoList[position].deathCount.toString()
//
//
//
//        holder.cardView.setOnClickListener {
//
//        }
    }

    fun addItem(refreshCovidInfoList: MutableList<CovidInfo>) {
        covidInfoList = mutableListOf()
        covidInfoList = refreshCovidInfoList
        notifyDataSetChanged()
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val countryName = itemView.findViewById<TextView>(R.id.countryNameTextView)
//        val confirmedCount = itemView.findViewById<TextView>(R.id.confirmedCountTextView)
//        val deathCount = itemView.findViewById<TextView>(R.id.deathCountTextView)
//        val cardView = itemView.findViewById<CardView>(R.id.cardView)
//        val statusImageView = itemView.findViewById<ImageView>(R.id.statusImageView)

        var index: Int? = null

        fun bind(covidInfo: CovidInfo, position: Int) {
            index = position

            itemView.wholeConfirmedTextView.setText(R.string.lbl_wholeConfirmedCount)

            itemView.countryNameTextView.setText(covidInfo.countryName)
            itemView.updatedDateTextView.setText(covidInfo.updatedDate)
            itemView.wholeConfirmedCountTextView.setText(intToString(covidInfo.wholeConfirmedCount))
            itemView.confirmedCountTextView.setText(intToString(covidInfo.confirmedCount))
            itemView.wholeDeathCountTextView.setText(intToString(covidInfo.wholeDeathCount))
            itemView.deathCountTextView.setText(intToString(covidInfo.deathCount))
            itemView.cardView.setOnClickListener {
                val index: Int = CountryName.countryList.indexOf(covidInfo.countryName)
                val countryName = covidInfo.countryName
                val slug = Slug.slugList[index]
                val iso2 = ISO2.iso2List[index]

//                var toast = Toast.makeText(
//                    context,
//                    "CountryName : ${countryName}\nSlug : ${slug}\nISO2 : ${iso2}",
//                    Toast.LENGTH_SHORT)

                var toast = Toast.makeText(
                    context,
                    "Developing",
                    Toast.LENGTH_SHORT)
                toast.show()

//                var toast = Toast.makeText(
//                    context,
//                    if(covidInfo.isUpper) "True" else "False",
//                    Toast.LENGTH_SHORT)
//                toast.show()
            }
            itemView.statusImageView.setImageResource(if(covidInfo.isUpper) R.drawable.sad else R.drawable.happy)
            itemView.deleteImageView.setOnClickListener {
                deleteData()
            }

            itemView.wholeConfirmedTextView.setText(covidInfoText.wholeConfirmedText)
            itemView.confirmedTextView.setText(covidInfoText.recentConfirmedText)
            itemView.wholeDeathTextView.setText(covidInfoText.wholeDeathText)
            itemView.deathTextView.setText(covidInfoText.recentDeathText)
        }

        fun deleteData() {
            Thread {
                index?.let { covidInfoList!!.get(it) }?.let { db.countryDao().countryDelete(Country(it.countryName)) }
            }.start()
            Toast.makeText(context, covidInfoText.deleteComplete, Toast.LENGTH_SHORT).show()
        }

        fun intToString(num: Int): String {
            val decimalFormat = DecimalFormat("#,###")
            return decimalFormat.format(num)
        }
    }

}