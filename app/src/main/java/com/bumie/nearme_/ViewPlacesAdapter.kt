package com.bumie.nearme_

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class ViewPlacesAdapter(private val context:Context, val places:ArrayList<Datum>):
    RecyclerView.Adapter<ViewPlacesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPlacesViewHolder {
        val layout = LayoutInflater.from(context).inflate(R.layout.view_places, null, false)
        return ViewPlacesViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewPlacesViewHolder, position: Int) {
        val place = places[position]
        holder.txt1.text = place.name
        holder.txt2.text = place.category
    }

    override fun getItemCount(): Int {
       return places.size
    }


}


class ViewPlacesViewHolder(layout:View): RecyclerView.ViewHolder(layout) {

    val txt1: TextView = layout.findViewById(R.id.textView)
    val txt2: TextView = layout.findViewById(R.id.textView2)

}