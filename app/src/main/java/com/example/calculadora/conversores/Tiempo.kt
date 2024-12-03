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
import java.text.DecimalFormat

class Tiempo : AppCompatActivity() {

    // Variable que indica si se está actualizando el valor de los EditText
    private var actualizando = false
    private lateinit var myButtons: Map<Button, String>
    private lateinit var unidadOrigenEditText: EditText
    private lateinit var unidadDestinoEditText: EditText
    private lateinit var editTextActual: EditText

    // Función que se ejecuta al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tiempo)

        // Obtener los EditText de la vista
        unidadOrigenEditText = findViewById(R.id.unidadOrigen)
        unidadDestinoEditText = findViewById(R.id.unidadDestino)

        // Establecer el EditText actual
        editTextActual = unidadOrigenEditText

        // Establecer el EditText actual al hacer clic en él
        unidadOrigenEditText.setOnFocusChangeListener { _, hasFocus ->
            // Si el EditText gana el foco, se establece como el EditText actual
            if (hasFocus) {
                // Establecer el EditText actual
                editTextActual = unidadOrigenEditText
            }
        }

        // Establecer el EditText actual al hacer clic en él
        unidadDestinoEditText.setOnFocusChangeListener { _, hasFocus ->
            // Si el EditText gana el foco, se establece como el EditText actual
            if (hasFocus) {
                // Establecer el EditText actual
                editTextActual = unidadDestinoEditText
            }
        }

        // Obtener los elementos de la vista
        val optionsSpinner: Spinner = findViewById(R.id.spinner1)
        val selectedOptionTextView: TextView = findViewById(R.id.seleccion1)
        val optionsSpinner2: Spinner = findViewById(R.id.spinner2)
        val selectedOptionTextView2: TextView = findViewById(R.id.seleccion2)

        // Opciones de conversión
        val options = arrayOf(
            "Año yr", "Semana sem", "Día d", "Hora h",
            "Minuto min", "Segundo s", "Milisegundo ms",
            "Microsegundo µs", "Picosegundo ps"
        )

        // Botón de retroceso
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }

        // Adaptador para el Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Establecer el adaptador para los Spinners
        optionsSpinner.adapter = adapter
        // Establecer la opción seleccionada
        optionsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // Función que se ejecuta al seleccionar un elemento
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // Establecer el texto de la opción seleccionada
                selectedOptionTextView.text = options[position]
                convertirYActualizar(unidadOrigenEditText, unidadDestinoEditText, optionsSpinner, optionsSpinner2)
            }
            // Función que se ejecuta al no seleccionar ningún elemento
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Establecer el adaptador para los Spinners
        optionsSpinner2.adapter = adapter
        optionsSpinner2.setSelection(options.indexOf("Segundo s"))
        // Establecer la opción seleccionada
        optionsSpinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // Función que se ejecuta al seleccionar un elemento
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // Establecer el texto de la opción seleccionada
                selectedOptionTextView2.text = options[position]
                convertirYActualizar(unidadOrigenEditText, unidadDestinoEditText, optionsSpinner, optionsSpinner2)
            }
            // Función que se ejecuta al no seleccionar ningún elemento
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Establecer el valor inicial de los EditText
        unidadOrigenEditText.setText("1")
        unidadOrigenEditText.setSelection(1)

        // Función que se ejecuta al cambiar el texto de los EditText
        unidadOrigenEditText.addTextChangedListener(object : TextWatcher {
            // Función que se ejecuta al cambiar el texto
            override fun afterTextChanged(s: Editable?) {
                // Si no se está actualizando el valor de los EditText
                if (!actualizando) {
                    // Convertir y actualizar el valor de los EditText
                    convertirYActualizar(unidadOrigenEditText, unidadDestinoEditText, optionsSpinner, optionsSpinner2)
                }
            }
            // Función que se ejecuta antes de cambiar el texto
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Función que se ejecuta al cambiar el texto de los EditText
        unidadDestinoEditText.addTextChangedListener(object : TextWatcher {
            // Función que se ejecuta al cambiar el texto
            override fun afterTextChanged(s: Editable?) {
                // Si no se está actualizando el valor de los EditText
                if (!actualizando) {
                    // Convertir y actualizar el valor de los EditText
                    convertirYActualizar(unidadDestinoEditText, unidadOrigenEditText, optionsSpinner2, optionsSpinner)
                }
            }
            // Función que se ejecuta antes de cambiar el texto
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Función que se ejecuta al presionar una tecla
        val keyListener = View.OnKeyListener { v, keyCode, event ->
            // Si se presiona la tecla Enter
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                // Convertir y actualizar el valor de los EditText
                if (v.id == R.id.unidadOrigen) {
                    // Convertir y actualizar el valor de los EditText
                    convertirYActualizar(unidadOrigenEditText, unidadDestinoEditText, optionsSpinner, optionsSpinner2)
                } // Si se presiona la tecla Enter
                else if (v.id == R.id.unidadDestino) {
                    convertirYActualizar(unidadDestinoEditText, unidadOrigenEditText, optionsSpinner2, optionsSpinner)
                }
                true
            } // Si no se presiona la tecla Enter
            else {
                false
            }
        }

        // Establecer el escuchador de teclas para los EditText
        unidadOrigenEditText.setOnKeyListener(keyListener)
        unidadDestinoEditText.setOnKeyListener(keyListener)

        initButtons()
    }

    // Función que convierte y actualiza el valor de los EditText
    private fun convertirYActualizar(origen: EditText, destino: EditText, spinnerOrigen: Spinner, spinnerDestino: Spinner) {
        // Si se está actualizando el valor de los EditText
        if (actualizando) return

        // Convertir y actualizar el valor de los EditText
        val valor = origen.text.toString().toDoubleOrNull() ?: 0.0
        val unidadOrigen = spinnerOrigen.selectedItem.toString()
        val unidadDestino = spinnerDestino.selectedItem.toString()
        val resultado = convertirUnidad(unidadOrigen, unidadDestino, valor)
        val resultadoFormateado = if (resultado % 1 == 0.0) resultado.toLong().toString() else resultado.toString()

        // Actualizar el valor del EditText de destino
        actualizando = true
        destino.setText(resultadoFormateado)

        // Ajustar el tamaño del texto de los EditText
        ajustarTamañoTexto(destino, resultadoFormateado)
        ajustarTamañoTexto(origen, origen.text.toString())

        // Establecer el cursor al final del texto
        actualizando = false
    }

    // Función que inicializa los botones
    private fun initButtons() {
        // Mapa de botones
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

        // Establecer el escuchador de clics para los botones
        myButtons.forEach { (button, value) ->
            // Función que se ejecuta al hacer clic en un botón
            button.setOnClickListener {
                // Añadir el valor del botón al EditText actual
                editTextActual.append(value)
                // Convertir y actualizar el valor de los EditText
                if (editTextActual == unidadOrigenEditText) {
                    // Convertir y actualizar el valor de los EditText
                    convertirYActualizar(unidadOrigenEditText, unidadDestinoEditText, findViewById(R.id.spinner1), findViewById(R.id.spinner2))
                } // Si se presiona la tecla Enter
                else { // Si se presiona la tecla Enter
                    convertirYActualizar(unidadDestinoEditText, unidadOrigenEditText, findViewById(R.id.spinner2), findViewById(R.id.spinner1))
                }
            }
        }

        // Botón de limpiar
        findViewById<Button>(R.id.cleanAllButton).setOnClickListener {
            unidadOrigenEditText.text.clear()
            unidadDestinoEditText.text.clear()
        }

        // Botón de borrar
        findViewById<ImageButton>(R.id.deleteButton).setOnClickListener {
            // Borrar el último carácter del EditText actual
            val text = editTextActual.text
            // Si el texto no está vacío
            if (text.isNotEmpty()) {
                // Borrar el último carácter
                editTextActual.setText(text.substring(0, text.length - 1))
                editTextActual.setSelection(editTextActual.text.length)
            }
        }
    }

    // Función que ajusta el tamaño del texto de un EditText
    private fun ajustarTamañoTexto(editText: EditText, valor: String) {
        // Ajustar el tamaño del texto del EditText
        var tamañoLetra = 25f
        editText.textSize = tamañoLetra
        var ancho = editText.paint.measureText(valor)
        val anchoEditText = editText.width - editText.paddingLeft - editText.paddingRight

        // Ajustar el tamaño del texto del EditText
        while (ancho > anchoEditText && tamañoLetra > 15f) {
            // Disminuir el tamaño de la letra
            tamañoLetra -= 1f
            editText.textSize = tamañoLetra
            ancho = editText.paint.measureText(valor)
        }

        // Ajustar el tamaño del texto del EditText
        while (ancho < anchoEditText && tamañoLetra < 25f) {
            // Aumentar el tamaño de la letra
            tamañoLetra += 1f
            editText.textSize = tamañoLetra
            ancho = editText.paint.measureText(valor)
        }

        // Ajustar el tamaño del texto del EditText
        if (tamañoLetra < 15f) {
            // Formato científico
            val formato = DecimalFormat("0.###E0")
            editText.setText(formato.format(valor.toDouble()))
        }
    }

    // Función que convierte una unidad a otra
    fun convertirUnidad(unidadOrigen: String, unidadDestino: String, valor: Double): Double {
        // Mapa de conversiones de medidas de tiempo
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

        // Convertir la unidad de origen a la unidad de destino
        val valorEnSegundos = valor * (conversionMedidas[unidadOrigen] ?: error("Unidad de origen no válida"))
        return valorEnSegundos / (conversionMedidas[unidadDestino] ?: error("Unidad de destino no válida"))
    }
}