package com.example.calculadora.conversores

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.calculadora.R
import java.text.DecimalFormat

class Longitud : AppCompatActivity() {

    private var actualizando = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.longitud)

        val optionsSpinner: Spinner = findViewById(R.id.spinner1)
        val selectedOptionTextView: TextView = findViewById(R.id.seleccion1)
        val optionsSpinner2: Spinner = findViewById(R.id.spinner2)
        val selectedOptionTextView2: TextView = findViewById(R.id.seleccion2)
        val unidadOrigenEditText: EditText = findViewById(R.id.unidadOrigen)
        val unidadDestinoEditText: EditText = findViewById(R.id.unidadDestino)

        val options = arrayOf("Kilómetro km", "Metro m", "Decímetro dm", "Centímetro cm",
            "Milímetro mm", "Micrómetro µm", "Nanómetro nm", "Picómetro pm", "Milla náutica nmi",
            "Milla mi", "Yarda yd", "Pie ft", "Pulgada in", "Pársec pc", "Distancia lunar ld",
            "Unidad astronómica ua", "Año luz ly")

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
        optionsSpinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedOptionTextView2.text = options[position]
                convertirYActualizar(unidadDestinoEditText, unidadOrigenEditText, optionsSpinner2, optionsSpinner)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        unidadOrigenEditText.setText("1")
        unidadOrigenEditText.setSelection(1)

        unidadOrigenEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!actualizando) {
                    if (s.isNullOrEmpty()) {
                        unidadOrigenEditText.setText("0")
                        unidadOrigenEditText.setSelection(1)
                    } else if (s.toString() == "0") {
                        unidadOrigenEditText.setText("")
                    }
                    convertirYActualizar(unidadOrigenEditText, unidadDestinoEditText, optionsSpinner, optionsSpinner2)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        unidadDestinoEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!actualizando) {
                    if (s.isNullOrEmpty()) {
                        unidadDestinoEditText.setText("0")
                        unidadDestinoEditText.setSelection(1)
                    } else if (s.toString() == "0") {
                        unidadDestinoEditText.setText("")
                    }
                    convertirYActualizar(unidadDestinoEditText, unidadOrigenEditText, optionsSpinner2, optionsSpinner)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        unidadOrigenEditText.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                convertirYActualizar(unidadOrigenEditText, unidadDestinoEditText, optionsSpinner, optionsSpinner2)
                true
            } else {
                false
            }
        }

        unidadDestinoEditText.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                convertirYActualizar(unidadDestinoEditText, unidadOrigenEditText, optionsSpinner2, optionsSpinner)
                true
            } else {
                false
            }
        }
    }

    private fun convertirYActualizar(origen: EditText, destino: EditText, spinnerOrigen: Spinner, spinnerDestino: Spinner) {
        val valor = origen.text.toString().toDoubleOrNull() ?: 0.0
        val unidadOrigen = spinnerOrigen.selectedItem.toString()
        val unidadDestino = spinnerDestino.selectedItem.toString()
        val resultado = convertirUnidad(unidadOrigen, unidadDestino, valor)
        var resultadoFormateado = resultado.toString()

        actualizando = true
        destino.setText(resultadoFormateado)

        ajustarTamañoTexto(destino, resultadoFormateado)
        ajustarTamañoTexto(origen, origen.text.toString())

        actualizando = false
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
            "Kilómetro km" to 1000.0,
            "Metro m" to 1.0,
            "Decímetro dm" to 0.1,
            "Centímetro cm" to 0.01,
            "Milímetro mm" to 0.001,
            "Micrómetro µm" to 1e-6,
            "Nanómetro nm" to 1e-9,
            "Picómetro pm" to 1e-12,
            "Milla náutica nmi" to 1852.0,
            "Milla mi" to 1609.34,
            "Yarda yd" to 0.9144,
            "Pie ft" to 0.3048,
            "Pulgada in" to 0.0254,
            "Pársec pc" to 3.0857e16,
            "Distancia lunar ld" to 3.844e8,
            "Unidad astronómica ua" to 1.496e11,
            "Año luz ly" to 9.461e15
        )

        val valorEnMetros = valor * (conversionMedidas[unidadOrigen] ?: error("Unidad de origen no válida"))
        return valorEnMetros / (conversionMedidas[unidadDestino] ?: error("Unidad de destino no válida"))
    }
}