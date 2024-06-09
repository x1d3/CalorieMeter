package com.example.caloriemeter.dao

import android.content.ContentValues
import com.example.caloriemeter.DatabaseHelper

class ProductDaoImpl(private val dbHelper: DatabaseHelper) : ProductDao {
    override fun getAllProducts(query: String): List<Pair<Int, String>> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, name FROM Product WHERE name LIKE ?", arrayOf("%$query%"))
        val products = mutableListOf<Pair<Int, String>>()
        cursor.use {
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                products.add(Pair(id, name))
            }
        }
        return products
    }

    override fun insertProduct(name: String, calories: Int) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("calories_per_100g", calories)
        }
        db.insert("Product", null, values)
    }
}