package com.example.exam

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import com.example.exam.adapters.CustomViewHolder
import com.example.exam.gson.LocationDetails
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_location_details.*
import okhttp3.*
import java.io.IOException


class LocationDetailsActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_details)


        val navBarTitle = intent.getStringExtra(CustomViewHolder.location_title_key)
        val latitude = intent.getDoubleExtra(CustomViewHolder.latitude, 0.0)
        val longitude = intent.getDoubleExtra(CustomViewHolder.longitude, 0.0)
        val locationName = intent.getStringExtra(CustomViewHolder.location_title_key)

        supportActionBar?.title = navBarTitle
        location_name.text = navBarTitle
        location_description.resetLoader()

        fetchJson()




        openMaps.setOnClickListener {
            val intent = Intent(openMaps.context, MapsActivity::class.java)

            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
            intent.putExtra("title", locationName)
            openMaps.context.startActivity(intent)

        }

    }


    private fun fetchJson() {
        val locationId = intent.getLongExtra(CustomViewHolder.location_id_key, -1)
        val locationDetailsUrl = "https://www.noforeignland.com/home/api/v1/place?id=$locationId"

        val client = OkHttpClient()

        val request = Request.Builder()
            .url(locationDetailsUrl)
            .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {

                if (response.isSuccessful) {
                    val body = response.body?.string()
                    val place = GsonBuilder().create().fromJson(body, LocationDetails::class.java).place
                    val imageUrl = place.banner
                    runOnUiThread {
                        try {
                            println(place.comments)
                            when (place.comments) {
                                "<p></p>", "<p><br></p>" -> location_description.text =
                                    "No comments!"
                                else -> location_description.text = HtmlCompat.fromHtml(
                                    place.comments,
                                    HtmlCompat.FROM_HTML_MODE_LEGACY
                                )
                            }

                            if (imageUrl.isNotEmpty()) {
                                Picasso.get()
                                    .load(imageUrl)
                                    .placeholder(R.drawable.placeholder)
                                    .fit()
                                    .into(location_image)
                            } else {
                                location_image.visibility = View.GONE
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }
                }


            }


            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute HTTP request")
                e.printStackTrace()
            }

        })
    }
}

