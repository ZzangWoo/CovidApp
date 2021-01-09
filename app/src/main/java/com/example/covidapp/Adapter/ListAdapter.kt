package com.example.covidapp.Adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.covidapp.Entity.CovidInfo
import com.example.covidapp.R
import com.example.covidapp.Room.Database.CovidDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.add_dialog.view.*
import kotlinx.android.synthetic.main.list_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ListAdapter (val context: Context, var covidInfoList: List<CovidInfo>)
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
            itemView.countryNameTextView.setText(covidInfo.countryName)
            itemView.confirmedCountTextView.setText(covidInfo.confirmedCount.toString())
            itemView.deathCountTextView.setText(covidInfo.deathCount.toString())
            itemView.cardView.setOnClickListener {
                var toast = Toast.makeText(context, covidInfo.countryName, Toast.LENGTH_SHORT)
                toast.show()
            }
            itemView.statusImageView.setImageResource(R.drawable.happy)
            
        }

        fun editData(name: String) {
            Thread {

            }
        }
    }

}