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
import androidx.core.content.ContextCompat
import com.example.calculadora.R

class SistemaNumeral : AppCompatActivity() {

    // Declaración de variables
    private var actualizando = false
    private lateinit var myButtons: Map<Button, String>
    private lateinit var unidadOrigenEditText: EditText
    private lateinit var unidadDestinoEditText: EditText
    private lateinit var editTextActual: EditText

    // Función que se ejecuta al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sistema_numeral)

        // Obtener referencias a los elementos de la interfaz
        unidadOrigenEditText = findViewById(R.id.unidadOrigen)
        unidadDestinoEditText = findViewById(R.id.unidadDestino)

        // Establecer el EditText actual
        editTextActual = unidadOrigenEditText

        // Actualizar el estado de los botones al cambiar de EditText
        unidadOrigenEditText.setOnFocusChangeListener { _, hasFocus ->
            // Si el EditText de origen tiene el foco, se establece como EditText actual
            if (hasFocus) {
                // Se establece el EditText de origen como EditText actual
                editTextActual = unidadOrigenEditText
                actualizarEstadoBotones()
            }
        }

        // Actualizar el estado de los botones al cambiar de EditText
        unidadDestinoEditText.setOnFocusChangeListener { _, hasFocus ->
            // Si el EditText de destino tiene el foco, se establece como EditText actual
            if (hasFocus) {
                // Se establece el EditText de destino como EditText actual
                editTextActual = unidadDestinoEditText
                actualizarEstadoBotones()
            }
        }

        // Obtener referencias a los elementos de la interfaz
        val optionsSpinner: Spinner = findViewById(R.id.spinner1)
        val selectedOptionTextView: TextView = findViewById(R.id.seleccion1)
        val optionsSpinner2: Spinner = findViewById(R.id.spinner2)
        val selectedOptionTextView2: TextView = findViewById(R.id.seleccion2)

        // Opciones de conversión
        val options = arrayOf("Binario BIN", "Octal OCT", "Decimal DEC", "Hexadecimal HEX")

        // Adaptador para el Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Botón de retroceso
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }

        // Establecer el adaptador y el listener para el Spinner
        optionsSpinner.adapter = adapter
        optionsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // Función que se ejecuta al seleccionar un elemento del Spinner
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // Actualizar el TextView con la opción seleccionada
                selectedOptionTextView.text = options[position]
                convertirYActualizar(unidadOrigenEditText, unidadDestinoEditText, optionsSpinner, optionsSpinner2)
                actualizarEstadoBotones()
            }
            // Función que se ejecuta al no seleccionar ningún elemento del Spinner
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Establecer el adaptador y el listener para el Spinner
        optionsSpinner2.adapter = adapter
        optionsSpinner2.setSelection(options.indexOf("Decimal DEC"))
        // Función que se ejecuta al seleccionar un elemento del Spinner
        optionsSpinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // Función que se ejecuta al seleccionar un elemento del Spinner
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // Actualizar el TextView con la opción seleccionada
                selectedOptionTextView2.text = options[position]
                convertirYActualizar(unidadOrigenEditText, unidadDestinoEditText, optionsSpinner, optionsSpinner2)
                actualizarEstadoBotones()
            }
            // Función que se ejecuta al no seleccionar ningún elemento del Spinner
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Establecer el valor inicial del EditText de origen
        unidadOrigenEditText.setText("1")
        unidadOrigenEditText.setSelection(1)

        // Función que se ejecuta al cambiar el texto del EditText
        unidadOrigenEditText.addTextChangedListener(object : TextWatcher {
            // Función que se ejecuta al cambiar el texto del EditText
            override fun afterTextChanged(s: Editable?) {
                // Si no se está actualizando, se convierte y se actualiza el EditText de destino
                if (!actualizando) {
                    // Se convierte y se actualiza el EditText de destino
                    convertirYActualizar(unidadOrigenEditText, unidadDestinoEditText, optionsSpinner, optionsSpinner2)
                }
            }

            // Función que se ejecuta antes de cambiar el texto del EditText
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            // Función que se ejecuta al cambiar el texto del EditText
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Función que se ejecuta al cambiar el texto del EditText
        unidadDestinoEditText.addTextChangedListener(object : TextWatcher {
            // Función que se ejecuta al cambiar el texto del EditText
            override fun afterTextChanged(s: Editable?) {
                // Si no se está actualizando, se convierte y se actualiza el EditText de origen
                if (!actualizando) {
                    // Se convierte y se actualiza el EditText de origen
                    convertirYActualizar(unidadDestinoEditText, unidadOrigenEditText, optionsSpinner2, optionsSpinner)
                }
            }
            // Función que se ejecuta antes de cambiar el texto del EditText
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            // Función que se ejecuta al cambiar el texto del EditText
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Listener para el botón de retroceso
        val keyListener = View.OnKeyListener { v, keyCode, event ->
            // Si se presiona la tecla Enter, se convierte y se actualiza el EditText correspondiente
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                // Se convierte y se actualiza el EditText correspondiente
                if (v.id == R.id.unidadOrigen) {
                    // Se convierte y se actualiza el EditText de destino
                    convertirYActualizar(unidadOrigenEditText, unidadDestinoEditText, optionsSpinner, optionsSpinner2)
                }
                // Se convierte y se actualiza el EditText correspondiente
                else if (v.id == R.id.unidadDestino) {
                    // Se convierte y se actualiza el EditText de origen
                    convertirYActualizar(unidadDestinoEditText, unidadOrigenEditText, optionsSpinner2, optionsSpinner)
                }
                true
            } else { // Si no se presiona la tecla Enter, se devuelve false
                false
            }
        }

        // Establecer el listener para los EditText
        unidadOrigenEditText.setOnKeyListener(keyListener)
        unidadDestinoEditText.setOnKeyListener(keyListener)

        // Inicializar los botones
        initButtons()
    }

    // Función que se ejecuta al cambiar la orientación de la pantalla
    private fun convertirYActualizar(origen: EditText, destino: EditText, spinnerOrigen: Spinner, spinnerDestino: Spinner) {
        // Si se está actualizando, se devuelve
        if (actualizando) return

        // Se obtiene el valor del EditText de origen
        val valorTexto = origen.text.toString().trim()
        // Si el valor del EditText de origen está vacío, se actualiza el EditText de destino con un valor vacío
        if (valorTexto.isEmpty()) {
            actualizando = true
            destino.setText("")
            actualizando = false
            return
        }

        // Se obtienen las unidades de origen y destino
        val unidadOrigen = spinnerOrigen.selectedItem.toString()
        val unidadDestino = spinnerDestino.selectedItem.toString()
        val resultado = convertirUnidad(unidadOrigen, unidadDestino, valorTexto)

        // Se actualiza el EditText de destino con el resultado
        actualizando = true
        destino.setText(resultado)
        ajustarTamañoTexto(destino, resultado)
        ajustarTamañoTexto(origen, valorTexto)
        actualizando = false
    }


    // Función que se ejecuta al inicializar los botones
    private fun initButtons() {
        // Mapa de botones con su valor
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
            findViewById<Button>(R.id.dotButton) to ".",
            findViewById<Button>(R.id.equalsButton) to "A",
            findViewById<Button>(R.id.plusButton) to "B",
            findViewById<Button>(R.id.minusButton) to "C",
            findViewById<Button>(R.id.multiplyButton) to "D",
            findViewById<Button>(R.id.divideButton) to "E",
            findViewById<Button>(R.id.percentButton) to "F"
        )

        //
        myButtons.forEach { (button, value) ->
            // Listener para los botones
            button.setOnClickListener {
                // Se añade el valor del botón al EditText actual
                editTextActual.append(value)
                // Se ajusta el tamaño del texto del EditText actual
                if (editTextActual == unidadOrigenEditText) {
                    // Se ajusta el tamaño del texto del EditText de origen
                    convertirYActualizar(unidadOrigenEditText, unidadDestinoEditText, findViewById(R.id.spinner1), findViewById(R.id.spinner2))
                } else { // Se ajusta el tamaño del texto del EditText de destino
                    // Se ajusta el tamaño del texto del EditText de destino
                    convertirYActualizar(unidadDestinoEditText, unidadOrigenEditText, findViewById(R.id.spinner2), findViewById(R.id.spinner1))
                }
            }
        }

        // Listener para el botón de limpiar
        findViewById<Button>(R.id.cleanAllButton).setOnClickListener {
            // Se limpian los EditText
            unidadOrigenEditText.text.clear()
            unidadDestinoEditText.text.clear()
        }

        // Listener para el botón de borrar
        findViewById<ImageButton>(R.id.deleteButton).setOnClickListener {
            // Se borra el último carácter del EditText actual
            val text = editTextActual.text
            // Si el texto no está vacío, se borra el último carácter
            if (text.isNotEmpty()) {
                // Se borra el último carácter del EditText actual
                editTextActual.setText(text.substring(0, text.length - 1))
                editTextActual.setSelection(editTextActual.text.length)
            }
        }
    }

    // Función que se ejecuta al actualizar el estado de los botones
    private fun actualizarEstadoBotones() {
        // Se obtiene si la unidad de origen es binaria
        val isBinario = (findViewById<Spinner>(R.id.spinner1).selectedItem.toString() == "Binario BIN" && editTextActual == unidadOrigenEditText) ||
                (findViewById<Spinner>(R.id.spinner2).selectedItem.toString() == "Binario BIN" && editTextActual == unidadDestinoEditText)
        // Se obtiene si la unidad de origen es hexadecimal
        val isHexadecimal = (findViewById<Spinner>(R.id.spinner1).selectedItem.toString() == "Hexadecimal HEX" && editTextActual == unidadOrigenEditText) ||
                (findViewById<Spinner>(R.id.spinner2).selectedItem.toString() == "Hexadecimal HEX" && editTextActual == unidadDestinoEditText)

        // Se actualiza el estado de los botones
        myButtons.forEach { (button, value) ->
            // Si la unidad de origen es binaria y el valor no es 0 ni 1, se deshabilita el botón
            if (isBinario && value != "0" && value != "1" && value != "00") {
                // Se deshabilita el botón
                button.isEnabled = false
                button.setTextColor(ContextCompat.getColor(this, R.color.orangeDisabled))
            } // Si la unidad de origen es hexadecimal y el valor no es un dígito hexadecimal, se deshabilita el botón
            else if (isHexadecimal && value in listOf("A", "B", "C", "D", "E", "F")) {
                // Se deshabilita el botón
                button.isEnabled = true
                button.setTextColor(ContextCompat.getColor(this, R.color.orange))
            } // Si la unidad de origen no es hexadecimal y el valor es un dígito hexadecimal, se deshabilita el botón
            else if (!isHexadecimal && value in listOf("A", "B", "C", "D", "E", "F")) {
                // Se deshabilita el botón
                button.isEnabled = false
                button.setTextColor(ContextCompat.getColor(this, R.color.orangeDisabled))
            } // Si la unidad de origen no es binaria ni hexadecimal, se habilita el botón
            else {
                // Se habilita el botón
                button.isEnabled = true
                button.setTextColor(ContextCompat.getColor(this, R.color.black))
            }
        }
    }

    // Función que se ejecuta al ajustar el tamaño del texto
    private fun ajustarTamañoTexto(editText: EditText, valor: String) {
        // Se ajusta el tamaño del texto del EditText
        var tamañoLetra = 25f
        editText.textSize = tamañoLetra
        var ancho = editText.paint.measureText(valor)
        val anchoEditText = editText.width - editText.paddingLeft - editText.paddingRight

        // Se ajusta el tamaño del texto del EditText
        while (ancho > anchoEditText && tamañoLetra > 15f) {
            // Se ajusta el tamaño del texto del EditText
            tamañoLetra -= 1f
            editText.textSize = tamañoLetra
            ancho = editText.paint.measureText(valor)
        }

        // Se ajusta el tamaño del texto del EditText
        while (ancho < anchoEditText && tamañoLetra < 25f) {
            // Se ajusta el tamaño del texto del EditText
            tamañoLetra += 1f
            editText.textSize = tamañoLetra
            ancho = editText.paint.measureText(valor)
        }
    }

    // Función que se ejecuta al convertir una unidad
    fun convertirUnidad(unidadOrigen: String, unidadDestino: String, valor: String): String {
        // Si el valor está vacío, se devuelve un valor vacío
        if (valor.isBlank()) {
            return ""
        }

        // Se convierte el valor a la unidad de destino
        return try {
            // Se convierte el valor a la unidad de destino
            val decimalValue = when (unidadOrigen) {
                // Se convierte el valor a la unidad de destino
                "Binario BIN" -> valor.toLong(2)
                "Octal OCT" -> valor.toLong(8)
                "Decimal DEC" -> valor.toLong(10)
                "Hexadecimal HEX" -> valor.toLong(16)
                // Si la unidad de origen no es válida, se lanza una excepción
                else -> error("Unidad de origen no válida")
            }

            // Se convierte el valor a la unidad de destino
            when (unidadDestino) {
                "Binario BIN" -> decimalValue.toString(2)
                "Octal OCT" -> decimalValue.toString(8)
                "Decimal DEC" -> decimalValue.toString(10)
                "Hexadecimal HEX" -> decimalValue.toString(16)
                // Si la unidad de destino no es válida, se lanza una excepción
                else -> error("Unidad de destino no válida")
            }
        } // Si se produce una excepción, se devuelve un valor vacío
        catch (e: NumberFormatException) {
            ""
        }
    }

}