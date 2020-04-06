package com.example.exam.utils

import android.content.Context
import android.net.ConnectivityManager
import com.example.exam.SplashScreenActivity

object Utils {

    fun isNetworkAvailable(context: SplashScreenActivity): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}