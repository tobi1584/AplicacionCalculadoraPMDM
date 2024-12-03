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

class Datos : AppCompatActivity() {

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

    private val conversionMap = mapOf(
        "Byte B" to 1.0,
        "Kilobyte KB" to 1_000.0,
        "Megabyte MB" to 1_000_000.0,
        "Gigabyte GB" to 1_000_000_000.0,
        "Terabyte TB" to 1_000_000_000_000.0,
        "Petabyte PB" to 1_000_000_000_000_000.0
    )

    private var isNewCalculation: Boolean = true

    private val locale = Locale("es", "ES")

    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.datos)
        initUI()
        setupConverter()
        setupCalculator()
        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, Conversores::class.java)
            startActivity(intent)
            finish()
        }
    }

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

        inputEditText.filters = arrayOf(
            InputFilter.LengthFilter(20),
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

        inputEditText.isFocusable = true
    }

    private fun setupConverter() {
        ArrayAdapter.createFromResource(
            this,
            R.array.data_units,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            unitSpinner1.adapter = adapter
            unitSpinner2.adapter = adapter
        }

        unitSpinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                performConversion(false)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        unitSpinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                performConversion(false)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                performConversion(false)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupCalculator() {
        numberButtons.forEach { button ->
            button.setOnClickListener {
                val number = button.text.toString()
                appendNumber(number)
            }
        }

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

    private fun appendNumber(number: String) {
        if (isNewCalculation) {
            inputEditText.setText("")
            isNewCalculation = false
        }
        inputEditText.append(number)
    }

    private fun appendDot() {
        if (isNewCalculation) {
            inputEditText.setText("0")
            isNewCalculation = false
        }
        val currentText = inputEditText.text.toString()
        val lastNumber = currentText.split(Regex("[+-x*/]")).last()
        if (!lastNumber.contains(".")) {
            inputEditText.append(".")
        }
    }

    private fun appendOperator(operator: String) {
        val currentText = inputEditText.text.toString()
        if (currentText.isEmpty()) return

        val lastChar = currentText.last()
        if ("+-x*/".contains(lastChar)) {
            inputEditText.setText(currentText.dropLast(1) + operator)
        } else {
            inputEditText.append(operator)
        }
        isNewCalculation = false
    }

    private fun performConversion(finalize: Boolean) {
        val inputText = inputEditText.text.toString()
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

        val valueInBase = inputText.toDoubleOrNull() ?: return
        val convertedValue = valueInBase * fromFactor / toFactor

        val formattedValue = formatForDisplay(convertedValue)

        outputEditText.setText(formattedValue)

        if (finalize) {
            isNewCalculation = true
        }
    }

    private fun formatForDisplay(number: Double): String {
        val nf = NumberFormat.getInstance(locale) as DecimalFormat
        val absNumber = kotlin.math.abs(number)
        return if (absNumber != 0.0 && (absNumber < 1e-4 || absNumber > 1e9)) {
            val decimalSymbols = DecimalFormatSymbols(locale)
            val sciFormat = DecimalFormat("0.#####E0", decimalSymbols)
            sciFormat.format(number)
        } else {
            nf.maximumFractionDigits = 15
            nf.minimumFractionDigits = 0
            nf.format(number)
        }
    }

    private fun clearAll() {
        inputEditText.setText("")
        outputEditText.setText("")
        isNewCalculation = true
    }

    private fun deleteLast() {
        val currentText = inputEditText.text.toString()
        if (currentText.isNotEmpty()) {
            inputEditText.setText(currentText.dropLast(1))
            if (currentText.last() in "+-x*/") {
                isNewCalculation = false
            }
        }
    }
}
