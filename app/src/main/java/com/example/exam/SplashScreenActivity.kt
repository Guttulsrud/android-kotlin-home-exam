package com.example.exam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.exam.api.ApiServiceInterface
import com.example.exam.db.LocationDAO
import com.example.exam.gson.Location
import com.example.exam.gson.Locations
import com.example.exam.utils.Utils
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var locationDAO: LocationDAO


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        locationDAO = LocationDAO(this)

        if (Utils.isNetworkAvailable(this)) {
            if (locationDAO.checkIfDataHasBeenCached()) {
                startMainActivity()

            } else {
                fetchAndParseApiResponse()

            }
        } else {
            Toast.makeText(
                this, "No network available, please restart the app.",
                Toast.LENGTH_LONG
            ).show();
        }


    }


    private fun fetchAndParseApiResponse() {

        val builder = OkHttpClient.Builder()
        val client = builder.build()

        val api = Retrofit.Builder()
            .baseUrl("https://www.noforeignland.com/home/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiServiceInterface::class.java)


        val locationsToAdd: MutableList<Location> = ArrayList()
        val call = api.getLocationsAll()
        call.enqueue(object : Callback<Locations> {


            override fun onResponse(call: Call<Locations>, response: Response<Locations>) {
                if (response.isSuccessful) {
                    val locations: Locations? = response.body()
                    locations!!.features.forEach {

                        locationsToAdd.add(
                            Location(
                                it.properties?.id,
                                it.properties?.name,
                                it.properties?.icon,
                                it.properties?.id,
                                it.geometry?.coordinates?.get(0),
                                it.geometry?.coordinates?.get(1)
                            )
                        )
                    }

                    runBlocking {
                        locationDAO.insertLocationsAll(locationsToAdd)
                    }
                    startMainActivity()
                }
            }

            override fun onFailure(call: Call<Locations>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun startMainActivity() {
        val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    //Setting app to fullscreen
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }


}
