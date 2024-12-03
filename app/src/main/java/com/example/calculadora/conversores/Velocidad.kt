package com.example.calculadora.conversores

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.calculadora.R
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

class Velocidad : AppCompatActivity() {

    // Declaración de variables
    private lateinit var unitSpinner1: Spinner
    private lateinit var unitSpinner2: Spinner
    private lateinit var inputEditText: EditText
    private lateinit var outputEditText: EditText

    // Declaración de botones
    private lateinit var cleanAllButton: Button
    private lateinit var deleteButton: ImageButton
    private lateinit var divideButton: Button
    private lateinit var multiplyButton: Button
    private lateinit var minusButton: Button
    private lateinit var plusButton: Button
    private lateinit var equalsButton: Button
    private lateinit var dotButton: Button
    private lateinit var number00Button: Button
    private lateinit var percentButton: Button
    private lateinit var numberButtons: List<Button>
    private lateinit var backButton: ImageButton

    // Mapa de conversión
    private val conversionMap = mapOf(
        "Kilómetro por hora km/h" to 1000.0 / 3600.0,
        "Metro por segundo m/s" to 1.0,
        "Milla por hora mph" to 1609.344 / 3600.0,
        "Milla náutica nmi" to 1852.0 / 3600.0,
        "Pie por segundo ft/s" to 0.3048,
        "Yarda por segundo yd/s" to 0.9144
    )

    // Variable para saber si es una nueva conversión
    private var isNewCalculation: Boolean = true

    // Variable para el idioma
    private val locale = Locale("es", "ES")

    // Función que se ejecuta al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        // Llamamos a la función onCreate de la clase padre
        super.onCreate(savedInstanceState)
        setContentView(R.layout.velocidad)
        initUI()
        setupConverter()
        setupCalculator()

        // Botón para regresar a la pantalla de conversores
        backButton = findViewById(R.id.backButton)
        // Función que se ejecuta al hacer clic en el botón
        backButton.setOnClickListener {
            val intent = Intent(this, Conversores::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Función para inicializar la interfaz de usuario
    private fun initUI() {
        // Asignamos los elementos de la interfaz a las variables
        unitSpinner1 = findViewById(R.id.unitSpinner1)
        unitSpinner2 = findViewById(R.id.unitSpinner2)
        inputEditText = findViewById(R.id.inputEditText)
        outputEditText = findViewById(R.id.outputEditText)

        // Asignamos los elementos de la interfaz a las variables
        cleanAllButton = findViewById(R.id.cleanAllButton)
        deleteButton = findViewById(R.id.deleteButton)
        divideButton = findViewById(R.id.divideButton)
        multiplyButton = findViewById(R.id.multiplyButton)
        minusButton = findViewById(R.id.minusButton)
        plusButton = findViewById(R.id.plusButton)
        equalsButton = findViewById(R.id.equalsButton)
        dotButton = findViewById(R.id.dotButton)
        number00Button = findViewById(R.id.number00)
        percentButton = findViewById(R.id.percentButton)

        // Asignamos los elementos de la interfaz a las variables
        numberButtons = listOf(
            findViewById(R.id.number0),
            findViewById(R.id.number1),
            findViewById(R.id.number2),
            findViewById(R.id.number3),
            findViewById(R.id.number4),
            findViewById(R.id.number5),
            findViewById(R.id.number6),
            findViewById(R.id.number7),
            findViewById(R.id.number8),
            findViewById(R.id.number9)
        )

        // Limitamos el número de caracteres y los caracteres permitidos en el inputEditText
        inputEditText.filters = arrayOf(
            InputFilter.LengthFilter(20),
            InputFilter { source, start, end, dest, dstart, dend ->
                // Solo permitimos los caracteres 0-9, +, -, x, /, .
                val allowedChars = "0123456789+-x*/."
                // Recorremos los caracteres de la fuente
                for (i in start until end) {
                    // Si el caracter no está permitido, devolvemos una cadena vacía
                    if (!allowedChars.contains(source[i])) {
                        return@InputFilter ""
                    }
                }
                null
            }
        )

        inputEditText.isFocusable = true
    }

    // Función para configurar el conversor
    private fun setupConverter() {
        // Creamos un ArrayAdapter a partir de un recurso de cadenas
        ArrayAdapter.createFromResource(
            this,
            R.array.velocity_units,
            android.R.layout.simple_spinner_item
        ).also { adapter -> // Especificamos el diseño a usar cuando se despliega la lista
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            unitSpinner1.adapter = adapter
            unitSpinner2.adapter = adapter
        }

        // Establecemos los índices iniciales de los spinners
        unitSpinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // Función que se ejecuta al seleccionar un elemento
            override fun onItemSelected(
                // Parámetros: AdapterView, View, posición, id
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                performConversion(false)
            }

            // Función que se ejecuta al no seleccionar ningún elemento
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Función que se ejecuta al seleccionar un elemento
        unitSpinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // Parámetros: AdapterView, View, posición, id
            override fun onItemSelected(
                // Parámetros: AdapterView, View, posición, id
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                performConversion(false)
            }

            // Función que se ejecuta al no seleccionar ningún elemento
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Función que se ejecuta al cambiar el texto
        inputEditText.addTextChangedListener(object : TextWatcher {
            // Función que se ejecuta después de cambiar el texto
            override fun afterTextChanged(s: Editable?) {
                performConversion(false)
            }

            // Funciones que se ejecutan antes y durante el cambio del texto
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    // Función para configurar la calculadora
    private fun setupCalculator() {
        numberButtons.forEach { button ->
            // Función que se ejecuta al hacer clic en un botón
            button.setOnClickListener {
                val number = button.text.toString()
                appendNumber(number)
            }
        }

        // Función que se ejecuta al hacer clic en un botón
        number00Button.setOnClickListener { appendNumber("00") }

        divideButton.setOnClickListener { appendOperator("/") }
        multiplyButton.setOnClickListener { appendOperator("x") }
        minusButton.setOnClickListener { appendOperator("-") }
        plusButton.setOnClickListener { appendOperator("+") }

        cleanAllButton.setOnClickListener { clearAll() }
        deleteButton.setOnClickListener { deleteLast() }
        equalsButton.setOnClickListener { performConversion(true) }
        dotButton.setOnClickListener { appendDot() }
    }

    // Función para añadir un número
    private fun appendNumber(number: String) {
        // Si es una nueva conversión, limpiamos el inputEditText
        if (isNewCalculation) {
            // Limpiamos el inputEditText
            inputEditText.setText("")
            isNewCalculation = false
        }
        // Añadimos el número al inputEditText
        inputEditText.append(number)
    }

    // Función para añadir un punto
    private fun appendDot() {
        // Si es una nueva conversión, limpiamos el inputEditText
        if (isNewCalculation) {
            inputEditText.setText("0")
            isNewCalculation = false
        }
        // Obtenemos el texto actual
        val currentText = inputEditText.text.toString()
        val lastNumber = currentText.split(Regex("[+-x*/]")).last()
        // Si el último número no contiene un punto, añadimos un punto
        if (!lastNumber.contains(".")) {
            inputEditText.append(".")
        }
    }

    // Función para añadir un operador
    private fun appendOperator(operator: String) {
        // Si es una nueva conversión, no hacemos nada
        val currentText = inputEditText.text.toString()
        if (currentText.isEmpty()) return

        // Obtenemos el último caracter del texto actual
        val lastChar = currentText.last()
        // Si el último caracter es un operador, lo reemplazamos
        if ("+-x*/".contains(lastChar)) {
            // Reemplazamos el último caracter por el operador
            inputEditText.setText(currentText.dropLast(1) + operator)
        } else {
            // Añadimos el operador al inputEditText
            inputEditText.append(operator)
        }
        // No es una nueva conversión
        isNewCalculation = false
    }

    // Función para realizar la conversión
    private fun performConversion(finalize: Boolean) {
        // Obtenemos el texto de entrada
        val inputText = inputEditText.text.toString()
        if (inputText.isEmpty()) {
            outputEditText.setText("")
            return
        }

        // Aquí simplemente mostramos el texto de entrada en el outputEditText
        outputEditText.setText(inputText)

        // Obtenemos las unidades de los spinners
        val fromUnit = unitSpinner1.selectedItem.toString()
        val toUnit = unitSpinner2.selectedItem.toString()

        val fromFactor = conversionMap[fromUnit] ?: 1.0
        val toFactor = conversionMap[toUnit] ?: 1.0

        // Si el texto de entrada no es un número válido, no hacemos nada
        val valueInBase = inputText.toDoubleOrNull() ?: return
        val convertedValue = valueInBase * fromFactor / toFactor

        val formattedValue = formatForDisplay(convertedValue)

        // Mostramos el valor convertido en el outputEditText
        outputEditText.setText(formattedValue)

        // Si finalize es verdadero, es una nueva conversión
        if (finalize) {
            isNewCalculation = true
        }
    }

    // Función para dar formato a un número
    private fun formatForDisplay(number: Double): String {
        // Obtenemos el formato de número
        val nf = NumberFormat.getInstance(locale) as DecimalFormat
        val absNumber = kotlin.math.abs(number)
        // Si el número es muy pequeño o muy grande, usamos notación científica
        return if (absNumber != 0.0 && (absNumber < 1e-4 || absNumber > 1e9)) {
            // Formateamos el número en notación científica
            val decimalSymbols = DecimalFormatSymbols(locale)
            val sciFormat = DecimalFormat("0.#####E0", decimalSymbols)
            sciFormat.format(number)
        } // Si no, formateamos el número normalmente
        else {
            nf.maximumFractionDigits = 15
            nf.minimumFractionDigits = 0
            nf.format(number)
        }
    }

    // Función para limpiar todo
    private fun clearAll() {
        inputEditText.setText("")
        outputEditText.setText("")
        isNewCalculation = true
    }

    // Función para borrar el último caracter
    private fun deleteLast() {
        // Obtenemos el texto actual
        val currentText = inputEditText.text.toString()
        // Si el texto no está vacío, borramos el último caracter
        if (currentText.isNotEmpty()) {
            // Borramos el último caracter
            inputEditText.setText(currentText.dropLast(1))
            // Si el último caracter es un operador, no es una nueva conversión
            if (currentText.last() in "+-x*/") {
                isNewCalculation = false
            }
        }
    }
}
