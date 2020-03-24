package com.example.exam

import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.exam.adapters.MainAdapter
import com.example.exam.db.LocationDAO
import com.example.exam.gson.ListFeed
import com.example.exam.gson.Location
import com.example.exam.utils.Utils
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private var locationDAO: LocationDAO? = null

    var list: MutableList<Location> = ArrayList()
    var displayList: MutableList<Location> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView_main.layoutManager = LinearLayoutManager(this)

        locationDAO = LocationDAO(this)


        val count = locationDAO!!.getLocationCount()
        if (count < 1) {
            if (Utils.isNetworkAvailable(this)) {
                fetchFeed()
            } else {
                println("no network :o")
            }
        } else {

            displayList = locationDAO!!.getLocationsLimited(0, 100)
            list.addAll(displayList)

            runOnUiThread {
                recyclerView_main.adapter = MainAdapter(displayList)
            }





            recyclerView_main.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(
                    recyclerView: RecyclerView,
                    newState: Int
                ) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(1)) {

                        runOnUiThread {
                            val loadedItemCount: Int? = recyclerView_main.adapter?.itemCount

                            val nextItemsToLoad = locationDAO!!.getLocationsLimited(loadedItemCount,100 )

                            displayList.addAll(nextItemsToLoad)
                            list.addAll(nextItemsToLoad)

                            recyclerView_main.adapter = MainAdapter(displayList)

                            if (loadedItemCount != null) {
                                (recyclerView_main.adapter as MainAdapter).notifyItemRangeChanged(loadedItemCount, 2)
                            }
                        }

                    }
                }
            })

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


        //TODO: do in background
        val gson = GsonBuilder().create()

        var counter = 0
        for (location in locations) {
            counter++

            location.type?.let {
                locationDAO?.insert(
                    it, gson.toJson(location.properties), gson.toJson(location.geometry)
                )
            }
            when(counter) {
                1000 -> println(1000)
                2000 -> println(2000)
                3000 -> println(3000)
                4000 -> println(4000)
                5000 -> println(5000)
                6000 -> println(6000)
                7000 -> println(7000)
                8000 -> println(8000)
                9000 -> println(9000)
                10000 -> println(10000)
                11000 -> println(11000)
                11700 -> println(11700)
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









