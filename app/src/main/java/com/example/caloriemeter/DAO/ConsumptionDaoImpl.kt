package com.example.caloriemeter.dao

import android.content.ContentValues
import com.example.caloriemeter.DatabaseHelper

class ConsumptionDaoImpl(private val dbHelper: DatabaseHelper) : ConsumptionDao {
    override fun getConsumptionsByMealTime(mealTime: String): List<Consumption> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT p.name, p.calories_per_100g, c.grams FROM Consumption c JOIN Product p ON c.product_id = p.id WHERE c.meal_time = ?", arrayOf(mealTime))
        val consumptions = mutableListOf<Consumption>()
        cursor.use {
            while (cursor.moveToNext()) {
                val productName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val caloriesPer100g = cursor.getInt(cursor.getColumnIndexOrThrow("calories_per_100g"))
                val grams = cursor.getInt(cursor.getColumnIndexOrThrow("grams"))
                consumptions.add(Consumption(productName, caloriesPer100g, grams))
            }
        }
        return consumptions
    }

    override fun insertConsumption(mealTime: String, productId: Int, grams: Int) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("meal_time", mealTime)
            put("product_id", productId)
            put("grams", grams)
        }
        db.insert("Consumption", null, values)
    }

    override fun deleteAllConsumptions() {
        val db = dbHelper.writableDatabase
        db.execSQL("DELETE FROM Consumption")
    }
}