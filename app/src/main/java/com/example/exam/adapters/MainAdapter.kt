package com.example.exam.adapters

import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.exam.LocationDetailsActivity
import com.example.exam.MapsActivity
import com.example.exam.R
import com.example.exam.gson.ListFeed
import com.example.exam.gson.Location
import kotlinx.android.synthetic.main.location_row.view.*
import java.util.*


class MainAdapter(private val listFeed: ListFeed) : RecyclerView.Adapter<CustomViewHolder>() {

    override fun getItemCount(): Int {
        return listFeed.features.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.location_row, parent, false)
        return CustomViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val location = listFeed.features[position]
        holder.view.textView_location_title?.text = location.properties?.name

        holder.view.mapsButton.setOnClickListener {
            val intent = Intent(holder.view.mapsButton.context, MapsActivity::class.java)


            intent.putExtra("longitude", location.geometry?.coordinates?.get(0))
            intent.putExtra("latitude", location.geometry?.coordinates?.get(1))
            holder.view.mapsButton.context.startActivity(intent)
        }


        val omg = location.properties?.icon?.toLowerCase(Locale.ROOT)
        val context: Context = holder.view.context
        val iconDrawableId = context.resources.getIdentifier(omg, "drawable", context.packageName)

        val iconDrawable = ContextCompat.getDrawable(context, iconDrawableId);
        holder.view.icon.setImageDrawable(iconDrawable);

        holder.location = location


    }


}


class CustomViewHolder(val view: View, var location: Location? = null) :
    RecyclerView.ViewHolder(view) {

    companion object {
        const val location_title_key = "title"
        const val location_id_key = "id"
        const val latitude = "latitude"
        const val longitude = "longitude"
    }

    init {
        view.setOnClickListener {
            val intent = Intent(view.context, LocationDetailsActivity::class.java)
            intent.putExtra(location_title_key, location?.properties?.name)
            intent.putExtra(location_id_key, location?.properties?.id)

            intent.putExtra(longitude, location?.geometry?.coordinates?.get(0))
            intent.putExtra(latitude, location?.geometry?.coordinates?.get(1))
            view.context.startActivity(intent)
        }
    }
}