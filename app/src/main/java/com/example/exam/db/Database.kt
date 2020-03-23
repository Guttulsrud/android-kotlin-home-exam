package com.example.exam.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.exam.db.LocationTable.COLUMN_ID
import com.example.exam.db.LocationTable.COLUMN_GEOMETRY
import com.example.exam.db.LocationTable.COLUMN_PROPERTIES
import com.example.exam.db.LocationTable.COLUMN_TYPE

const val DATABASE_VERSION: Int = 1
const val DATABASE_NAME: String = "locations_database"


open class Database(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME, null,
    DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase?) {
        val queryCreateLocationsTable =
            "CREATE TABLE ${LocationTable.TABLE_NAME} ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TYPE TEXT, $COLUMN_PROPERTIES TEXT, $COLUMN_GEOMETRY TEXT )"


        db?.execSQL(queryCreateLocationsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}