package com.example.caloriemeter.dao

data class Consumption(val productName: String, val caloriesPer100g: Int, val grams: Int)

interface ConsumptionDao {
    fun getConsumptionsByMealTime(mealTime: String): List<Consumption>
    fun insertConsumption(mealTime: String, productId: Int, grams: Int)
    fun deleteAllConsumptions()
}