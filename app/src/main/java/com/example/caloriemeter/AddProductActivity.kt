package com.example.caloriemeter
import android.content.ContentValues
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class AddProductActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var etGrams: EditText
    private lateinit var btnSave: Button
    private lateinit var btnAddProduct: Button
    private lateinit var searchView: SearchView
    private lateinit var listViewProducts: ListView

    private var products = mutableListOf<Pair<Int, String>>()
    private var selectedProduct: Pair<Int, String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        dbHelper = DatabaseHelper(this)
        etGrams = findViewById(R.id.etGrams)
        btnSave = findViewById(R.id.btnSave)
        btnAddProduct = findViewById(R.id.btnAddProduct)
        searchView = findViewById(R.id.searchView)
        listViewProducts = findViewById(R.id.listViewProducts)

        val mealTime = intent.getStringExtra("MEAL_TIME")

        loadProducts("")

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { loadProducts(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { loadProducts(it) }
                return true
            }
        })

        listViewProducts.setOnItemClickListener { _, _, position, _ ->
            selectedProduct = products[position]
            searchView.setQuery(products[position].second, false)
        }

        btnSave.setOnClickListener {
            saveConsumption(mealTime)
        }

        btnAddProduct.setOnClickListener {
            showAddProductDialog()
        }
    }

    private fun loadProducts(query: String) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, name FROM Product WHERE name LIKE ?", arrayOf("%$query%"))
        products.clear()

        cursor.use {
            while (cursor.moveToNext()) {
                val idIndex = cursor.getColumnIndexOrThrow("id")
                val nameIndex = cursor.getColumnIndexOrThrow("name")
                val id = cursor.getInt(idIndex)
                val name = cursor.getString(nameIndex)

                products.add(Pair(id, name))
            }
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, products.map { it.second })
        listViewProducts.adapter = adapter
    }

    private fun saveConsumption(mealTime: String?) {
        if (mealTime == null || selectedProduct == null) return

        val productId = selectedProduct!!.first
        val grams = etGrams.text.toString().toIntOrNull()

        if (grams == null || grams <= 0) {
            Toast.makeText(this, "Введите корректное количество грамм", Toast.LENGTH_SHORT).show()
            return
        }

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("meal_time", mealTime)
            put("product_id", productId)
            put("grams", grams)
        }

        db.insert("Consumption", null, values)
        finish()
    }

    private fun showAddProductDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_product, null)
        val etProductName = dialogView.findViewById<EditText>(R.id.etProductName)
        val etCalories = dialogView.findViewById<EditText>(R.id.etCalories)

        AlertDialog.Builder(this)
            .setTitle("Добавить продукт")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { dialog, _ ->
                val productName = etProductName.text.toString()
                val calories = etCalories.text.toString().toIntOrNull()


                if (productName.isNotBlank() && calories != null && calories > 0) {
                    saveProduct(productName, calories)
                    loadProducts(searchView.query.toString()) // Обновляем список продуктов после добавления нового
                } else {
                    Toast.makeText(this, "Введите корректные данные", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }

    private fun saveProduct(name: String, calories: Int) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("calories_per_100g", calories)
        }

        db.insert("Product", null, values)
    }
}
