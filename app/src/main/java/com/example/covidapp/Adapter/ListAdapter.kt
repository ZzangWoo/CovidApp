package com.example.covidapp.Adapter

import android.content.Context
import android.graphics.BitmapFactory
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

class ListAdapter (val context: Context, val covidInfoList: ArrayList<CovidInfo>)
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
        holder.countryName.text = covidInfoList[position].countryName
        holder.confirmedCount.text = covidInfoList[position].confirmedCount.toString()
        holder.deathCount.text = covidInfoList[position].deathCount.toString()

        holder.statusImageView.setImageResource(R.drawable.happy)

        holder.cardView.setOnClickListener {
            var toast = Toast.makeText(context, holder.countryName.text, Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val countryName = itemView.findViewById<TextView>(R.id.countryNameTextView)
        val confirmedCount = itemView.findViewById<TextView>(R.id.confirmedCountTextView)
        val deathCount = itemView.findViewById<TextView>(R.id.deathCountTextView)
        val cardView = itemView.findViewById<CardView>(R.id.cardView)
        val statusImageView = itemView.findViewById<ImageView>(R.id.statusImageView)
    }

}