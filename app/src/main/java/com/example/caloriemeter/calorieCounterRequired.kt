package com.example.caloriemeter

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class calorieCounterRequired : AppCompatActivity() {
    private lateinit var weightInput: EditText
    private lateinit var heightInput: EditText
    private lateinit var ageInput: EditText
    private lateinit var genderGroup: RadioGroup
    private lateinit var activityGroup: RadioGroup
    private lateinit var calculateButton: Button
    private lateinit var resultView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calorie_counter_required)

        weightInput = findViewById(R.id.weight_input)
        heightInput = findViewById(R.id.height_input)
        ageInput = findViewById(R.id.age_input)
        genderGroup = findViewById(R.id.gender_group)
        activityGroup = findViewById(R.id.radioGroup)
        calculateButton = findViewById(R.id.button)
        resultView = findViewById(R.id.editTextText111)

        calculateButton.setOnClickListener {
            calculateCalories()
        }
    }

    private fun calculateCalories() {
        val weight = weightInput.text.toString().toDoubleOrNull() ?: return
        val height = heightInput.text.toString().toDoubleOrNull() ?: return
        val age = ageInput.text.toString().toIntOrNull() ?: return
        val gender = when (genderGroup.checkedRadioButtonId) {
            R.id.gender_male -> "male"
            R.id.gender_female -> "female"
            else -> return
        }
        val activity = when (activityGroup.checkedRadioButtonId) {
            R.id.radio_minimal -> "min"
            R.id.radio_medium -> "medium"
            R.id.radio_high -> "high"
            R.id.radio_very_high -> "very_high"
            else -> return
        }

        val caloriesNorm = getCaloriesNorm(weight, height, age, gender, activity)
        val minimalCalories = getCaloriesMinimal(caloriesNorm)
        val maximalCalories = getCaloriesMaximal(caloriesNorm)

        resultView.text = " $caloriesNorm"
    }

    private fun getActivityRatio(activity: String): Double {
        return when (activity) {
            "min" -> 1.2
            "low" -> 1.375
            "medium" -> 1.55
            "high" -> 1.725
            "very_high" -> 1.9
            else -> 1.0
        }
    }

    private fun getCaloriesNorm(weight: Double, height: Double, age: Int, gender: String, activity: String): Int {
        val genderConstant = if (gender == "male") 5 else -161
        val activityRatio = getActivityRatio(activity)
        return ((10 * weight + 6.25 * height - 5 * age + genderConstant) * activityRatio).toInt()
    }

    private fun getCaloriesMinimal(caloriesNorm: Int): Int {
        return (caloriesNorm * 0.85).toInt()
    }

    private fun getCaloriesMaximal(caloriesNorm: Int): Int {
        return (caloriesNorm * 1.15).toInt()
    }
}
