package com.example.exam.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.exam.LocationDetailsActivity
import com.example.exam.MapsActivity
import com.example.exam.R
import com.example.exam.Models.Location
import kotlinx.android.synthetic.main.location_row.view.*
import java.util.*


class MainAdapter(private val listFeed: MutableList<Location>) :
    RecyclerView.Adapter<CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cell = layoutInflater.inflate(R.layout.location_row, parent, false)
        return CustomViewHolder(cell)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val context: Context = holder.view.context
        val location = listFeed[position]
        holder.view.textView_location_title?.text = location.name

        // OnClickListener for activating map activity with location
        holder.view.mapsButton.setOnClickListener {
            val intent = Intent(holder.view.mapsButton.context, MapsActivity::class.java)
            intent.putExtra("longitude", location.longitude)
            intent.putExtra("latitude", location.latitude)
            intent.putExtra("title", location.name)
            holder.view.mapsButton.context.startActivity(intent)
        }

        holder.view.icon.setImageDrawable(
            ContextCompat.getDrawable(
                context, context.resources.getIdentifier(
                    location.icon?.toLowerCase(Locale.ROOT),
                    "drawable",
                    context.packageName
                )
            )
        )
        holder.location = location
    }

    override fun getItemCount(): Int {
        return listFeed.count()
    }
}

class CustomViewHolder(val view: View, var location: Location? = null) :
    RecyclerView.ViewHolder(view) {

    companion object {
        const val location_title_key = "title"
        const val location_id_key = "_id"
        const val latitude = "latitude"
        const val longitude = "longitude"
    }

    init {
        view.setOnClickListener {
            val intent = Intent(view.context, LocationDetailsActivity::class.java)
            intent.putExtra(location_title_key, location?.name)
            intent.putExtra(location_id_key, location?.id)
            intent.putExtra(longitude, location?.longitude)
            intent.putExtra(latitude, location?.latitude)
            view.context.startActivity(intent)
        }
    }
}