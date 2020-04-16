package com.example.exam.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.provider.BaseColumns
import com.example.exam.db.LocationDetailsTable.DETAILS_BANNER
import com.example.exam.db.LocationDetailsTable.DETAILS_COMMENTS
import com.example.exam.db.LocationDetailsTable.DETAILS_ID
import com.example.exam.db.LocationDetailsTable.DETAILS_NAME
import com.example.exam.db.LocationDetailsTable.DETAILS_TABLE
import com.example.exam.db.LocationTable.LOCATION_ICON
import com.example.exam.db.LocationTable.LOCATION_LATITUDE
import com.example.exam.db.LocationTable.LOCATION_LONGITUDE
import com.example.exam.db.LocationTable.LOCATIONS_TABLE
import com.example.exam.db.LocationTable.LOCATION_ID
import com.example.exam.db.LocationTable.LOCATION_NAME
import com.example.exam.Models.Details
import com.example.exam.Models.Location
import com.example.exam.db.LocationTable.LOCATION_SYS_ID


object LocationTable : BaseColumns {
    const val LOCATIONS_TABLE = "location_table"
    const val LOCATION_ID = "_id"
    const val LOCATION_NAME = "name"
    const val LOCATION_ICON = "icon"
    const val LOCATION_LONGITUDE = "longitude"
    const val LOCATION_LATITUDE = "latitude"
    const val LOCATION_SYS_ID = "sys_id"
}

object LocationDetailsTable : BaseColumns {
    const val DETAILS_TABLE = "location_details_table"
    const val DETAILS_ID = "_id"
    const val DETAILS_NAME = "name"
    const val DETAILS_BANNER = "banner"
    const val DETAILS_COMMENTS = "comments"
}

var run: Boolean = false

class LocationDAO(context: Context) : Database(context) {

    //Inserting all locations
    fun insertLocationsAll(locations: MutableList<Location>) {
        val db = writableDatabase
        if (!run) {
            try {
                db.beginTransaction()
                for (location in locations) {
                    val contentValues = ContentValues()
                    contentValues.put(LOCATION_ID, location.id)
                    contentValues.put(LOCATION_NAME, location.name)
                    contentValues.put(LOCATION_ICON, location.icon)
                    contentValues.put(LOCATION_LONGITUDE, location.longitude)
                    contentValues.put(LOCATION_LATITUDE, location.latitude)
                    db.insert(LOCATIONS_TABLE, null, contentValues)
                }
                db.setTransactionSuccessful()
            } catch (e: Exception) {
                println(e.message)
            } finally {
                db.endTransaction()
                run = true

            }
        }
    }

    //Inserting one location details
    fun insertDetailsOne(locationDetails: Details) {
        val db = writableDatabase
        try {
            db.beginTransaction()
            val contentValues = ContentValues()
            contentValues.put(DETAILS_ID, locationDetails.id)
            contentValues.put(DETAILS_NAME, locationDetails.name)
            contentValues.put(DETAILS_BANNER, locationDetails.banner)
            contentValues.put(DETAILS_COMMENTS, locationDetails.comments)
            db.insert(DETAILS_TABLE, null, contentValues)

            db.setTransactionSuccessful()
        } catch (e: Exception) {
            println(e.message)
        } finally {

            db.endTransaction()
        }
    }

    //Getting one location details
    fun getDetailsOne(id: String): Details? {
        val cursor: Cursor =
            readableDatabase.query(
                DETAILS_TABLE,
                null,
                "_id LIKE ?",
                arrayOf(id),
                null,
                null,
                ""
            )
        with(cursor) {
            if (cursor.moveToFirst()) {
                return Details(
                    getLong(getColumnIndexOrThrow(DETAILS_ID)),
                    getString(getColumnIndexOrThrow(DETAILS_NAME)),
                    getString(getColumnIndexOrThrow(DETAILS_COMMENTS)),
                    getString(getColumnIndexOrThrow(DETAILS_BANNER))
                )
            }
        }
        return null
    }


    //Getting all location details
    fun getDetailsAll(): List<Details> {
        val detailsList = mutableListOf<Details>()
        val cursor: Cursor =
            readableDatabase.query(DETAILS_TABLE, null, null, null, null, null, null)
        with(cursor) {
            while (moveToNext()) {
                detailsList.add(
                    Details(
                        getLong(getColumnIndexOrThrow(DETAILS_ID)),
                        getString(getColumnIndexOrThrow(DETAILS_NAME)),
                        getString(getColumnIndexOrThrow(DETAILS_COMMENTS)),
                        getString(getColumnIndexOrThrow(DETAILS_BANNER))
                    )
                )
            }
        }
        return detailsList
    }

    //Getting all location
    fun getLocationsAll(orderBy: String? = null, query: String? = null): MutableList<Location> {
        val locationList = mutableListOf<Location>()
        val cursor: Cursor =
            readableDatabase.query(LOCATIONS_TABLE, null, query, null, null, null, orderBy)
        with(cursor) {
            while (moveToNext()) {
                locationList.add(
                    Location(
                        getLong(getColumnIndexOrThrow(LOCATION_ID)),
                        getString(getColumnIndexOrThrow(LOCATION_NAME)),
                        getString(getColumnIndexOrThrow(LOCATION_ICON)),
                        getDouble(getColumnIndexOrThrow(LOCATION_LONGITUDE)),
                        getDouble(getColumnIndexOrThrow(LOCATION_LATITUDE)),
                        getLong(getColumnIndexOrThrow(LOCATION_SYS_ID))
                    )
                )
            }
        }
        return locationList
    }

    fun getLocationsIdAll(): MutableList<Long> {
        val idList = mutableListOf<Long>()
        val cursor: Cursor = readableDatabase.query(
            LOCATIONS_TABLE, null, null, null, null, null, null
        )

        with(cursor) {
            while (moveToNext()) {
                idList.add(getLong(getColumnIndexOrThrow(LOCATION_ID)))
            }
        }
        return idList
    }

    //Getting all locations by given name
    fun getLocationsByName(query: String): MutableList<Location> {
        val locationList = mutableListOf<Location>()

        val cursor: Cursor = readableDatabase.query(
            LOCATIONS_TABLE, null, "name LIKE '%$query%'", null, null, null, null
        )

        with(cursor) {
            while (moveToNext()) {
                locationList.add(
                    Location(
                        getLong(getColumnIndexOrThrow(LOCATION_ID)),
                        getString(getColumnIndexOrThrow(LOCATION_NAME)),
                        getString(getColumnIndexOrThrow(LOCATION_ICON)),
                        getDouble(getColumnIndexOrThrow(LOCATION_LONGITUDE)),
                        getDouble(getColumnIndexOrThrow(LOCATION_LATITUDE)),
                        getLong(getColumnIndexOrThrow(LOCATION_SYS_ID))
                    )
                )
            }
        }
        return locationList
    }


    fun getLocationById(id: String): Location? {
        val cursor: Cursor = readableDatabase.query(
            LOCATIONS_TABLE, null, "_id LIKE ?",
            arrayOf(id), null, null, null
        )
        with(cursor) {
            if (cursor.moveToFirst()) {
                return Location(
                    getLong(getColumnIndexOrThrow(LOCATION_ID)),
                    getString(getColumnIndexOrThrow(LOCATION_NAME)),
                    getString(getColumnIndexOrThrow(LOCATION_ICON)),
                    getDouble(getColumnIndexOrThrow(LOCATION_LONGITUDE)),
                    getDouble(getColumnIndexOrThrow(LOCATION_LATITUDE)),
                    getLong(getColumnIndexOrThrow(LOCATION_SYS_ID))
                )
            }
        }
        return null
    }


    fun checkIfDetailsIdExists(id: String): Boolean {
        val cursor: Cursor =
            readableDatabase.query(DETAILS_TABLE, null, "_id = $id", null, null, null, null)
        val count: Int = cursor.count
        cursor.close()
        return count > 0
    }

    fun checkIfDataHasBeenCached(): Boolean {
        return DatabaseUtils.queryNumEntries(readableDatabase, LOCATIONS_TABLE) > 1
    }

}