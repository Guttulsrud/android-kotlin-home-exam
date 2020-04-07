package com.example.exam.api

import com.example.exam.gson.LocationDetails
import com.example.exam.gson.Locations
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServiceInterface {

    @GET("v1/places")
    fun getLocationsAll(): Call<Locations>

    @GET("v1/place")
    fun getLocationDetails(@Query("id") id: Long?): Call<LocationDetails>

}