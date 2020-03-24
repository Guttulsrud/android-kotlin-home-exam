package com.example.exam.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.provider.BaseColumns
import com.example.exam.db.LocationTable.COLUMN_GEOMETRY
import com.example.exam.db.LocationTable.COLUMN_ID
import com.example.exam.db.LocationTable.COLUMN_PROPERTIES
import com.example.exam.db.LocationTable.COLUMN_TYPE
import com.example.exam.db.LocationTable.TABLE_NAME
import com.example.exam.gson.Geometry
import com.example.exam.gson.Location
import com.example.exam.gson.Properties
import com.google.gson.GsonBuilder


object LocationTable : BaseColumns {
    const val TABLE_NAME = "location_table"
    const val COLUMN_TYPE = "type"
    const val COLUMN_ID = "_id"
    const val COLUMN_PROPERTIES = "properties"
    const val COLUMN_GEOMETRY = "geometry"
}


class LocationDAO(context: Context) : Database(context) {
    var kek: Int = 0

    fun insert(type: String, properties: String, geometry: String): Long? {

        val values = ContentValues().apply {
            put(COLUMN_TYPE, type)
            put(COLUMN_PROPERTIES, properties)
            put(COLUMN_GEOMETRY, geometry)
        }

        return writableDatabase.use {
            it.insert(TABLE_NAME, null, values)
        }
    }

    fun bulkInsert(list: MutableList<Location>) {


    }

    fun update(location: LocationDTO): Int? {
        val values = ContentValues().apply {
            put(COLUMN_TYPE, location.type)
            put(COLUMN_PROPERTIES, location.type)
            put(COLUMN_GEOMETRY, location.type)
        }

        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf(location.id.toString())

        return writableDatabase.use {
            it.update(TABLE_NAME, values, selection, selectionArgs)
        }
    }

    fun fetchAll(): MutableList<Location> {

        val cursor: Cursor = readableDatabase.query(TABLE_NAME, null, null, null, null, null, null)
        val locationList = mutableListOf<Location>()
        with(cursor) {
            while (moveToNext()) {
                val type = getString(getColumnIndexOrThrow(COLUMN_TYPE))
                val properties = getString(getColumnIndexOrThrow(COLUMN_PROPERTIES))
                val geometry = getString(getColumnIndexOrThrow(COLUMN_GEOMETRY))

                val gson = GsonBuilder().create()

                //return array of strings?.....
                val prop = gson.fromJson(properties, Properties::class.java)
                val geo = gson.fromJson(geometry, Geometry::class.java)

                locationList.add(Location(type, prop, geo))
            }
        }
        return locationList
    }


    fun getpaged(limit: Int, from: Int): MutableList<Location> {
        val cursor: Cursor = readableDatabase.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_TYPE, COLUMN_PROPERTIES, COLUMN_GEOMETRY),
            "_id BETWEEN ? AND ?",
            arrayOf(limit.toString(), from.toString()),
            null,
            null,
            null
        )

        val locationList = mutableListOf<Location>()

        with(cursor) {
            while (moveToNext()) {
                val type = getString(getColumnIndexOrThrow(COLUMN_TYPE))
                val properties = getString(getColumnIndexOrThrow(COLUMN_PROPERTIES))
                val geometry = getString(getColumnIndexOrThrow(COLUMN_GEOMETRY))

                val gson = GsonBuilder().create()

                //return array of strings?.....
                val prop = gson.fromJson(properties, Properties::class.java)
                val geo = gson.fromJson(geometry, Geometry::class.java)

                locationList.add(Location(type, prop, geo))


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

    fun delete(id: Long) {

    }


}