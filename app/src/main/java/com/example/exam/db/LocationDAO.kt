package com.example.exam.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.provider.BaseColumns
import com.example.exam.db.LocationTable.COLUMN_API_ID
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
    const val COLUMN_API_ID = "nfl_id"
    const val COLUMN_LONGITUDE = "longitude"
    const val COLUMN_LATITUDE = "latitude"
}

var run: Boolean = false

class LocationDAO(context: Context) : Database(context) {

    fun insertData(locations: MutableList<Location>) {
        val db = writableDatabase

        if (!run) {
            try {
                db.beginTransaction()
                for (location in locations) {
                    val contentValues = ContentValues()
                    contentValues.put(COLUMN_NAME, location.name)
                    contentValues.put(COLUMN_ICON, location.icon)
                    contentValues.put(COLUMN_API_ID, location.apiId)
                    contentValues.put(COLUMN_LONGITUDE, location.longitude)
                    contentValues.put(COLUMN_LATITUDE, location.latitude)
                    db.insert(TABLE_NAME, null, contentValues)
                }

                db.setTransactionSuccessful()
            } catch (e: Exception) {
                println("kek no work")
                println(e.message)

                //todo: proper exception catch
            } finally {
                db.endTransaction()
                println("db.endTransaction()")
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

    fun getLocationsAll(): MutableList<Location> {

        val cursor: Cursor = readableDatabase.query(TABLE_NAME, null, null, null, null, null, null)
        val locationList = mutableListOf<Location>()
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(COLUMN_ID))
                val name = getString(getColumnIndexOrThrow(COLUMN_NAME))
                val icon = getString(getColumnIndexOrThrow(COLUMN_ICON))
                val apiId = getLong(getColumnIndexOrThrow(COLUMN_API_ID))
                val longitude = getDouble(getColumnIndexOrThrow(COLUMN_LONGITUDE))
                val latitude = getDouble(getColumnIndexOrThrow(COLUMN_LATITUDE))

                locationList.add(Location(id, name, icon, apiId, longitude, latitude))
            }
        }
        return locationList
    }


    fun getLocationByName(query:String): MutableList<Location> {
        val locationList = mutableListOf<Location>()

        val cursor: Cursor = readableDatabase.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_ICON, COLUMN_API_ID, COLUMN_LATITUDE, COLUMN_LONGITUDE),
            "name LIKE '%$query%'",
            null,
            null,
            null,
            null
        )


        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(COLUMN_ID))
                val name = getString(getColumnIndexOrThrow(COLUMN_NAME))
                val icon = getString(getColumnIndexOrThrow(COLUMN_ICON))
                val apiId = getLong(getColumnIndexOrThrow(COLUMN_API_ID))
                val longitude = getDouble(getColumnIndexOrThrow(COLUMN_LONGITUDE))
                val latitude = getDouble(getColumnIndexOrThrow(COLUMN_LATITUDE))
                locationList.add(Location(id, name, icon, apiId, longitude, latitude))
            }
        }

        return locationList
    }


    fun getLocationCount(): Long {
        val db = readableDatabase
        val count = DatabaseUtils.queryNumEntries(db, TABLE_NAME)
        db.close()
        return count
    }
}