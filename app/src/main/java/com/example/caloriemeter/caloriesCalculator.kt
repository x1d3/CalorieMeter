package com.example.caloriemeter

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.caloriemeter.dao.Consumption
import com.example.caloriemeter.dao.ConsumptionDao
import com.example.caloriemeter.dao.ProductDao

class caloriesCalculator : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var productDao: ProductDao
    private lateinit var consumptionDao: ConsumptionDao
    private lateinit var tvMorningCalories: TextView
    private lateinit var tvLunchCalories: TextView
    private lateinit var tvEveningCalories: TextView
    private lateinit var morningListView: ListView
    private lateinit var lunchListView: ListView
    private lateinit var eveningListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calories_calculator)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        productDao = dbHelper.productDao
        consumptionDao = dbHelper.consumptionDao

        tvMorningCalories = findViewById(R.id.tvMorningCalories)
        tvLunchCalories = findViewById(R.id.tvLunchCalories)
        tvEveningCalories = findViewById(R.id.tvEveningCalories)
        morningListView = findViewById(R.id.morningListView)
        lunchListView = findViewById(R.id.lunchListView)
        eveningListView = findViewById(R.id.eveningListView)

        findViewById<Button>(R.id.btnAddMorning).setOnClickListener {


            openAddProductActivity("Утро")
        }
        findViewById<Button>(R.id.btnAddLunch).setOnClickListener {
            openAddProductActivity("Обед")
        }
        findViewById<Button>(R.id.btnAddEvening).setOnClickListener {
            openAddProductActivity("Вечер")
        }
        findViewById<Button>(R.id.btnReset).setOnClickListener {
            confirmReset()
        }
    }

    override fun onResume() {
        super.onResume()
        loadCaloriesAndProducts()
    }

    private fun openAddProductActivity(mealTime: String) {
        val intent = Intent(this, AddProductActivity::class.java)
        intent.putExtra("MEAL_TIME", mealTime)
        startActivity(intent)
    }

    private fun loadCaloriesAndProducts() {
        loadCaloriesAndProductsForMealTime("Утро", morningListView, tvMorningCalories)
        loadCaloriesAndProductsForMealTime("Обед", lunchListView, tvLunchCalories)
        loadCaloriesAndProductsForMealTime("Вечер", eveningListView, tvEveningCalories)
    }

    private fun loadCaloriesAndProductsForMealTime(mealTime: String, listView: ListView, caloriesTextView: TextView) {
        val consumptions = consumptionDao.getConsumptionsByMealTime(mealTime)
        val products = consumptions.map {
            "${it.productName}: ${it.caloriesPer100g * it.grams / 100} ккал на ${it.grams} г"
        }
        val totalCalories = consumptions.sumBy { it.caloriesPer100g * it.grams / 100 }
        caloriesTextView.text = getString(R.string.calories_format, mealTime, totalCalories)
        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, products)
    }

    private fun confirmReset() {
        AlertDialog.Builder(this).apply {
            setTitle("Подтверждение сброса")
            setMessage("Вы уверены, что хотите сбросить статистику?")
            setPositiveButton("Да") { dialog, _ ->
                consumptionDao.deleteAllConsumptions()
                loadCaloriesAndProducts()
                dialog.dismiss()
            }
            setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }
            create()
            show()
        }
    }
}
data class Consumption(
    val productName: String,
    val caloriesPer100g: Int,
    val grams: Int
) {
    override fun toString(): String {
        return "$productName: ${caloriesPer100g * grams / 100} ккал на ${grams} г"
    }
}


