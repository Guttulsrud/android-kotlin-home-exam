package com.example.exam

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exam.adapters.MainAdapter
import com.example.exam.db.LocationDAO
import com.example.exam.gson.ListFeed
import com.example.exam.gson.Location
import com.example.exam.utils.Utils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private var locationDAO: LocationDAO? = null


    var list: MutableList<Location> = ArrayList()
    var displayList: MutableList<Location> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView_main.layoutManager = LinearLayoutManager(this)

        locationDAO = LocationDAO(this)


        val count = locationDAO!!.getLocationCount()
        if (count < 1) {
            if (Utils.isNetworkAvailable(this)) {
                fetchFeed()
            } else {

            }
        } else {

            displayList = locationDAO!!.getpaged(0, 3)

            list.addAll(displayList)

            runOnUiThread {
                recyclerView_main.adapter = MainAdapter(displayList)
            }
        }


    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val searchItem = menu?.findItem(R.id.menu_search)
        if (searchItem != null) {
            val searchView = searchItem.actionView as SearchView

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText!!.isNotEmpty()) {
                        displayList.clear()
                        val search = newText.toLowerCase(Locale.ROOT)

                        list.forEach {
                            if (it.properties?.name?.toLowerCase(Locale.ROOT)?.contains(search)!!) {
                                displayList.add(it)
                            }
                        }
                        recyclerView_main.adapter?.notifyDataSetChanged()
                    } else {
                        displayList.clear()
                        displayList.addAll(list)
                        recyclerView_main.adapter?.notifyDataSetChanged()

                    }

                    return true
                }

            })
        }
        return true
    }


    private fun parseJsonToFeed(json: String?): MutableList<Location> {
        return GsonBuilder().create().fromJson(json, ListFeed::class.java).features
    }

    private fun parseAndCacheLocations(locations: MutableList<Location>) {

        val gson = GsonBuilder().create()


        var counter: Int = 0
        for (location in locations) {
            location.type?.let {
                locationDAO?.insert(
                    it, gson.toJson(location.properties), gson.toJson(location.geometry)
                )
            }
            counter++

            when (counter) {
                1000 -> println("1000 added")
                2000 -> println("2000 added")
                3000 -> println("3000 added")
                4000 -> println("4000 added")
                5000 -> println("5000 added")
                6000 -> println("6000 added")
                7000 -> println("7000 added")
                8000 -> println("8000 added")
                9000 -> println("9000 added")
                10000 -> println("10000 added")
            }
        }
        println("caching done!")
    }


    private fun fetchFeed() {

        val url = "https://www.noforeignland.com/home/api/v1/places/"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()


        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {

                if (response.isSuccessful) {
                    displayList = parseJsonToFeed(response.body?.string())
                    list.addAll(displayList)


                    runOnUiThread {
                        recyclerView_main.adapter = MainAdapter(displayList)
                    }

                    parseAndCacheLocations(list)

                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute HTTP request")
            }
        })

    }

}









