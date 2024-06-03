package com.example.caloriemeter

import android.content.DialogInterface
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


class caloriesCalculator : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
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
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery("SELECT p.name, p.calories_per_100g, c.grams FROM Consumption c JOIN Product p ON c.product_id = p.id WHERE c.meal_time = ?", arrayOf(mealTime))


        val products = mutableListOf<String>()
        var totalCalories = 0
        cursor.use { cursor ->
            while (cursor.moveToNext()) {
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val caloriesPer100g = cursor.getInt(cursor.getColumnIndexOrThrow("calories_per_100g"))
                val grams = cursor.getInt(cursor.getColumnIndexOrThrow("grams"))
                val calories = caloriesPer100g * grams / 100
                products.add("$name: $calories ккал на $grams г")
                totalCalories += calories
            }
        }
        caloriesTextView.text = getString(R.string.calories_format, mealTime, totalCalories)
        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, products)
    }

    private fun confirmReset() {
        AlertDialog.Builder(this).apply {
            setTitle("Подтверждение сброса")
            setMessage("Вы уверены, что хотите сбросить статистику?")
            setPositiveButton("Да") { dialog, _ ->
                resetCalories()
                dialog.dismiss()
            }
            setNegativeButton("Нет") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    private fun resetCalories() {
        val db = dbHelper.writableDatabase
        db.execSQL("DELETE FROM Consumption")
        loadCaloriesAndProducts()
    }
}
