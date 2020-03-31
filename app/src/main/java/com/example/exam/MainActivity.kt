package com.example.exam

import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.exam.adapters.MainAdapter
import com.example.exam.db.LocationDAO
import com.example.exam.gson.ListFeed
import com.example.exam.gson.Location
import com.example.exam.utils.Utils
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private var locationDAO: LocationDAO? = null


    var list: MutableList<Location> = ArrayList()
    var displayList: MutableList<Location> = ArrayList()
    private var allLocations: MutableList<Location> = ArrayList()

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


            //Fetch all locations from DB on a sepereate IO coroutine
            CoroutineScope(IO).launch {
                allLocations = locationDAO!!.fetchAll()

            }


            CoroutineScope(Main).launch {
                displayList = locationDAO!!.getLocationsLimited(0, 10)
                list.addAll(displayList)
                recyclerView_main.adapter = MainAdapter(displayList)
            }


            setRecyclerScrollListener()



        }

    }


    private fun setRecyclerScrollListener() {
        recyclerView_main.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(
                recyclerView: RecyclerView,
                newState: Int
            ) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {

                    runOnUiThread {
                        val loadedItemCount: Int? = recyclerView_main.adapter?.itemCount


                        //TODO: Scroll down on insert
                        val nextItemsToLoad =
                            locationDAO!!.getLocationsLimited(loadedItemCount, 10)

                        displayList.addAll(nextItemsToLoad)
                        list.addAll(nextItemsToLoad)

                        recyclerView_main.adapter = MainAdapter(displayList)



                        if (loadedItemCount != null) {
                            recyclerView_main.adapter?.itemCount?.let {
                                (recyclerView_main.adapter as MainAdapter).notifyItemRangeChanged(
                                    0,
                                    it
                                )
                            }
                        }
                    }

                }
            }
        })
    }


    //TODO: Search must fetch from db
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

                        //val allLocations = locationDAO!!.fetchAll()

                        allLocations.forEach {
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
                        locationDAO!!.insertData(list)
                    }


                    runOnUiThread {
                        recyclerView_main.adapter = MainAdapter(displayList)
                    }


                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute HTTP request")
            }
        })

    }


//    private suspend fun setTextOnMainThread(input: String) {
//        Thread.sleep(7000)
//        withContext(Main) {
//            button.text = input
//        }
//    }


//    private suspend fun getApiJson() {
//
//        val test = CoroutineScope(IO).launch {
//
//            setTextOnMainThread("hello i am response from API!!!")
//
//        }
//
//        test.invokeOnCompletion {
//
//        }
//    }

//    private fun fetchJson():String {
//
//
//        CoroutineScope(IO).launch {
//            val result1:Deferred<String> = async {
//
//            }
//            val result2:Deferred<String> = async {
//
//            }
//        }
//    }

//    private suspend fun getResultFromApi():String {
//        val url = "https://www.noforeignland.com/home/api/v1/places/"
//        val request = Request.Builder().url(url).build()
//        val client = OkHttpClient()
//
//
//
//
//    }


}









