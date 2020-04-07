package com.example.exam

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exam.adapters.MainAdapter
import com.example.exam.db.LocationDAO
import com.example.exam.gson.Location
import kotlinx.android.synthetic.main.activity_main.*
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


        displayLocationsInRecyclerView()
        setSearchViewOnQueryTextListener()
        createAndDisplaySpinner()


    }

    private fun createAndDisplaySpinner() {

        val sortTypes = resources.getStringArray(R.array.mode)

        spinner.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                sortTypes
            )
        (spinner.adapter as ArrayAdapter<*>).setDropDownViewResource(R.layout.spinner)


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {


            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                when (sortTypes[position]) {
                    sortTypes[0] -> getAndDisplayLocationsAll()
                    sortTypes[1] -> getAndDisplayLocationsAll("icon")
                    sortTypes[2] -> getAndDisplayLocationsAll("name", "asc")
                    sortTypes[3] -> getAndDisplayLocationsAll("name", "desc")
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                (spinner.adapter as ArrayAdapter<*>).setDropDownViewResource(R.layout.spinner)
            }
        }
    }

    private fun displayLocationsInRecyclerView() {
        locationsInRecyclerView.clear()
        locationsInRecyclerView.addAll(allLocations)
        recyclerView_main.adapter = MainAdapter(locationsInRecyclerView)
        recyclerView_main.adapter?.notifyDataSetChanged()
    }


    private fun getAndDisplayLocationsAll(query: String? = null, ascDesc: String? = null) {
        allLocations = if (!ascDesc.isNullOrEmpty()) {
            locationDAO.getLocationsAllSorted("$query $ascDesc")
        } else {
            locationDAO.getLocationsAllSorted(query)
        }
        displayLocationsInRecyclerView()
    }


    private fun setSearchViewOnQueryTextListener() {
        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return search_view.isIconified
            }

            override fun onQueryTextChange(searchText: String?): Boolean {
                locationsInRecyclerView.clear()

                if (searchText!!.toLowerCase(Locale.ROOT).isNotEmpty()) {

                    val locations: List<Location> =
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
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


}









