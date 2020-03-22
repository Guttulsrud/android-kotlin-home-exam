package com.example.exam

import android.os.Bundle
import android.os.Handler
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
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


//        recyclerView_main.layoutManager = LinearLayoutManager(this)
//
//        recyclerView_main.adapter = LocationDetailsAdapter()

        val navBarTitle = intent.getStringExtra(CustomViewHolder.location_title_key)
        supportActionBar?.title = navBarTitle
        location_name.text = navBarTitle
        location_description.movementMethod = ScrollingMovementMethod()
        location_description.resetLoader()


        fetchJson()

    }


    private fun fetchJson() {
        val locationId = intent.getLongExtra(CustomViewHolder.location_id_key, -1)
        val locationDetailsUrl = "https://www.noforeignland.com/home/api/v1/place?id=$locationId"

        val client = OkHttpClient()

        val request = Request.Builder().url(locationDetailsUrl).build()


        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {

                //TODO: Fix so loading waits until ready before rendering complete text

                if (response.isSuccessful) {
                    val body = response.body?.string()
                    val gson = GsonBuilder().create()
                    val place = gson.fromJson(body, LocationDetails::class.java).place


                    runOnUiThread {
                        try {
                            if (place.comments != "<p></p>") {
                                location_description.text = HtmlCompat.fromHtml(place.comments, HtmlCompat.FROM_HTML_MODE_LEGACY)
                            } else {
                                location_description.text = "No comments!"
                            }

                            if (place.banner.isNotEmpty()) {
                                Picasso.get().load(place.banner)
                                    .placeholder(R.drawable.placeholder)
                                    .into(location_image)
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

//
//    private class LocationDetailsAdapter : RecyclerView.Adapter<DetailsViewHolder>() {
//
//        override fun onCreateViewHolder(
//            parent: ViewGroup, viewType: Int
//        ): DetailsViewHolder {
//
//
//            val layoutInflater = LayoutInflater.from(parent.context)
//            val customView = layoutInflater.inflate(R.layout.details_row, parent, false)
//            return DetailsViewHolder(customView)
//        }
//
//        override fun getItemCount(): Int {
//            return 5
//        }
//
//        override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {
//
//
//        }
//
//
//    }
//
//
//    private class DetailsViewHolder(val customView: View) :
//        RecyclerView.ViewHolder(customView) {
//
//    }
}

