package com.example.caloriemeter

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.caloriemeter.caloriesCalculator
import com.example.caloriemeter.calorieCounterRequired

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Находим кнопку "start" и устанавливаем обработчик нажатия
        findViewById<View>(R.id.start).setOnClickListener {
            val intent = Intent(this, caloriesCalculator::class.java)
            startActivity(intent)
        }

        // Находим кнопку "caloriecounter" и устанавливаем обработчик нажатия
        findViewById<View>(R.id.caloriecounter).setOnClickListener {
            val intent = Intent(this, calorieCounterRequired::class.java)
            startActivity(intent)
        }
    }
}
