package com.example.exam.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.exam.db.LocationTable.LOCATION_ICON
import com.example.exam.db.LocationTable.LOCATION_ID
import com.example.exam.db.LocationTable.LOCATION_LATITUDE
import com.example.exam.db.LocationTable.LOCATION_LONGITUDE
import com.example.exam.db.LocationTable.LOCATION_NAME
import com.example.exam.db.LocationDetailsTable.DETAILS_BANNER
import com.example.exam.db.LocationDetailsTable.DETAILS_ID
import com.example.exam.db.LocationDetailsTable.DETAILS_NAME
import com.example.exam.db.LocationDetailsTable.DETAILS_COMMENTS

const val DATABASE_VERSION: Int = 1
const val DATABASE_NAME: String = "locations_database"

open class Database(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME, null,
    DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase?) {

        //Inserting tables
        val queryCreateLocationsTable =
            "CREATE TABLE ${LocationTable.LOCATIONS_TABLE} ($LOCATION_ID INTEGER PRIMARY KEY, TEXT, $LOCATION_NAME TEXT, $LOCATION_ICON TEXT, $LOCATION_LONGITUDE TEXT, $LOCATION_LATITUDE TEXT)"
        val queryCreateDetailsTable = "CREATE TABLE ${LocationDetailsTable.DETAILS_TABLE} ($DETAILS_ID INTEGER PRIMARY KEY, TEXT, $DETAILS_NAME TEXT, $DETAILS_BANNER TEXT, $DETAILS_COMMENTS TEXT)"

        db?.execSQL(queryCreateLocationsTable)
        db?.execSQL(queryCreateDetailsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Do nothing!
    }

}