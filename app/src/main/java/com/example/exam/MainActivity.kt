package com.example.exam

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exam.adapters.MainAdapter
import com.example.exam.db.LocationDAO
import com.example.exam.gson.ListFeed
import com.example.exam.utils.Utils
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException
import java.util.Locale.filter


class MainActivity : AppCompatActivity() {


    private var locationDAO: LocationDAO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView_main.layoutManager = LinearLayoutManager(this)

        locationDAO = LocationDAO(this)


        if (locationDAO!!.getLocationCount() < 1) {
            if (Utils.isNetworkAvailable(this)) {
                fetchFeed()
            } else {
                Toast.makeText(this, getString(R.string.no_connection_message), Toast.LENGTH_LONG)
                    .show()
            }
        } else {

            val locations = locationDAO!!.fetchAll()

            runOnUiThread {
                recyclerView_main.adapter = MainAdapter(ListFeed(locations))
            }
        }
    }



    private fun fetchFeed() {

        val url = "https://www.noforeignland.com/home/api/v1/places/"
        println("Fetching JSON")
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()


        var test: ListFeed
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()
                val gson = GsonBuilder().create()
                val listFeed = gson.fromJson(body, ListFeed::class.java)

                test = listFeed


                //TODO: Do in background
                for (item in listFeed.features) {
                    item.type?.let {
                        locationDAO?.insert(
                            it,
                            gson.toJson(item.properties),
                            gson.toJson(item.geometry)
                        )
                    }
                }

                runOnUiThread {
                    recyclerView_main.adapter = MainAdapter(listFeed)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute HTTP request")
            }
        })


    }


}









