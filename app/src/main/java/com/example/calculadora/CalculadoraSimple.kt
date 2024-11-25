package com.example.calculadora

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.calculadora.databinding.CalculadoraSimpleBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalculadoraSimple : AppCompatActivity() {

    private lateinit var binding: CalculadoraSimpleBinding
    private val lista = ArrayList<String>()
    private lateinit var myButtons: Map<Button, String>
    // nueva configuración para base de datos
    private lateinit var dbHelper: SQLite



    // Ciclo de vida: Inicialización
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CalculadoraSimpleBinding.inflate(layoutInflater)
        dbHelper = SQLite(this, "CalculadoraDB", null, 1)
        setContentView(binding.root)
        initComponent()
        initListeners()
    }

    private fun initComponent(){
        myButtons = mapOf(
            binding.number0 to "0",
            binding.number1 to "1",
            binding.number2 to "2",
            binding.number3 to "3",
            binding.number4 to "4",
            binding.number5 to "5",
            binding.number6 to "6",
            binding.number7 to "7",
            binding.number8 to "8",
            binding.number9 to "9",
            binding.plusButton to "+",
            binding.minusButton to "-",
            binding.multiplyButton to "x",
            binding.divideButton to "/",
            binding.dotButton to "."
        )
    }

    // Configuración de listeners
    private fun initListeners() {
        myButtons.forEach { (button, value) ->
            button.setOnClickListener {
                lista.add(value)
                binding.mainEditText.append(value)
            }
        }

        binding.equalsButton.setOnClickListener {
            resultado()
        }

        binding.cleanAllButton.setOnClickListener {
            lista.clear()
            binding.mainEditText.setText("")
        }

        binding.deleteButton.setOnClickListener {
            deleteLast()
        }

        binding.percentButton.setOnClickListener {
            calculatePercentage()
        }

        binding.switchComplexButton.setOnClickListener {
            val intent = Intent(this, CalculadoraCompleja::class.java)
            startActivity(intent)
        }
    }

    // Métodos de interacción del usuario
    private fun resultado() {
        if (lista.isEmpty() || lista.last() in setOf("+", "-", "x", "/")) {
            Toast.makeText(this, "Se ha producido un error", Toast.LENGTH_LONG).show()
            return
        }

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("operacion", binding.mainEditText.text.toString())
            put("fecha", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
        }
        db.insert("historial", null, values)
        db.close()

        val resultado = calculo(lista)
        lista.clear()
        lista.add(resultado.toString())
        binding.mainEditText.setText(resultado.toString())
    }

    private fun deleteLast() {
        if (lista.isNotEmpty()) {
            lista.removeAt(lista.size - 1)
            binding.mainEditText.setText(lista.joinToString(""))
        }
    }

    private fun calculatePercentage() {
        if (lista.isNotEmpty() && lista.last().toDoubleOrNull() != null) {
            val number = lista.joinToString("").toDouble()
            val percentage = number / 100
            lista.clear()
            lista.add(percentage.toString())
            binding.mainEditText.setText(percentage.toString())
        } else {
            Toast.makeText(this, "No se puede calcular el porcentaje", Toast.LENGTH_LONG).show()
        }
    }

    // Métodos de lógica principal del cálculo
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
