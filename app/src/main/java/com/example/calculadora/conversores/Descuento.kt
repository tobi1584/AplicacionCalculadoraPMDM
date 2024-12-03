package com.example.calculadora.conversores

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.calculadora.R
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.absoluteValue

class Descuento : AppCompatActivity() {

    private lateinit var precioOriginalEditText: EditText
    private lateinit var descuentoEditText: EditText
    private lateinit var precioFinalTextView: TextView

    private lateinit var numberButtons: List<Button>
    private lateinit var clearButton: Button
    private lateinit var deleteButton: Button

    private var currentEditText: EditText? = null

    private var precioOriginalValue: StringBuilder = StringBuilder()
    private var descuentoValue: StringBuilder = StringBuilder()

    private val scientificFormatter = DecimalFormat("0.##E0")

    private val MAX_DIGITS = 15

    private val locale = Locale("es", "ES")

    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.descuentos)

        precioOriginalEditText = findViewById(R.id.mainEditText)
        descuentoEditText = findViewById(R.id.mainEditText4)
        precioFinalTextView = findViewById(R.id.mainEditText6)

        backButton = findViewById(R.id.backButton)

        precioOriginalValue.append("0")
        descuentoValue.append("0")

        updateEditTextDisplay(precioOriginalEditText, precioOriginalValue.toString())
        updateEditTextDisplay(descuentoEditText, descuentoValue.toString())

        precioFinalTextView.text = formatNumber(0.0)

        precioOriginalEditText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(MAX_DIGITS))
        descuentoEditText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(5))

        precioOriginalEditText.keyListener = null
        descuentoEditText.keyListener = null

        numberButtons = listOf(
            findViewById(R.id.dotButton),
            findViewById(R.id.number00),
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

        clearButton = findViewById(R.id.cleanAllButton)
        deleteButton = findViewById(R.id.deleteButton)

        setEditTextListeners()
        setNumberButtonsListeners()
        setActionButtonsListeners()
        disableKeyboard()

        backButton.setOnClickListener {
            val intent = Intent(this, Conversores::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setEditTextListeners() {
        val editTexts = listOf(precioOriginalEditText, descuentoEditText)
        for (editText in editTexts) {
            editText.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    currentEditText = v as EditText
                }
            }
            editText.setOnClickListener {
                currentEditText = it as EditText
            }
        }
    }

    private fun setNumberButtonsListeners() {
        for (button in numberButtons) {
            button.setOnClickListener {
                val input = button.text.toString()
                insertNumber(input)
            }
        }
    }

    private fun insertNumber(number: String) {
        currentEditText?.let { editText ->
            val rawValue: StringBuilder = when (editText.id) {
                R.id.mainEditText -> precioOriginalValue
                R.id.mainEditText4 -> descuentoValue
                else -> StringBuilder()
            }

            if (rawValue.toString() == "0" && number == "0" && !rawValue.contains(".")) {
                return
            }

            if (rawValue.toString() == "0" && number != "0" && number != "." && !rawValue.contains(".")) {
                rawValue.clear()
            }

            if (editText.id == R.id.mainEditText4) {
                if (number == "00") {
                    return
                }
                when (number) {
                    "." -> {
                        if (!rawValue.contains(".")) {
                            if (rawValue.isEmpty() || rawValue.toString() == "-") {
                                rawValue.append("0")
                            }
                            rawValue.append(".")
                        }
                    }
                    else -> {
                        if (rawValue.contains(".")) {
                            val decimalPart = rawValue.toString().substringAfter(".")
                            if (decimalPart.length >= 2) {
                                return
                            }
                        }
                        rawValue.append(number)
                    }
                }

                val descuentoDouble = rawValue.toString().toDoubleOrNull()
                if (descuentoDouble != null && descuentoDouble > 99.99) {
                    rawValue.setLength(rawValue.length - number.length)
                }
            } else {
                rawValue.append(number)
            }

            val maxDigits = if (editText.id == R.id.mainEditText4) 5 else MAX_DIGITS

            if (rawValue.length > maxDigits) {
                rawValue.setLength(maxDigits)
            }

            updateEditTextDisplay(editText, rawValue.toString())
            calculatePrecioFinal()
        }
    }

    private fun setActionButtonsListeners() {
        clearButton.setOnClickListener {
            currentEditText?.let { editText ->
                when (editText.id) {
                    R.id.mainEditText -> precioOriginalValue.clear().append("0")
                    R.id.mainEditText4 -> descuentoValue.clear().append("0")
                }
                updateEditTextDisplay(editText, when (editText.id) {
                    R.id.mainEditText -> precioOriginalValue.toString()
                    R.id.mainEditText4 -> descuentoValue.toString()
                    else -> ""
                })
                calculatePrecioFinal()
            }
        }

        deleteButton.setOnClickListener {
            currentEditText?.let { editText ->
                val rawValue: StringBuilder = when (editText.id) {
                    R.id.mainEditText -> precioOriginalValue
                    R.id.mainEditText4 -> descuentoValue
                    else -> StringBuilder()
                }

                if (rawValue.isNotEmpty()) {
                    rawValue.deleteCharAt(rawValue.length - 1)
                    if (rawValue.isEmpty()) {
                        rawValue.append("0")
                    }
                    updateEditTextDisplay(editText, rawValue.toString())
                    calculatePrecioFinal()
                }
            }
        }
    }

    private fun disableKeyboard() {
        precioOriginalEditText.apply {
            showSoftInputOnFocus = false
            isFocusable = true
        }

        descuentoEditText.apply {
            showSoftInputOnFocus = false
            isFocusable = true
        }

        precioFinalTextView.apply {}
    }

    private fun calculatePrecioFinal() {
        val precioOriginal = precioOriginalValue.toString().toDoubleOrNull()
        val descuento = descuentoValue.toString().toDoubleOrNull()

        if (precioOriginal != null && descuento != null) {
            val descuentoAmount = precioOriginal * (descuento / 100)
            val precioFinal = precioOriginal - descuentoAmount

            val formattedPrecioFinal = formatNumber(precioFinal)

            precioFinalTextView.text = formattedPrecioFinal
        } else {
            precioFinalTextView.text = formatNumber(0.0)
        }
    }

    private fun updateEditTextDisplay(editText: EditText, rawValue: String) {
        if (rawValue.isEmpty()) {
            editText.text = android.text.SpannableStringBuilder("0")
            return
        }

        val number = rawValue.toDoubleOrNull()
        if (number != null) {
            if (editText.id == R.id.mainEditText && rawValue.length > 9) {
                editText.text = android.text.SpannableStringBuilder(scientificFormatter.format(number))
            } else {
                val nf = NumberFormat.getInstance(locale) as DecimalFormat
                nf.isGroupingUsed = true
                nf.maximumFractionDigits = if (editText.id == R.id.mainEditText4) 2 else 0
                nf.minimumFractionDigits = if (editText.id == R.id.mainEditText4) 0 else 0
                editText.text = android.text.SpannableStringBuilder(nf.format(number))
            }
        } else {
            editText.text = android.text.SpannableStringBuilder(rawValue)
        }
    }

    private fun formatNumber(number: Double, threshold: Double = 1e5): String {
        return if (number.absoluteValue >= threshold || (number.absoluteValue > 0 && number.absoluteValue < 1e-2)) {
            scientificFormatter.format(number)
        } else {
            val nf = NumberFormat.getInstance(locale) as DecimalFormat
            nf.apply {
                isGroupingUsed = true
                maximumFractionDigits = 2
                minimumFractionDigits = if (number % 1.0 == 0.0) 0 else 2
            }.format(number)
        }
    }
}
