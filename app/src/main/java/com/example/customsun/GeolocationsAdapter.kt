package com.example.customsun

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.customsun.calc.GeoLocation

class GeolocationsAdapter(context:Context, layout:Int, locations: MutableList<GeoLocation>)
    : ArrayAdapter<GeoLocation>(context, layout, locations){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        view.findViewById<TextView>(android.R.id.text1).text = getItem(position)?.locationName
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        view.findViewById<TextView>(android.R.id.text1).text = getItem(position)?.locationName
        return view
    }
}