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
import com.example.calculadora.databinding.LongitudBinding
import java.text.DecimalFormat

class Tiempo : AppCompatActivity() {

    private var actualizando = false
    private lateinit var longitudBinding: LongitudBinding
    private lateinit var myButtons: Map<Button, String>
    private lateinit var unidadOrigenEditText: EditText
    private lateinit var unidadDestinoEditText: EditText
    private lateinit var editTextActual: EditText // Declare editTextActual

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.longitud)

        unidadOrigenEditText = findViewById(R.id.unidadOrigen)
        unidadDestinoEditText = findViewById(R.id.unidadDestino)

        // Initialize editTextActual with one of the EditTexts
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
            "Año yr", "Semana sem", "Día d", "Hora h",
            "Minuto min", "Segundo s", "Milisegundo ms",
            "Microsegundo µs", "Picosegundo ps"
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
        optionsSpinner2.setSelection(options.indexOf("Segundo s"))
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

        val valor = origen.text.toString().toDoubleOrNull() ?: 0.0
        val unidadOrigen = spinnerOrigen.selectedItem.toString()
        val unidadDestino = spinnerDestino.selectedItem.toString()
        val resultado = convertirUnidad(unidadOrigen, unidadDestino, valor)
        val resultadoFormateado = if (resultado % 1 == 0.0) resultado.toLong().toString() else resultado.toString()

        actualizando = true
        destino.setText(resultadoFormateado)

        ajustarTamañoTexto(destino, resultadoFormateado)
        ajustarTamañoTexto(origen, origen.text.toString())

        actualizando = false
    }

    private fun initButtons() {
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
    }

    private fun ajustarTamañoTexto(editText: EditText, valor: String) {
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
    }

    fun convertirUnidad(unidadOrigen: String, unidadDestino: String, valor: Double): Double {
        val conversionMedidas = mapOf(
            "Año yr" to 3.1536e7,
            "Semana sem" to 604800.0,
            "Día d" to 86400.0,
            "Hora h" to 3600.0,
            "Minuto min" to 60.0,
            "Segundo s" to 1.0,
            "Milisegundo ms" to 0.001,
            "Microsegundo µs" to 1e-6,
            "Picosegundo ps" to 1e-12
        )

        val valorEnSegundos = valor * (conversionMedidas[unidadOrigen] ?: error("Unidad de origen no válida"))
        return valorEnSegundos / (conversionMedidas[unidadDestino] ?: error("Unidad de destino no válida"))
    }
}