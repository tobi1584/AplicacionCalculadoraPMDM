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

class Volumen : AppCompatActivity() {

    // Declaración de variables
    private lateinit var unitSpinner1: Spinner
    private lateinit var unitSpinner2: Spinner
    private lateinit var inputEditText: EditText
    private lateinit var outputEditText: EditText

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

    // Mapa de conversión de unidades
    private val conversionMap = mapOf(
        "Metro cúbico m³" to 1.0,
        "Decímetro cúbico dm³" to 0.001,
        "Centímetro cúbico cm³" to 0.000001,
        "Milímetro cúbico mm³" to 0.000000001,
        "Hectolitro hl" to 0.1,
        "Litro l" to 0.001,
        "Decilitro dl" to 0.0001,
        "Centilitro cl" to 0.00001,
        "Mililitro ml" to 0.000001,
        "Pie cúbico ft³" to 0.0283168,
        "Pulgada cúbica in³" to 0.0000163871,
        "Yarda cúbica yd³" to 0.764554858
    )

    // Variable para saber si es una nueva operación
    private var isNewCalculation: Boolean = true

    private val locale = Locale("es", "ES")

    // Función que se ejecuta al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.volumen)
        initUI()
        setupConverter()
        setupCalculator()

        // Botón para regresar a la pantalla de conversores
        backButton = findViewById(R.id.backButton)
        // Listener para el botón de regresar
        backButton.setOnClickListener {
            // Creamos un intent para abrir la actividad de Conversores
            val intent = Intent(this, Conversores::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Función para inicializar los elementos de la interfaz
    private fun initUI() {
        unitSpinner1 = findViewById(R.id.unitSpinner1)
        unitSpinner2 = findViewById(R.id.unitSpinner2)
        inputEditText = findViewById(R.id.inputEditText)
        outputEditText = findViewById(R.id.outputEditText)

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

        // Lista de botones de números
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

        // Filtro para el inputEditText
        inputEditText.filters = arrayOf(
            InputFilter.LengthFilter(20),
            // Filtro para solo permitir números y operadores
            InputFilter { source, start, end, dest, dstart, dend ->
                val allowedChars = "0123456789+-x*/."
                for (i in start until end) {
                    if (!allowedChars.contains(source[i])) {
                        return@InputFilter ""
                    }
                }
                null
            }
        )
        // Hacemos que el inputEditText sea focusable
        inputEditText.isFocusable = true
    }

    // Función para configurar el conversor
    private fun setupConverter() {
        // Creamos un ArrayAdapter para los spinners
        ArrayAdapter.createFromResource(
            this,
            R.array.volume_units,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Especificamos el layout que se usará para desplegar las opciones
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            unitSpinner1.adapter = adapter
            unitSpinner2.adapter = adapter
        }

        // Listener para los spinners
        unitSpinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // Función que se ejecuta al seleccionar un elemento
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                performConversion(false)
            }

            // Función que se ejecuta al no seleccionar nada
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Listener para los spinners
        unitSpinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // Función que se ejecuta al seleccionar un elemento
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) { // Realizamos la conversión
                performConversion(false)
            }

            // Función que se ejecuta al no seleccionar nada
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Listener para el inputEditText
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                performConversion(false)
            }

            // Funciones que no se utilizan
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    // Función para configurar la calculadora
    private fun setupCalculator() {
        // Listener para los botones de números
        numberButtons.forEach { button ->
            button.setOnClickListener {
                val number = button.text.toString()
                appendNumber(number)
            }
        }

        // Listener para el botón de porcentaje
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

    // Función para añadir un número al inputEditText
    private fun appendNumber(number: String) {
        // Si es una nueva operación, limpiamos el inputEditText
        if (isNewCalculation) {
            inputEditText.setText("")
            isNewCalculation = false
        }
        // Añadimos el número al inputEditText
        inputEditText.append(number)
    }

    // Función para añadir un punto al inputEditText
    private fun appendDot() {
        // Si es una nueva operación, limpiamos el inputEditText
        if (isNewCalculation) {
            inputEditText.setText("0")
            isNewCalculation = false
        }
        // Obtenemos el último número
        val currentText = inputEditText.text.toString()
        val lastNumber = currentText.split(Regex("[+-x*/]")).last()

        // Si el último número no contiene un punto, añadimos el punto
        if (!lastNumber.contains(".")) {
            inputEditText.append(".")
        }
    }

    // Función para añadir un operador al inputEditText
    private fun appendOperator(operator: String) {
        // Obtenemos el texto actual del inputEditText
        val currentText = inputEditText.text.toString()
        // Si el texto actual está vacío, no hacemos nada
        if (currentText.isEmpty()) return

        // Obtenemos el último caracter del texto actual
        val lastChar = currentText.last()
        // Si el último caracter es un operador, lo reemplazamos por el nuevo operador
        if ("+-x*/".contains(lastChar)) {
            // Reemplazamos el último caracter por el nuevo operador
            inputEditText.setText(currentText.dropLast(1) + operator)
        } else { // Si no, simplemente añadimos el operador al final
            inputEditText.append(operator)
        }
        // No es una nueva operación
        isNewCalculation = false
    }

    // Función para realizar la conversión
    private fun performConversion(finalize: Boolean) {
        // Obtenemos el texto del inputEditText
        val inputText = inputEditText.text.toString()
        // Si el texto está vacío, limpiamos el outputEditText
        if (inputText.isEmpty()) {
            outputEditText.setText("")
            return
        }

        // Aquí simplemente mostramos el texto de entrada en el outputEditText
        outputEditText.setText(inputText)

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

        // Si finalize es verdadero, es una nueva operación
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
        return if (absNumber != 0.0 && (absNumber < 1e-6 || absNumber > 1e9)) {
            // Formateamos el número en notación científica
            val decimalSymbols = DecimalFormatSymbols(locale)
            val sciFormat = DecimalFormat("0.#####E0", decimalSymbols)
            sciFormat.format(number)
        } else {
            // Formateamos el número con 15 decimales
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
        // Obtenemos el texto actual del inputEditText
        val currentText = inputEditText.text.toString()

        // Si el texto actual no está vacío, borramos el último caracter
        if (currentText.isNotEmpty()) {
            // Borramos el último caracter
            inputEditText.setText(currentText.dropLast(1))
            // Si el último caracter es un operador, no es una nueva operación
            if (currentText.last() in "+-x*/") {
                isNewCalculation = false
            }
        }
    }
}
