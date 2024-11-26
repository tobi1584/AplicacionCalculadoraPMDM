package com.example.calculadora

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calculadora.databinding.CalculadoraComplejaBinding
import com.example.calculadora.databinding.CalculadoraSimpleBinding
import kotlin.math.roundToInt

class CalculadoraCompleja : AppCompatActivity() {

    private lateinit var binding: CalculadoraComplejaBinding
    private val lista = ArrayList<String>()
    private lateinit var myButtons: Map<Button, String>
    private var resultadoCalculado = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CalculadoraComplejaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponent()
        initListeners()
    }
    private fun initComponent() {
        myButtons = mapOf(
            binding.number0Button to "0",
            binding.number1Button to "1",
            binding.number2Button to "2",
            binding.number3Button to "3",
            binding.number4Button to "4",
            binding.number5Button to "5",
            binding.number6Button to "6",
            binding.number7Button to "7",
            binding.number8Button to "8",
            binding.number9Button to "9",
            binding.plusButton to "+",
            binding.minusButton to "-",
            binding.multiplyButton to "x",
            binding.divideButton to "/",
            binding.dotButton to ".",
            binding.piButton to "\u03C0"
        )
    }

    private fun initListeners() {
        myButtons.forEach { (button, value) ->
            button.setOnClickListener {
                if (resultadoCalculado) {
                    if (value in setOf("+", "-", "x", "/")) {
                        lista.add(value)
                        binding.editText.append(value)
                    } else {
                        lista.clear()
                        if (value == "\u03C0") {
                            binding.editText.setText(Math.PI.toString())
                            lista.add(Math.PI.toString())
                        } else {
                            binding.editText.setText(value)
                            lista.add(value)
                        }
                    }
                    resultadoCalculado = false
                } else {
                    if (value == "\u03C0") {
                        val piValue = Math.PI
                        lista.add(piValue.toString())
                        binding.editText.append("\u03C0")
                    } else {
                        lista.add(value)
                        binding.editText.append(value)
                    }
                }
            }
        }

        binding.equalsButton.setOnClickListener {
            resultado()
        }

        binding.cleanAllButton.setOnClickListener {
            lista.clear()
            binding.editText.setText("")
        }

        binding.deleteButton.setOnClickListener {
            deleteLast()
        }

        binding.percentButton.setOnClickListener {
            calculatePercentage()
        }

        binding.switchSimpleButton.setOnClickListener {
            val intent = Intent(this, CalculadoraSimple::class.java)
            startActivity(intent)
        }
    }

    private fun resultado() {
        if (lista.isEmpty() || lista.last() in setOf("+", "-", "x", "/")) {
            Toast.makeText(this, "Se ha producido un error", Toast.LENGTH_LONG).show()
            return
        }

        val resultado = calculo(lista)
        lista.clear()
        lista.add(resultado.toString())

        val resultadoFinal: String

        if (resultado % 1 == 0.0) {
            resultadoFinal = resultado.toInt().toString()
        } else {
            resultadoFinal = resultado.toString()
        }

        binding.editText.setText(resultadoFinal)
        resultadoCalculado = true
    }

    private fun deleteLast() {
        if (lista.isNotEmpty()) {
            lista.removeAt(lista.size - 1)
            binding.editText.setText(lista.joinToString(""))
        }
    }

    private fun calculatePercentage() {
        if (lista.isNotEmpty() && lista.last().toDoubleOrNull() != null) {
            val number = lista.joinToString("").toDouble()
            val percentage = number / 100
            lista.clear()
            lista.add(percentage.toString())
            binding.editText.setText(percentage.toString())
        } else {
            Toast.makeText(this, "No se puede calcular el porcentaje", Toast.LENGTH_LONG).show()
        }
    }

    private fun calculo(lista: ArrayList<String>): Double {
        val operators = setOf("+", "-", "x", "/")
        val numbers = mutableListOf<Double>()
        val operations = mutableListOf<String>()

        parseInput(lista, operators, numbers, operations)
        validateExpression(numbers, operations)
        performOperations(numbers, operations, setOf("x", "/"))
        performOperations(numbers, operations, setOf("+", "-"))

        return roundIfDivision(numbers[0], lista, operators)
    }

    private fun parseInput(
        lista: ArrayList<String>,
        operators: Set<String>,
        numbers: MutableList<Double>,
        operations: MutableList<String>
    ) {
        var number = ""
        for (item in lista) {
            if (item in operators) {
                if (number.isNotEmpty()) {
                    numbers.add(number.toDouble())
                    number = ""
                }
                operations.add(item)
            } else {
                number += item
            }
        }
        if (number.isNotEmpty()) {
            numbers.add(number.toDouble())
        }
    }

    private fun validateExpression(numbers: List<Double>, operations: List<String>) {
        if (numbers.size - 1 != operations.size) {
            Toast.makeText(this, "La expresión no es válida", Toast.LENGTH_LONG).show()
        }
    }

    private fun performOperations(
        numbers: MutableList<Double>,
        operations: MutableList<String>,
        targetOperations: Set<String>
    ) {
        var i = 0
        while (i < operations.size) {
            if (operations[i] in targetOperations) {
                val result = when (operations[i]) {
                    "x" -> numbers[i] * numbers[i + 1]
                    "/" -> numbers[i] / numbers[i + 1]
                    "+" -> numbers[i] + numbers[i + 1]
                    "-" -> numbers[i] - numbers[i + 1]
                    else -> throw IllegalArgumentException("Operación no válida.")
                }
                numbers[i] = result
                numbers.removeAt(i + 1)
                operations.removeAt(i)
            } else {
                i++
            }
        }
    }

    private fun roundIfDivision(result: Double, lista: ArrayList<String>, operators: Set<String>): Double {
        val lastOperation = lista.lastOrNull { it in operators }
        return if (lastOperation == "/") {
            Math.round(result * 1000.0) / 1000.0
        } else {
            result
        }
    }


}