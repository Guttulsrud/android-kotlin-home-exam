package com.example.exam

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.exam.adapters.MainAdapter
import com.example.exam.db.LocationDAO
import com.example.exam.gson.Location
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private lateinit var locationDAO: LocationDAO
    lateinit var allLocations: List<Location>
    var locationsInRecyclerView: MutableList<Location> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView_main.layoutManager = LinearLayoutManager(this)
        locationDAO = LocationDAO(this)


        allLocations = locationDAO.getLocationsAll()
        locationsInRecyclerView.addAll(allLocations)
        recyclerView_main.adapter = MainAdapter(locationsInRecyclerView)

        setSearchViewOnQueryTextListener()


    }


    private fun setSearchViewOnQueryTextListener() {
        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return search_view.isIconified
            }

            override fun onQueryTextChange(searchText: String?): Boolean {
                locationsInRecyclerView.clear()

                if (searchText!!.toLowerCase(Locale.ROOT).isNotEmpty()) {

                    val locations: MutableList<Location> =
                        locationDAO.getLocationByName(searchText.toLowerCase(Locale.ROOT))
                    locationsInRecyclerView.addAll(locations)
                    recyclerView_main.adapter?.notifyDataSetChanged()


                } else {
                    locationsInRecyclerView.clear()
                    locationsInRecyclerView.addAll(allLocations)
                    recyclerView_main.adapter?.notifyDataSetChanged()
                }
                return true
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









