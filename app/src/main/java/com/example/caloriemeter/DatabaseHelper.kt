package com.example.caloriemeter

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.caloriemeter.dao.ConsumptionDao
import com.example.caloriemeter.dao.ConsumptionDaoImpl
import com.example.caloriemeter.dao.ProductDao
import com.example.caloriemeter.dao.ProductDaoImpl

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    val productDao: ProductDao = ProductDaoImpl(this)
    val consumptionDao: ConsumptionDao = ConsumptionDaoImpl(this)

    companion object {
        private const val DATABASE_NAME = "caloriemeter.db"
        private const val DATABASE_VERSION = 1

        private const val SQL_CREATE_PRODUCT = """
            CREATE TABLE Product (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                calories_per_100g INTEGER NOT NULL
            )
        """

        private const val SQL_CREATE_CONSUMPTION = """
            CREATE TABLE Consumption (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                meal_time TEXT NOT NULL,
                product_id INTEGER NOT NULL,
                grams INTEGER NOT NULL,
                FOREIGN KEY (product_id) REFERENCES Product(id)
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_PRODUCT)
        db?.execSQL(SQL_CREATE_CONSUMPTION)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS Product")
        db?.execSQL("DROP TABLE IF EXISTS Consumption")
        onCreate(db)
    }
}
