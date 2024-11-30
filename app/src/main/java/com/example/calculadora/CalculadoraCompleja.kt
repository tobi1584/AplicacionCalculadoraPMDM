package com.example.calculadora

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.calculadora.databinding.CalculadoraComplejaBinding
import kotlin.math.pow
import kotlin.math.sqrt

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
            binding.piButton to "\u03C0",
            binding.eulerButton to "e",
            binding.parentOpenButton to "(",
            binding.parentCloseButton to ")",
            binding.senButton to "sen(",
            binding.cosButton to "cos(",
            binding.tanButton to "tan("
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
                        } else if (value == "e") {
                            binding.editText.setText("e")
                            lista.add(Math.E.toString())
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
                    } else if (value == "e") {
                        val eValue = Math.E
                        lista.add(eValue.toString())
                        binding.editText.append("e")
                    } else if (value == ".") {
                        if (lista.isEmpty() || lista.last() == ".") {
                            Toast.makeText(this, "Operación inválida", Toast.LENGTH_SHORT).show()
                        } else {
                            lista.add(value)
                            binding.editText.append(value)
                        }
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

        binding.factorialButton.setOnClickListener {
            val texto = binding.editText.text;
            calcularFactorial(texto)
        }

        binding.funcReciButton.setOnClickListener {
            val num = encontrarNumeros()
            if (num != null && num.isNotEmpty() && num.none { it in setOf('+', '-', 'x', '/') }) {
                anadirFraccion(num)
            } else {
                Toast.makeText(this, "Error: Entrada inválida", Toast.LENGTH_LONG).show()
            }
        }

        binding.squareRootButton.setOnClickListener{
            val num = encontrarNumeros()
            if (num != null && num.isNotEmpty() && num.none { it in setOf('+', '-', 'x', '/') }) {
                anadirRaiz(num)
            } else {
                Toast.makeText(this, "Error: Entrada inválida", Toast.LENGTH_LONG).show()
            }
        }

        binding.xRaisedYButton.setOnClickListener {
            if (lista.isNotEmpty() && lista.last().toDoubleOrNull() != null) {
                // Si el último elemento es un número, agregamos el operador "^"
                lista.add("^")
                binding.editText.append("^")
            } else {
                Toast.makeText(this, "Primero debes ingresar un número base", Toast.LENGTH_LONG).show()
            }
        }

        binding.senButton.setOnClickListener {
            val num = encontrarNumeros()
            // Verificar si el número es válido
            if (num != null && num.isNotEmpty() && num.none { it in setOf('+', '-', 'x', '/') }) {
                anadirSenoRad(num)
            } else {
                Toast.makeText(this, "Error: Entrada inválida", Toast.LENGTH_LONG).show()
            }
        }

        binding.cosButton.setOnClickListener {
            val num = encontrarNumeros()
            // Verificar si el número es válido
            if (num != null && num.isNotEmpty() && num.none { it in setOf('+', '-', 'x', '/') }) {
                anadirCosenoRad(num)
            } else {
                Toast.makeText(this, "Error: Entrada inválida", Toast.LENGTH_LONG).show()
            }
        }

        binding.tanButton.setOnClickListener {
            val num = encontrarNumeros()
            // Verificar si el número es válido
            if (num != null && num.isNotEmpty() && num.none { it in setOf('+', '-', 'x', '/') }) {
                anadirTangenteRad(num)
            } else {
                Toast.makeText(this, "Error: Entrada inválida", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun anadirSenoRad(num: String) {
        try {
            // Convertir el número a Double
            val num2 = num.toDouble()
            val formattedNum2 = String.format("%.1f", num2).toDouble() // Redondear a 1 decimal

            // Guardar el número original
            val num3 = num

            // Calcular el seno
            val resultado = Math.sin(Math.toRadians(formattedNum2))
            val formatResultado = String.format("%.3f", resultado)

            // Limpiar la lista y agregar el resultado
            lista.clear()
            lista.add(formatResultado)

            // Mostrar el resultado en el EditText
            binding.editText.setText("")
            binding.editText.append("sen(")
            binding.editText.append(num3)
            binding.editText.append("º)")
        } catch (e: NumberFormatException) { // Capturar errores de conversión
            Toast.makeText(this, "Error: No se pudo calcular el seno", Toast.LENGTH_LONG).show()
        }
    }

    private fun anadirCosenoRad(num: String) {
        try {
            // Convertir el número a Double
            val num2 = num.toDouble()
            val formattedNum2 = String.format("%.1f", num2).toDouble()

            // Guardar el número original
            val num3 = num

            // Calcular el coseno
            val resultado = Math.cos(Math.toRadians(formattedNum2))
            val formatResultado = String.format("%.3f", resultado)

            // Limpiar la lista y agregar el resultado
            lista.clear()
            lista.add(formatResultado)

            // Mostrar el resultado en el EditText
            binding.editText.setText("")
            binding.editText.append("cos(")
            binding.editText.append(num3)
            binding.editText.append("º)")
        } catch (e: NumberFormatException) { // Capturar errores de conversión
            Toast.makeText(this, "Error: No se pudo calcular el coseno", Toast.LENGTH_LONG).show()
        }
    }

    private fun anadirTangenteRad(num: String) {
        try {
            // Convertir el número a Double
            val num2 = num.toDouble()
            val formattedNum2 = String.format("%.1f", num2).toDouble()

            // Guardar el número original
            val num3 = num

            // Calcular la tangente
            val resultado = Math.tan(Math.toRadians(formattedNum2))
            val formatResultado = String.format("%.3f", resultado)

            // Limpiar la lista y agregar el resultado
            lista.clear()
            lista.add(formatResultado)

            // Mostrar el resultado en el EditText
            binding.editText.setText("")
            binding.editText.append("tan(")
            binding.editText.append(num3)
            binding.editText.append("º)")
        } catch (e: NumberFormatException) { // Capturar errores de conversión
            Toast.makeText(this, "Error: No se pudo calcular la tangente", Toast.LENGTH_LONG).show()
        }
    }


    private fun anadirRaiz(num: String) {
        val num2 = num
        val resultado = sqrt(num2.toDouble())

        lista.clear()
        lista.add(resultado.toString())

        binding.editText.setText("")
        // Mostrar el resultado en el EditText
        binding.editText.append(resultado.toString())
    }


    private fun encontrarNumeros(): String? {
        val simbols = setOf("+", "-", "x", "/", "^")
        var num = ""

        // Verificar si la lista es nula o vacía
        if (lista.isNullOrEmpty()) {
            Toast.makeText(this, "Error: Lista vacía o nula", Toast.LENGTH_LONG).show()
            return null
        }

        // Buscar los números desde el final de la lista
        for (i in lista.asReversed()) {
            if (i in simbols || i.isNullOrBlank()) {
                break
            } else {
                num += i
            }
        }

        // Validar si se encontró algún número
        return num.reversed().ifEmpty {
            null
        }
    }

    private fun anadirFraccion(num: String) {
        try {
            // Revertimos el número para obtener el orden original
            val num2 = num

            // Validar que el número sea convertible a Double
            val resultado = 1 / num2.toDouble()

            // Limpiar la lista y agregar el resultado
            lista.clear()
            lista.add(resultado.toString())

            // Mostrar el resultado en el EditText
            binding.editText.append("^(-1)")
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Error: No se pudo calcular el inverso", Toast.LENGTH_LONG).show()
        }
    }



    private fun calcularFactorial(texto: Editable) {
        val num = texto.toString().trim().toIntOrNull()
        if (num != null && num > 0) {
            val fact = factorial(num)
            lista.clear()
            lista.add(fact.toString())
            binding.editText.setText(fact.toString())
        } else {
            Toast.makeText(this, "Entrada no válida", Toast.LENGTH_LONG).show()
            lista.clear()
            binding.editText.setText("")
        }
    }

    private fun factorial(n: Int): Int {
        return if (n == 0) 1 else n * factorial(n - 1)
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
        if (!areParenthesesBalanced(lista)) {
            Toast.makeText(this, "Paréntesis no balanceados", Toast.LENGTH_LONG).show()
            throw IllegalArgumentException("Paréntesis no balanceados")
        }

        // Resolver paréntesis primero
        while (lista.contains("(")) {
            val openIndex = lista.lastIndexOf("(")
            val closeIndex = lista.subList(openIndex, lista.size).indexOf(")") + openIndex
            if (closeIndex == -1) {
                Toast.makeText(this, "Paréntesis no balanceados", Toast.LENGTH_LONG).show()
                throw IllegalArgumentException("Paréntesis no balanceados")
            }

            // Extraer la subexpresión
            val subExpresion = ArrayList(lista.subList(openIndex + 1, closeIndex))
            val subResultado = calculo(subExpresion) // Resolver recursivamente

            // Reemplazar la subexpresión con su resultado
            for (i in openIndex..closeIndex) {
                lista.removeAt(openIndex)
            }
            lista.add(openIndex, subResultado.toString())
        }

        // Continuar con el cálculo normal
        val operators = setOf("+", "-", "x", "/", "^")
        val numbers = mutableListOf<Double>()
        val operations = mutableListOf<String>()

        parseInput(lista, operators, numbers, operations)
        validateExpression(numbers, operations)

        // Procesar potencias primero
        performOperations(numbers, operations, setOf("^"))
        // Luego multiplicación y división
        performOperations(numbers, operations, setOf("x", "/"))
        // Finalmente suma y resta
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
        var isNegative = false

        for (item in lista) {
            if (item in operators) {
                if (item == "-" && (number.isEmpty() || operations.isNotEmpty() && operations.last() in operators)) {
                    isNegative = true
                } else {
                    if (number.isNotEmpty()) {
                        numbers.add(if (isNegative) -number.toDouble() else number.toDouble())
                        number = ""
                        isNegative = false
                    }
                    operations.add(item)
                }
            } else {
                number += item
            }
        }

        if (number.isNotEmpty()) {
            numbers.add(if (isNegative) -number.toDouble() else number.toDouble())
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
                    "^" -> numbers[i].pow(numbers[i + 1])
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

    private fun areParenthesesBalanced(lista: ArrayList<String>): Boolean {
        var balance = 0
        for (item in lista) {
            when (item) {
                "(" -> balance++
                ")" -> balance--
            }
            if (balance < 0) return false // Más paréntesis de cierre que de apertura
        }
        return balance == 0
    }



}