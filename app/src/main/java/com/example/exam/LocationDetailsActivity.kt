package com.example.exam

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.example.exam.adapters.CustomViewHolder
import com.example.exam.api.ApiServiceInterface
import com.example.exam.db.LocationDAO
import com.example.exam.Models.Details
import com.example.exam.Models.LocationDetails
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_location_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LocationDetailsActivity : AppCompatActivity() {
    private lateinit var locationDAO: LocationDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_details)
        locationDAO = LocationDAO(this)

        val id = intent.getLongExtra(CustomViewHolder.location_id_key, -1)
        location_name.text = intent.getStringExtra(CustomViewHolder.location_title_key)
        location_description.resetLoader()
        setMapsButtonListener()

        if (locationDAO.checkIfDetailsIdExists(id.toString())) {

            val details: Details? = locationDAO.getDetailsOne(id.toString())
            val comments = details?.comments?.let {
                HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
            }

            if (details?.banner?.isNotEmpty()!!) {
                Picasso.get()
                    .load(details.banner)
                    .placeholder(R.drawable.placeholder)
                    .fit()
                    .into(location_image)

            } else {
                location_image.visibility = View.GONE
            }
            if (!comments?.isBlank()!!) location_description.text =
                comments else location_description.text = R.string.no_comments.toString()
        } else {
            fetchAndParseApiResponse(id)
        }
    }

    private fun setMapsButtonListener() {
        openMaps.setOnClickListener {
            val title = intent.getStringExtra(CustomViewHolder.location_title_key)
            val latitude = intent.getDoubleExtra(CustomViewHolder.latitude, 0.0)
            val longitude = intent.getDoubleExtra(CustomViewHolder.longitude, 0.0)

            val intent = Intent(openMaps.context, MapsActivity::class.java)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
            intent.putExtra("title", title)
            openMaps.context.startActivity(intent)
        }
    }


    private fun fetchAndParseApiResponse(locationId: Long) {
        val api = Retrofit.Builder()
            .baseUrl("https://www.noforeignland.com/home/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
            .create(ApiServiceInterface::class.java)

        api.getLocationDetails(locationId).enqueue(object : Callback<LocationDetails> {
            override fun onResponse(
                call: Call<LocationDetails>,
                response: Response<LocationDetails>
            ) {
                if (response.isSuccessful) {

                    val locationDetails: LocationDetails? = response.body()
                    locationDetails?.let {

                        val comments = HtmlCompat.fromHtml(
                            it.place.comments,
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        )

                        if (!comments.isBlank()) location_description.text =
                            comments else location_description.text =
                            R.string.no_comments.toString()

                        if (it.place.banner.isNotEmpty()) {
                            Picasso.get()
                                .load(it.place.banner)
                                .placeholder(R.drawable.placeholder)
                                .fit()
                                .into(location_image)
                        } else {
                            location_image.visibility = View.GONE
                        }

                        CoroutineScope(IO).launch {
                            locationDAO.insertDetailsOne(
                                Details(
                                    intent.getLongExtra(CustomViewHolder.location_id_key, -1),
                                    intent.getStringExtra(CustomViewHolder.location_title_key),
                                    it.place.comments,
                                    it.place.banner
                                )
                            )
                        }
                    }
                }
            }

            override fun onFailure(call: Call<LocationDetails>, t: Throwable) {
                Toast.makeText(
                    this@LocationDetailsActivity,
                    "Failed to make service request. Please try again!",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

}

