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

    // Variable para evitar que se actualicen los EditText de forma recursiva
    private var actualizando = false
    private lateinit var myButtons: Map<Button, String>
    private lateinit var unidadOrigenEditText: EditText
    private lateinit var unidadDestinoEditText: EditText
    private lateinit var editTextActual: EditText

    // Función que se ejecuta al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.temperatura)

        // Obtener referencias a los elementos de la interfaz
        unidadOrigenEditText = findViewById(R.id.unidadOrigen)
        unidadDestinoEditText = findViewById(R.id.unidadDestino)

        editTextActual = unidadOrigenEditText

        // Listener para saber cuál es el EditText activo
        unidadOrigenEditText.setOnFocusChangeListener { _, hasFocus ->
            // Si el EditText gana el foco, se asigna a la variable editTextActual
            if (hasFocus) {
                editTextActual = unidadOrigenEditText
            }
        }

        // Listener para saber cuál es el EditText activo
        unidadDestinoEditText.setOnFocusChangeListener { _, hasFocus ->
            // Si el EditText gana el foco, se asigna a la variable editTextActual
            if (hasFocus) {
                editTextActual = unidadDestinoEditText
            }
        }

        // Obtener referencias a los elementos de la interfaz
        val optionsSpinner: Spinner = findViewById(R.id.spinner1)
        val selectedOptionTextView: TextView = findViewById(R.id.seleccion1)
        val optionsSpinner2: Spinner = findViewById(R.id.spinner2)
        val selectedOptionTextView2: TextView = findViewById(R.id.seleccion2)

        val options = arrayOf("Celsius Cº", "Fahrenheit ºF", "Kelvin K", "Rankine ºR", "Réaumur ºRe")

        // Botón para volver a la actividad anterior
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }

        // Adaptador para el Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Asignar el adaptador al Spinner
        optionsSpinner.adapter = adapter
        // Seleccionar la primera opción por defecto
        optionsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // Función que se ejecuta al seleccionar un elemento del Spinner
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // Actualizar el TextView con la opción seleccionada
                selectedOptionTextView.text = options[position]
                convertirYActualizar(unidadOrigenEditText, unidadDestinoEditText, optionsSpinner, optionsSpinner2)
            }
            // Función que se ejecuta al no seleccionar ningún elemento del Spinner
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Asignar el adaptador al Spinner
        optionsSpinner2.adapter = adapter
        optionsSpinner2.setSelection(options.indexOf("Fahrenheit ºF"))
        // Seleccionar la primera opción por defecto
        optionsSpinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // Función que se ejecuta al seleccionar un elemento del Spinner
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // Actualizar el TextView con la opción seleccionada
                selectedOptionTextView2.text = options[position]
                convertirYActualizar(unidadOrigenEditText, unidadDestinoEditText, optionsSpinner, optionsSpinner2)
            }
            // Función que se ejecuta al no seleccionar ningún elemento del Spinner
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Inicializar los EditText con un valor por defecto
        unidadOrigenEditText.setText("1")
        unidadOrigenEditText.setSelection(1)

        // Listener para detectar cambios en los EditText
        unidadOrigenEditText.addTextChangedListener(object : TextWatcher {
            // Función que se ejecuta al cambiar el texto del EditText
            override fun afterTextChanged(s: Editable?) {
                // Si no se está actualizando, se convierte y actualiza el otro EditText
                if (!actualizando) {
                    // Convertir y actualizar el otro EditText
                    convertirYActualizar(unidadOrigenEditText, unidadDestinoEditText, optionsSpinner, optionsSpinner2)
                }
            }
            // Funciones que se ejecutan al cambiar el texto del EditText
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            // Funciones que se ejecutan al cambiar el texto del EditText
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Listener para detectar cambios en los EditText
        unidadDestinoEditText.addTextChangedListener(object : TextWatcher {
            // Función que se ejecuta al cambiar el texto del EditText
            override fun afterTextChanged(s: Editable?) {
                // Si no se está actualizando, se convierte y actualiza el otro EditText
                if (!actualizando) {
                    // Convertir y actualizar el otro EditText
                    convertirYActualizar(unidadDestinoEditText, unidadOrigenEditText, optionsSpinner2, optionsSpinner)
                }
            }
            // Funciones que se ejecutan al cambiar el texto del EditText
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Listener para detectar la pulsación de la tecla Enter
        val keyListener = View.OnKeyListener { v, keyCode, event ->
            // Si se pulsa la tecla Enter, se convierte y actualiza el otro EditText
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                // Convertir y actualizar el otro EditText
                if (v.id == R.id.unidadOrigen) {
                    // Convertir y actualizar el otro EditText
                    convertirYActualizar(unidadOrigenEditText, unidadDestinoEditText, optionsSpinner, optionsSpinner2)
                } // Convertir y actualizar el otro EditText
                else if (v.id == R.id.unidadDestino) {
                    // Convertir y actualizar el otro EditText
                    convertirYActualizar(unidadDestinoEditText, unidadOrigenEditText, optionsSpinner2, optionsSpinner)
                }
                true
            } // Si no se pulsa la tecla Enter, se devuelve false
            else {
                false
            }
        }

        // Asignar el Listener a los EditText
        unidadOrigenEditText.setOnKeyListener(keyListener)
        unidadDestinoEditText.setOnKeyListener(keyListener)

        // Inicializar los botones
        initButtons()
    }

    // Función para convertir y actualizar los EditText
    private fun convertirYActualizar(origen: EditText, destino: EditText, spinnerOrigen: Spinner, spinnerDestino: Spinner) {
        // Si se está actualizando, se sale de la función
        if (actualizando) return

        // Se obtiene el valor del EditText de origen
        val valorTexto = origen.text.toString().trim()
        // Si el valor está vacío, se actualiza el EditText de destino con un valor vacío
        if (valorTexto.isEmpty()) {
            // Se actualiza el EditText de destino con un valor vacío
            actualizando = true
            destino.setText("")
            actualizando = false
            return
        }

        // Se convierte el valor del EditText de origen al tipo Double
        val unidadOrigen = spinnerOrigen.selectedItem.toString()
        val unidadDestino = spinnerDestino.selectedItem.toString()
        val resultado = convertirUnidad(unidadOrigen, unidadDestino, valorTexto.toDouble())

        // Se actualiza el EditText de destino con el resultado
        actualizando = true
        destino.setText(resultado.toString())
        actualizando = false
    }

    // Función para inicializar los botones
    private fun initButtons() {
        // Mapa con los botones y sus valores
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

        // Asignar un Listener a cada botón
        myButtons.forEach { (button, value) ->
            // Listener para detectar la pulsación de un botón
            button.setOnClickListener {
                // Se añade el valor del botón al EditText activo
                editTextActual.append(value)
                // Se coloca el cursor al final del EditText
                if (editTextActual == unidadOrigenEditText) {
                    // Convertir y actualizar el otro EditText
                    convertirYActualizar(unidadOrigenEditText, unidadDestinoEditText, findViewById(R.id.spinner1), findViewById(R.id.spinner2))
                } // Convertir y actualizar el otro EditText
                else {
                    // Convertir y actualizar el otro EditText
                    convertirYActualizar(unidadDestinoEditText, unidadOrigenEditText, findViewById(R.id.spinner2), findViewById(R.id.spinner1))
                }
            }
        }

        // Listener para el botón de limpiar
        findViewById<Button>(R.id.cleanAllButton).setOnClickListener {
            // Limpiar los EditText
            unidadOrigenEditText.text.clear()
            unidadDestinoEditText.text.clear()
        }

        // Listener para el botón de borrar
        findViewById<ImageButton>(R.id.deleteButton).setOnClickListener {
            // Borrar el último carácter del EditText activo
            val text = editTextActual.text
            // Si el texto no está vacío, se borra el último carácter
            if (text.isNotEmpty()) {
                // Se borra el último carácter del EditText
                editTextActual.setText(text.substring(0, text.length - 1))
                editTextActual.setSelection(editTextActual.text.length)
            }
        }
    }

    // Función para convertir una unidad a otra
    private fun convertirUnidad(unidadOrigen: String, unidadDestino: String, valor: Double): Double {
        // Convertir el valor a la unidad de origen
        val celsius = when (unidadOrigen) {
            "Celsius Cº" -> valor
            "Fahrenheit ºF" -> (valor - 32) * 5 / 9
            "Kelvin K" -> valor - 273.15
            "Rankine ºR" -> (valor - 491.67) * 5 / 9
            "Réaumur ºRe" -> valor * 5 / 4
            else -> throw IllegalArgumentException("Unidad de origen no válida")
        }

        // Convertir el valor a la unidad de destino
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