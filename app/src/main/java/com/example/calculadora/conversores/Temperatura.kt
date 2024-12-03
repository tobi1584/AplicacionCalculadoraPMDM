package com.example.calculadora.conversores

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.calculadora.R

class Temperatura : AppCompatActivity() {

    private var actualizando = false
    private lateinit var myButtons: Map<Button, String>
    private lateinit var unidadOrigenEditText: EditText
    private lateinit var unidadDestinoEditText: EditText
    private lateinit var editTextActual: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.temperatura)

        unidadOrigenEditText = findViewById(R.id.unidadOrigen)
        unidadDestinoEditText = findViewById(R.id.unidadDestino)

        editTextActual = unidadOrigenEditText

        unidadOrigenEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                editTextActual = unidadOrigenEditText
            }
        }

        unidadDestinoEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                editTextActual = unidadDestinoEditText
            }
        }

        val optionsSpinner: Spinner = findViewById(R.id.spinner1)
        val selectedOptionTextView: TextView = findViewById(R.id.seleccion1)
        val optionsSpinner2: Spinner = findViewById(R.id.spinner2)
        val selectedOptionTextView2: TextView = findViewById(R.id.seleccion2)

        val options = arrayOf("Celsius Cº", "Fahrenheit ºF", "Kelvin K", "Rankine ºR", "Réaumur ºRe")

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        optionsSpinner.adapter = adapter
        optionsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedOptionTextView.text = options[position]
                convertirYActualizar(unidadOrigenEditText, unidadDestinoEditText, optionsSpinner, optionsSpinner2)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        optionsSpinner2.adapter = adapter
        optionsSpinner2.setSelection(options.indexOf("Fahrenheit ºF"))
        optionsSpinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedOptionTextView2.text = options[position]
                convertirYActualizar(unidadOrigenEditText, unidadDestinoEditText, optionsSpinner, optionsSpinner2)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        unidadOrigenEditText.setText("1")
        unidadOrigenEditText.setSelection(1)

        unidadOrigenEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!actualizando) {
                    convertirYActualizar(unidadOrigenEditText, unidadDestinoEditText, optionsSpinner, optionsSpinner2)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        unidadDestinoEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!actualizando) {
                    convertirYActualizar(unidadDestinoEditText, unidadOrigenEditText, optionsSpinner2, optionsSpinner)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val keyListener = View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                if (v.id == R.id.unidadOrigen) {
                    convertirYActualizar(unidadOrigenEditText, unidadDestinoEditText, optionsSpinner, optionsSpinner2)
                } else if (v.id == R.id.unidadDestino) {
                    convertirYActualizar(unidadDestinoEditText, unidadOrigenEditText, optionsSpinner2, optionsSpinner)
                }
                true
            } else {
                false
            }
        }

        unidadOrigenEditText.setOnKeyListener(keyListener)
        unidadDestinoEditText.setOnKeyListener(keyListener)

        initButtons()
    }

    private fun convertirYActualizar(origen: EditText, destino: EditText, spinnerOrigen: Spinner, spinnerDestino: Spinner) {
        if (actualizando) return

        val valorTexto = origen.text.toString().trim()
        if (valorTexto.isEmpty()) {
            actualizando = true
            destino.setText("")
            actualizando = false
            return
        }

        val unidadOrigen = spinnerOrigen.selectedItem.toString()
        val unidadDestino = spinnerDestino.selectedItem.toString()
        val resultado = convertirUnidad(unidadOrigen, unidadDestino, valorTexto.toDouble())

        actualizando = true
        destino.setText(resultado.toString())
        actualizando = false
    }

    private fun initButtons() {
        myButtons = mapOf(
            findViewById<Button>(R.id.number0) to "0",
            findViewById<Button>(R.id.number1) to "1",
            findViewById<Button>(R.id.number2) to "2",
            findViewById<Button>(R.id.number3) to "3",
            findViewById<Button>(R.id.number4) to "4",
            findViewById<Button>(R.id.number5) to "5",
            findViewById<Button>(R.id.number6) to "6",
            findViewById<Button>(R.id.number7) to "7",
            findViewById<Button>(R.id.number8) to "8",
            findViewById<Button>(R.id.number9) to "9",
            findViewById<Button>(R.id.dotButton) to "."
        )

        myButtons.forEach { (button, value) ->
            button.setOnClickListener {
                editTextActual.append(value)
                if (editTextActual == unidadOrigenEditText) {
                    convertirYActualizar(unidadOrigenEditText, unidadDestinoEditText, findViewById(R.id.spinner1), findViewById(R.id.spinner2))
                } else {
                    convertirYActualizar(unidadDestinoEditText, unidadOrigenEditText, findViewById(R.id.spinner2), findViewById(R.id.spinner1))
                }
            }
        }

        findViewById<Button>(R.id.cleanAllButton).setOnClickListener {
            unidadOrigenEditText.text.clear()
            unidadDestinoEditText.text.clear()
        }

        findViewById<ImageButton>(R.id.deleteButton).setOnClickListener {
            val text = editTextActual.text
            if (text.isNotEmpty()) {
                editTextActual.setText(text.substring(0, text.length - 1))
                editTextActual.setSelection(editTextActual.text.length)
            }
        }
    }

    private fun convertirUnidad(unidadOrigen: String, unidadDestino: String, valor: Double): Double {
        val celsius = when (unidadOrigen) {
            "Celsius Cº" -> valor
            "Fahrenheit ºF" -> (valor - 32) * 5 / 9
            "Kelvin K" -> valor - 273.15
            "Rankine ºR" -> (valor - 491.67) * 5 / 9
            "Réaumur ºRe" -> valor * 5 / 4
            else -> throw IllegalArgumentException("Unidad de origen no válida")
        }

        return when (unidadDestino) {
            "Celsius Cº" -> celsius
            "Fahrenheit ºF" -> celsius * 9 / 5 + 32
            "Kelvin K" -> celsius + 273.15
            "Rankine ºR" -> celsius * 9 / 5 + 491.67
            "Réaumur ºRe" -> celsius * 4 / 5
            else -> throw IllegalArgumentException("Unidad de destino no válida")
        }
    }
}