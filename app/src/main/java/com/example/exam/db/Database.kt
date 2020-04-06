package com.example.exam.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.exam.db.LocationTable.COLUMN_API_ID
import com.example.exam.db.LocationTable.COLUMN_ICON
import com.example.exam.db.LocationTable.COLUMN_ID
import com.example.exam.db.LocationTable.COLUMN_LATITUDE
import com.example.exam.db.LocationTable.COLUMN_LONGITUDE
import com.example.exam.db.LocationTable.COLUMN_NAME

const val DATABASE_VERSION: Int = 1
const val DATABASE_NAME: String = "locations_database"


open class Database(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME, null,
    DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase?) {
        val queryCreateLocationsTable =
            "CREATE TABLE ${LocationTable.TABLE_NAME} ($COLUMN_ID INTEGER PRIMARY KEY, TEXT, $COLUMN_NAME TEXT, $COLUMN_ICON TEXT, $COLUMN_API_ID TEXT, $COLUMN_LONGITUDE TEXT, $COLUMN_LATITUDE TEXT)"
        db?.execSQL(queryCreateLocationsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}