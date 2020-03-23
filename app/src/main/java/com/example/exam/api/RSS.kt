package com.example.exam.api

import android.os.AsyncTask
import com.example.exam.gson.Location
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request

class RssTask(var listener: Listener?) : AsyncTask<String, Int, ArrayList<Location>>() {


    override fun doInBackground(vararg params: String): ArrayList<Location> {

        lateinit var listfeed: ArrayList<Location>

        publishProgress(0)

        try {
            var res = callWebRequest(params[0])

            listfeed = parseList(res)

            publishProgress(100)

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return listfeed
    }

    override fun onPostExecute(result: ArrayList<Location>) {

        super.onPostExecute(result)

        if (result.isEmpty()) {
            listener?.onNewsError()
        } else {
            listener?.onNewsSuccess(result)
        }
    }


    private fun parseList(jsonStr: String): ArrayList<Location> {

        val type = object : TypeToken<ArrayList<Location>>() {}.type


        return Gson().fromJson(jsonStr, type)
    }


    private fun callWebRequest(url: String): String {
        val client = OkHttpClient()

        val request: Request = Request.Builder()
            .url(url)
            .build()


        return client.newCall(request).execute().body!!.string()
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)

        if (values[0] == 0) {
            listener?.showProgress(true)
        } else {
            listener?.showProgress(false)
        }


    }


}