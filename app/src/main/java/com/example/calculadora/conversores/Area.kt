package com.example.calculadora.conversores

import android.content.Intent
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
import com.example.calculadora.CalculadoraSimple
import com.example.calculadora.R
import java.text.DecimalFormat

class Area : AppCompatActivity() {

    private var actualizando = false
    private lateinit var myButtons: Map<Button, String>
    private lateinit var unidadOrigenEditText: EditText
    private lateinit var unidadDestinoEditText: EditText
    private lateinit var editTextActual: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.area)

        try {
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

            val options = arrayOf(
                "Kilómetro cuadrado km2", "Héctarea ha", "Área a", "Metro cuadrado m2",
                "Decímetro cuadrado dm2", "Centímetro cuadrado cm2", "Micrón cuadrado µm2",
                "Milla cuadrada milla2", "Yarda cuadrada yd2", "Pie cuadrado ft2", "Pulgada cuadrada in2"
            )

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
            optionsSpinner2.setSelection(options.indexOf("Metro cuadrado m2"))
            optionsSpinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    selectedOptionTextView2.text = options[position]
                    convertirYActualizar(unidadOrigenEditText, unidadDestinoEditText, optionsSpinner, optionsSpinner2)
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

            val backButton = findViewById<ImageButton>(R.id.backButton)
            backButton.setOnClickListener {
                onBackPressed()
            }

            unidadOrigenEditText.setText("1")
            unidadOrigenEditText.setSelection(1)

            unidadOrigenEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (actualizando) return
                    convertirYActualizar(unidadOrigenEditText, unidadDestinoEditText, findViewById(R.id.spinner1), findViewById(R.id.spinner2))
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })


            unidadDestinoEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (actualizando) return
                    convertirYActualizar(unidadDestinoEditText, unidadOrigenEditText, findViewById(R.id.spinner2), findViewById(R.id.spinner1))
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

            findViewById<Button>(R.id.ConversorTextView).setOnClickListener {
                val intent = Intent(this, Conversores::class.java)
                startActivity(intent)
            }

            findViewById<Button>(R.id.CalculadoraTextView).setOnClickListener {
                val intent = Intent(this, CalculadoraSimple::class.java)
                startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun convertirYActualizar(origen: EditText, destino: EditText, spinnerOrigen: Spinner, spinnerDestino: Spinner) {
        if (actualizando) return
        if (origen.text.isNullOrEmpty()) {
            actualizando = true
            destino.text.clear()
            actualizando = false
            return
        }

        try {
            val valor = origen.text.toString().toDoubleOrNull() ?: return
            val unidadOrigen = spinnerOrigen.selectedItem.toString()
            val unidadDestino = spinnerDestino.selectedItem.toString()
            val resultado = convertirUnidad(unidadOrigen, unidadDestino, valor)
            val resultadoFormateado = if (resultado % 1 == 0.0) resultado.toLong().toString() else resultado.toString()

            actualizando = true
            destino.setText(resultadoFormateado)

            ajustarTamañoTexto(destino, resultadoFormateado)
            ajustarTamañoTexto(origen, origen.text.toString())

            actualizando = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initButtons() {
        try {
            myButtons = mapOf(
                findViewById<Button>(R.id.number00) to "00",
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun ajustarTamañoTexto(editText: EditText, valor: String) {
        try {
            var tamañoLetra = 25f
            editText.textSize = tamañoLetra
            var ancho = editText.paint.measureText(valor)
            val anchoEditText = editText.width - editText.paddingLeft - editText.paddingRight

            while (ancho > anchoEditText && tamañoLetra > 15f) {
                tamañoLetra -= 1f
                editText.textSize = tamañoLetra
                ancho = editText.paint.measureText(valor)
            }

            while (ancho < anchoEditText && tamañoLetra < 25f) {
                tamañoLetra += 1f
                editText.textSize = tamañoLetra
                ancho = editText.paint.measureText(valor)
            }

            if (tamañoLetra < 15f) {
                val formato = DecimalFormat("0.###E0")
                editText.setText(formato.format(valor.toDouble()))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun convertirUnidad(unidadOrigen: String, unidadDestino: String, valor: Double): Double {
        val conversionMedidas = mapOf(
            "Kilómetro cuadrado km2" to 1e6,
            "Héctarea ha" to 1e4,
            "Área a" to 100.0,
            "Metro cuadrado m2" to 1.0,
            "Decímetro cuadrado dm2" to 0.01,
            "Centímetro cuadrado cm2" to 0.0001,
            "Micrón cuadrado µm2" to 1e-12,
            "Milla cuadrada milla2" to 2.59e6,
            "Yarda cuadrada yd2" to 0.836127,
            "Pie cuadrado ft2" to 0.092903,
            "Pulgada cuadrada in2" to 0.00064516
        )

        val valorEnMetrosCuadrados = valor * (conversionMedidas[unidadOrigen] ?: error("Unidad de origen no válida"))
        return valorEnMetrosCuadrados / (conversionMedidas[unidadDestino] ?: error("Unidad de destino no válida"))
    }
}