package com.example.exam.api

import com.example.exam.gson.Locations
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServiceInterface {


    @GET("v1/places")
    fun getAllLocationsFromNfl(): Call<Locations>


}