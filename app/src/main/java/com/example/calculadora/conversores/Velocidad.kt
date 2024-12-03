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
import net.objecthunter.exp4j.ExpressionBuilder
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

class Velocidad : AppCompatActivity() {

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

    private val conversionMap = mapOf(
        "Kilómetro por hora km/h" to 1000.0 / 3600.0,
        "Metro por segundo m/s" to 1.0,
        "Milla por hora mph" to 1609.344 / 3600.0,
        "Milla náutica nmi" to 1852.0 / 3600.0,
        "Pie por segundo ft/s" to 0.3048,
        "Yarda por segundo yd/s" to 0.9144
    )

    private var isNewCalculation: Boolean = true

    private val locale = Locale("es", "ES")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.velocidad)
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
            R.array.velocity_units,
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

        val sanitizedInput = inputText.replace("x", "*")

        val inputValue = try {
            ExpressionBuilder(sanitizedInput).build().evaluate()
        } catch (e: ArithmeticException) {
            outputEditText.setText("Error: División por 0")
            return
        } catch (e: Exception) {
            outputEditText.setText("Input no válido")
            return
        }

        val fromUnit = unitSpinner1.selectedItem.toString()
        val toUnit = unitSpinner2.selectedItem.toString()

        val fromFactor = conversionMap[fromUnit] ?: 1.0
        val toFactor = conversionMap[toUnit] ?: 1.0

        val valueInBase = inputValue * fromFactor
        val convertedValue = valueInBase / toFactor

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
