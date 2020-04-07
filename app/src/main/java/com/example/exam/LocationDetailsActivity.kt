package com.example.exam

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.example.exam.adapters.CustomViewHolder
import com.example.exam.api.ApiServiceInterface
import com.example.exam.db.LocationDAO
import com.example.exam.gson.LocationDetails
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_location_details.*
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LocationDetailsActivity : AppCompatActivity() {
    private lateinit var locationDAO: LocationDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_details)
        locationDAO = LocationDAO(this)

        val id = intent.getLongExtra(CustomViewHolder.location_id_key, -1)
        fetchAndParseApiResponse(id)
        location_name.text = intent.getStringExtra(CustomViewHolder.location_title_key)
        location_description.resetLoader()


        setMapsButtonListener()
    }

    private fun setMapsButtonListener() {
        openMaps.setOnClickListener {
            val latitude = intent.getDoubleExtra(CustomViewHolder.latitude, 0.0)
            val longitude = intent.getDoubleExtra(CustomViewHolder.longitude, 0.0)
            val locationName = intent.getStringExtra(CustomViewHolder.location_title_key)
            val intent = Intent(openMaps.context, MapsActivity::class.java)

            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
            intent.putExtra("title", locationName)
            openMaps.context.startActivity(intent)
        }
    }


    private fun fetchAndParseApiResponse(locationId: Long) {
        val builder = OkHttpClient.Builder()
        val client = builder.build()

        val api = Retrofit.Builder()
            .baseUrl("https://www.noforeignland.com/home/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiServiceInterface::class.java)

        val call = api.getLocationDetails(locationId)
        call.enqueue(object : retrofit2.Callback<LocationDetails> {

            override fun onResponse(
                call: retrofit2.Call<LocationDetails>,
                response: retrofit2.Response<LocationDetails>
            ) {
                if (response.isSuccessful) {

                    val locationDetails: LocationDetails? = response.body()
                    locationDetails?.let {

                        val comments = HtmlCompat.fromHtml(
                            it.place.comments,
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        )

                        if (!comments.isBlank()) location_description.text =
                            comments else location_description.text = "No comments!"

                        if (it.place.banner.isNotEmpty()) {
                            Picasso.get()
                                .load(it.place.banner)
                                .placeholder(R.drawable.placeholder)
                                .fit()
                                .into(location_image)
                        } else {
                            location_image.visibility = View.GONE
                        }

                    }

                }
            }

            override fun onFailure(call: retrofit2.Call<LocationDetails>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    //Setting app to fullscreen
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }
}

