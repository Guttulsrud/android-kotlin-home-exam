package com.example.exam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.widget.Toast
import com.example.exam.api.ApiServiceInterface
import com.example.exam.db.LocationDAO
import com.example.exam.gson.Location
import com.example.exam.gson.Locations
import com.example.exam.utils.Utils
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable

class SplashScreenActivity : AppCompatActivity() {

    private var locationDAO: LocationDAO? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        locationDAO = LocationDAO(this)
        val count = locationDAO!!.getLocationCount()



        println("HELLLLLOOO")
        print("HELLLLLOOO")
        println("HELLLLLOOO")

        if (Utils.isNetworkAvailable(this)) {
            if (count < 1) {
                fetchAndParseApiResponse()
            } else {
                startMainActivity()
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


        val adapterData: MutableList<Location> = ArrayList()
        val call = api.getAllLocationsFromNfl()
        call.enqueue(object : Callback<Locations> {



            override fun onResponse(call: Call<Locations>, response: Response<Locations>) {
                if (response.isSuccessful) {
                    val locations: Locations? = response.body()
                    locations!!.features.forEach {

                        val name: String? = it.properties?.name
                        val apiId: Long? = it.properties?.id
                        val icon: String? = it.properties?.icon
                        val testId: Long? = it.properties?.id
                        val longitude: Double? = it.geometry?.coordinates?.get(0)
                        val latitude: Double? = it.geometry?.coordinates?.get(1)


                        val location = Location(testId, name, icon, apiId, longitude, latitude)
                        adapterData.add(location)
                    }

                    runBlocking {
                        locationDAO!!.insertData(adapterData)
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
}
