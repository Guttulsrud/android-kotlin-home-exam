package com.example.exam

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.exam.adapters.MainAdapter
import com.example.exam.db.LocationDAO
import com.example.exam.gson.Location
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    var locationDAO: LocationDAO? = null


    var allLocations: MutableList<Location> = ArrayList()
    var locationsInRecyclerView: MutableList<Location> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView_main.layoutManager = LinearLayoutManager(this)

        locationDAO = LocationDAO(this)


        locationsInRecyclerView = locationDAO!!.getLocationsAll()
        recyclerView_main.adapter = MainAdapter(locationsInRecyclerView)


    }




    override fun onWindowFocusChanged(hasFocus: Boolean) {
        val locationSearchView = search_view
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            locationSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(searchText: String?): Boolean {
                    println("reeherehrhreerh")

                    if (searchText!!.isNotEmpty()) {
                        locationsInRecyclerView.clear()
                        val keyword = searchText.toLowerCase(Locale.ROOT)


                        if (keyword.isNotEmpty()) {
                            val locations:MutableList<Location> = locationDAO!!.getLocationByName(keyword)
                            locationsInRecyclerView.addAll(locations)
                            recyclerView_main.adapter?.notifyDataSetChanged()
                        }

                        println("hello am not empty")

                    } else {
                        println("hello empty")

                        locationsInRecyclerView.clear()
                        locationsInRecyclerView.addAll(allLocations)
                        recyclerView_main.adapter?.notifyDataSetChanged()

                    }
                    return true
                }
            })
        }
    }
}









