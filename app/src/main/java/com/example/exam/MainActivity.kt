package com.example.exam

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exam.adapters.MainAdapter
import com.example.exam.gson.ListFeed
import com.example.exam.utils.Utils
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException


class MainActivity : AppCompatActivity() {


    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView_main.layoutManager = LinearLayoutManager(this)

        if (Utils.isNetworkAvailable(this)) {
            fetchFeed()
        } else {
            Toast.makeText(this, getString(R.string.no_connection_message), Toast.LENGTH_LONG)
                .show()
        }

    }

    private fun fetchFeed() {

        val url = "https://www.noforeignland.com/home/api/v1/places/"
        println("Fetching JSON")
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()



        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()
                val gson = GsonBuilder().create()
                val listFeed = gson.fromJson(body, ListFeed::class.java)

                runOnUiThread {
                    recyclerView_main.adapter = MainAdapter(listFeed)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute HTTP request")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }

        return true
    }


}








