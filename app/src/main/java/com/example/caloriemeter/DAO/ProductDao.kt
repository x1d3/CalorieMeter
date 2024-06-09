package com.example.caloriemeter.dao

interface ProductDao {
    fun getAllProducts(query: String): List<Pair<Int, String>>
    fun insertProduct(name: String, calories: Int)
}