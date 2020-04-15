package com.example.exam

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exam.adapters.MainAdapter
import com.example.exam.db.LocationDAO
import com.example.exam.Models.Location
import com.example.exam.Models.Locations
import com.example.exam.api.ApiServiceInterface
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private lateinit var locationDAO: LocationDAO
    lateinit var allLocations: MutableList<Location>
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


        //if swipe do this
        getAndCacheApiResponse()


    }

    //Function for searching DB with search view
    private fun setSearchViewOnQueryTextListener() {
        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return search_view.isIconified
            }

            override fun onQueryTextChange(searchText: String?): Boolean {
                locationsInRecyclerView.clear()

                if (searchText!!.toLowerCase(Locale.ROOT).isNotEmpty()) {
                    val locations: MutableList<Location> =
                        locationDAO.getLocationsByName(searchText.toLowerCase(Locale.ROOT))
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

    //Function for creating and display spinner
    private fun createAndDisplaySpinner() {
        val sortTypes = resources.getStringArray(R.array.mode)
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortTypes)
        (spinner.adapter as ArrayAdapter<*>).setDropDownViewResource(R.layout.spinner);

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
                    sortTypes[4] -> getAndDisplayHistoryLocations()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                (spinner.adapter as ArrayAdapter<*>).setDropDownViewResource(R.layout.spinner)
            }
        }
    }

    //Function for displaying locations in recyclerview
    private fun displayLocationsInRecyclerView() {
        locationsInRecyclerView.clear()
        locationsInRecyclerView.addAll(allLocations)
        recyclerView_main.adapter = MainAdapter(locationsInRecyclerView)
        recyclerView_main.adapter?.notifyDataSetChanged()
    }

    //Function for getting locations from DB, with optional query
    private fun getAndDisplayLocationsAll(query: String? = null, ascDesc: String? = null) {
        allLocations = if (!ascDesc.isNullOrEmpty()) {
            locationDAO.getLocationsAll("$query $ascDesc")
        } else {
            locationDAO.getLocationsAll(query)
        }
        displayLocationsInRecyclerView()
    }

    //Function for getting locations from DB, with optional query
    private fun getAndDisplayHistoryLocations() {
        val detailsList = locationDAO.getDetailsAll()
        val locationsToAdd: MutableList<Location> = ArrayList()

        for (details in detailsList) {
            locationDAO.getLocationById(details.id.toString())?.let { locationsToAdd.add(it) }
        }

        allLocations.clear()
        allLocations = locationsToAdd

        displayLocationsInRecyclerView()
    }

    //Setting app to fullscreen
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    //Fetching response from API, putting in DB
    private fun getAndCacheApiResponse() {
        val api = Retrofit.Builder()
            .baseUrl("https://www.noforeignland.com/home/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
            .create(ApiServiceInterface::class.java)

        val locationsToAdd: MutableList<Location> = ArrayList()
        api.getLocationsAll().enqueue(object : Callback<Locations> {
            override fun onResponse(call: Call<Locations>, response: Response<Locations>) {
                if (response.isSuccessful) {
                    val locations: Locations? = response.body()
                    locations!!.features.forEach {
                        locationsToAdd.add(
                            Location(
                                it.properties.id,
                                it.properties.name,
                                it.properties.icon,
                                it.geometry.coordinates[0],
                                it.geometry.coordinates[1]
                            )
                        )


                    }

                    CoroutineScope(IO).launch {
                        locationDAO.insertLocationsAll(locationsToAdd)
                    }
                }
            }

            override fun onFailure(call: Call<Locations>, t: Throwable) {
                Toast.makeText(
                    this@MainActivity, "Failed to make service request. Please try again!",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }


}









