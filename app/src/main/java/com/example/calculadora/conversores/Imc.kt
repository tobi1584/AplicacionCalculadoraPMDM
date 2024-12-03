package com.example.calculadora.conversores

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.calculadora.R

class Imc : AppCompatActivity() {

    private lateinit var ageInput: EditText
    private lateinit var heightInput: EditText
    private lateinit var weightInput: EditText
    private lateinit var calculateButton: Button
    private lateinit var maleButton: ImageButton
    private lateinit var femaleButton: ImageButton
    private lateinit var backButton: ImageButton

    private var selectedGender: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.imc)

        ageInput = findViewById(R.id.age_input)
        heightInput = findViewById(R.id.height_input)
        weightInput = findViewById(R.id.weight_input)
        calculateButton = findViewById(R.id.calculate_button)
        maleButton = findViewById(R.id.male_button)
        femaleButton = findViewById(R.id.female_button)
        backButton = findViewById(R.id.backButton)

        setupGenderSelection()

        calculateButton.setOnClickListener {
            calculateIMC()
        }

        setupInputFilters()

        backButton.setOnClickListener {
            val intent = Intent(this, Conversores::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupGenderSelection() {
        maleButton.setOnClickListener {
            selectedGender = "Hombre"
            highlightSelectedGender()
        }

        femaleButton.setOnClickListener {
            selectedGender = "Mujer"
            highlightSelectedGender()
        }
    }

    //Metodo para resaltar el genero seleccionado
    private fun highlightSelectedGender() {
        if (selectedGender == "Hombre") {
            maleButton.setBackgroundResource(R.drawable.selected_background)
            femaleButton.setBackgroundResource(android.R.color.transparent)
        } else if (selectedGender == "Mujer") {
            femaleButton.setBackgroundResource(R.drawable.selected_background)
            maleButton.setBackgroundResource(android.R.color.transparent)
        }
    }

    //Metodo para filtrar los inputs
    private fun setupInputFilters() {
        heightInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.contains(",")) { // Reemplazar comas por puntos
                    heightInput.setText(text.replace(",", "."))
                    heightInput.setSelection(heightInput.text.length)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        //Listener para el peso
        weightInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.contains(",")) {
                    weightInput.setText(text.replace(",", "."))
                    weightInput.setSelection(weightInput.text.length)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    //Metodo para calcular el IMC
    private fun calculateIMC() {
        ageInput.error = null
        heightInput.error = null
        weightInput.error = null

        //Obtener los valores de los inputs
        val ageText = ageInput.text.toString().trim()
        val heightText = heightInput.text.toString().trim()
        val weightText = weightInput.text.toString().trim()

        var valid = true

        val age: Int? = try {
            ageText.toInt()
        } catch (e: NumberFormatException) {
            ageInput.error = "Edad inválida"
            valid = false
            null
        }

        val heightCm: Double? = try {
            heightText.replace(",", ".").toDouble()
        } catch (e: NumberFormatException) {
            heightInput.error = "Altura inválida"
            valid = false
            null
        }

        val weightKg: Double? = try {
            weightText.replace(",", ".").toDouble()
        } catch (e: NumberFormatException) {
            weightInput.error = "Peso inválido"
            valid = false
            null
        }

        if (selectedGender == null) {
            Toast.makeText(this, "Por favor, selecciona un género", Toast.LENGTH_SHORT).show()
            valid = false
        }

        // Si los valores son validos, calcular el IMC
        if (valid && age != null && heightCm != null && weightKg != null) {
            val heightM = heightCm / 100
            val imc = weightKg / (heightM * heightM)

            //Switch para determinar la categoria del IMC
            val category = when {
                imc < 18.5 -> "Bajo peso"
                imc in 18.5..24.9 -> "Peso saludable"
                imc in 25.0..29.9 -> "Sobrepeso"
                else -> "Obesidad"
            }

            //Mostrar un dialogo con el resultado
            AlertDialog.Builder(this)
                .setTitle("Tu IMC")
                .setMessage(String.format("Tu IMC es %.2f (%s)", imc, category))
                .setPositiveButton("Aceptar", null)
                .show()
        }
    }
}
