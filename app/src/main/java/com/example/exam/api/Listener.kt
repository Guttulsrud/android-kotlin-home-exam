package com.example.exam.api

import com.example.exam.gson.Location

interface Listener {
    fun onNewsSuccess(newsList: ArrayList<Location>)
    fun onNewsError()
    fun showProgress(show: Boolean)
}