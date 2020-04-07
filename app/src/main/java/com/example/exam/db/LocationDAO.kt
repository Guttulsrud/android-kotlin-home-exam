package com.example.exam.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.provider.BaseColumns
import com.example.exam.db.LocationTable.COLUMN_ICON
import com.example.exam.db.LocationTable.COLUMN_ID
import com.example.exam.db.LocationTable.COLUMN_LATITUDE
import com.example.exam.db.LocationTable.COLUMN_LONGITUDE
import com.example.exam.db.LocationTable.COLUMN_NAME
import com.example.exam.db.LocationTable.TABLE_NAME
import com.example.exam.gson.Location


object LocationTable : BaseColumns {
    const val TABLE_NAME = "location_table"
    const val COLUMN_ID = "_id"
    const val COLUMN_NAME = "name"
    const val COLUMN_ICON = "icon"
    const val COLUMN_LONGITUDE = "longitude"
    const val COLUMN_LATITUDE = "latitude"
}

var run: Boolean = false

class LocationDAO(context: Context) : Database(context) {


    fun insertLocationsAll(locations: MutableList<Location>) {
        val db = writableDatabase

        if (!run) {
            try {
                db.beginTransaction()
                for (location in locations) {
                    val contentValues = ContentValues()
                    contentValues.put(COLUMN_ID, location.id)
                    contentValues.put(COLUMN_NAME, location.name)
                    contentValues.put(COLUMN_ICON, location.icon)
                    contentValues.put(COLUMN_LONGITUDE, location.longitude)
                    contentValues.put(COLUMN_LATITUDE, location.latitude)
                    db.insert(TABLE_NAME, null, contentValues)
                }

                db.setTransactionSuccessful()
            } catch (e: Exception) {
                println(e.message)
                //todo: proper exception catch
            } finally {

                db.endTransaction()
                run = true
            }
        }
    }


//    fun update(location: LocationDTO): Int? {
//        val values = ContentValues().apply {
//            put(COLUMN_TYPE, location.type)
//            put(COLUMN_PROPERTIES, location.type)
//            put(COLUMN_GEOMETRY, location.type)
//        }
//
//        val selection = "$COLUMN_ID = ?"
//        val selectionArgs = arrayOf(location.id.toString())
//
//        return writableDatabase.use {
//            it.update(TABLE_NAME, values, selection, selectionArgs)
//        }
//    }

    fun getLocationsAll(): List<Location> {

        val cursor: Cursor = readableDatabase.query(TABLE_NAME, null, null, null, null, null, "")
        val locationList = mutableListOf<Location>()
        with(cursor) {
            while (moveToNext()) {
                locationList.add(
                    Location(
                        getLong(getColumnIndexOrThrow(COLUMN_ID)),
                        getString(getColumnIndexOrThrow(COLUMN_NAME)),
                        getString(getColumnIndexOrThrow(COLUMN_ICON)),
                        getDouble(getColumnIndexOrThrow(COLUMN_LONGITUDE)),
                        getDouble(getColumnIndexOrThrow(COLUMN_LATITUDE))
                    )
                )
            }
        }
        return locationList
    }

    fun getLocationsAllSorted(query: String?): List<Location> {

        val cursor: Cursor =
            readableDatabase.query(TABLE_NAME, null, null, null, null, null, query)
        val locationList = mutableListOf<Location>()
        with(cursor) {
            while (moveToNext()) {
                locationList.add(
                    Location(
                        getLong(getColumnIndexOrThrow(COLUMN_ID)),
                        getString(getColumnIndexOrThrow(COLUMN_NAME)),
                        getString(getColumnIndexOrThrow(COLUMN_ICON)),
                        getDouble(getColumnIndexOrThrow(COLUMN_LONGITUDE)),
                        getDouble(getColumnIndexOrThrow(COLUMN_LATITUDE))
                    )
                )
            }
        }
        return locationList
    }


    fun getLocationByName(query: String): List<Location> {
        val locationList = mutableListOf<Location>()

        val cursor: Cursor = readableDatabase.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_ICON, COLUMN_LATITUDE, COLUMN_LONGITUDE),
            "name LIKE '%$query%'",
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                locationList.add(
                    Location(
                        getLong(getColumnIndexOrThrow(COLUMN_ID)),
                        getString(getColumnIndexOrThrow(COLUMN_NAME)),
                        getString(getColumnIndexOrThrow(COLUMN_ICON)),
                        getDouble(getColumnIndexOrThrow(COLUMN_LONGITUDE)),
                        getDouble(getColumnIndexOrThrow(COLUMN_LATITUDE))
                    )
                )
            }
        }
        return locationList
    }


    fun checkIfDataHasBeenCached(): Boolean {
        val db = readableDatabase
        return DatabaseUtils.queryNumEntries(db, TABLE_NAME) > 1
    }
}