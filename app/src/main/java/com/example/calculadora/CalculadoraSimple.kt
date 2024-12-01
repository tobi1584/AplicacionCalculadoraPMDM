package com.example.calculadora

import android.R
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import com.example.calculadora.databinding.CalculadoraSimpleBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalculadoraSimple : AppCompatActivity() {

    private lateinit var binding: CalculadoraSimpleBinding
    private val lista = ArrayList<String>()
    private lateinit var myButtons: Map<Button, String>
    private var resultadoCalculado = false
    private lateinit var dbHelper: SQLite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CalculadoraSimpleBinding.inflate(layoutInflater)
        dbHelper = SQLite(this, "CalculadoraDB", null, 1)
        setContentView(binding.root)
        initComponent()
        initListeners()

        // Inicializar la base de datos y realizar una operación de lectura
        val db = dbHelper.readableDatabase
        db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val tableName = cursor.getString(0)
                    println("Table: $tableName")
                } while (cursor.moveToNext())
            }
        }
        db.close()
    }

    private fun initComponent() {
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

    private fun initListeners() {
        myButtons.forEach { (button, value) ->
            button.setOnClickListener {
                if (resultadoCalculado) {
                    if (value in setOf("+", "-", "x", "/")) {
                        lista.add(value)
                        binding.mainEditText.append(value)
                    } else {
                        lista.clear()
                        binding.mainEditText.setText(value)
                        lista.add(value)
                    }
                    resultadoCalculado = false
                } else {
                    if (value == ".") {
                        if (lista.isEmpty() || lista.last() == ".") {
                            Toast.makeText(this, "Operación inválida", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                    }
                    lista.add(value)
                    binding.mainEditText.append(value)
                }
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

        binding.othersImageButton.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.menu.add(0, 1, 0, "Historial")
            popupMenu.menu.add(0, 2, 1, "Magnitudes")
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    1 -> {
                        mostrarHistorial()
                        Toast.makeText(this, "Historial seleccionado", Toast.LENGTH_SHORT).show()
                        true
                    }
                    2 -> {
                        mostrarMagnitudes()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    private fun mostrarMagnitudes() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Magnitudes")

        val scrollView = ScrollView(this)
        val layout = GridLayout(this)
        layout.rowCount = 5
        layout.columnCount = 2

        val constants = listOf(
            Triple("π (Pi)", "\u03C0", Math.PI),
            Triple("e (Número de Euler)", "e", Math.E),
            Triple("√2 (Raíz cuadrada de 2)", "√2", Math.sqrt(2.0)),
            Triple("Φ (Número áureo)", "\u03A6", 1.61803),
            Triple("γ (Constante de Euler-Mascheroni)", "\u03B3", 0.57721),
            Triple("ln(2) (Logaritmo natural de 2)", "ln(2)", Math.log(2.0)),
            Triple("ln(10) (Logaritmo natural de 10)", "ln(10)", Math.log(10.0)),
            Triple("c (Velocidad de la luz en el vacío)", "c", 299792458.0),
            Triple("G (Constante gravitacional)", "G", 6.67430e-11),
            Triple("h (Constante de Planck)", "h", 6.62607e-34)
        )

        lateinit var dialog: AlertDialog // Declarar la variable antes

        for ((name, symbol, value) in constants) {
            val imageView = ImageView(this)
            imageView.setImageResource(getImageResourceByName(symbol))
            val paramsImage = GridLayout.LayoutParams()
            paramsImage.width = 200
            paramsImage.height = 200
            paramsImage.setMargins(50, 70, 0, 0)
            imageView.layoutParams = paramsImage

            val textView = TextView(this)
            textView.text = name
            textView.gravity = Gravity.CENTER
            val paramsText = GridLayout.LayoutParams()
            paramsText.width = GridLayout.LayoutParams.WRAP_CONTENT
            paramsText.height = GridLayout.LayoutParams.WRAP_CONTENT
            paramsText.setMargins(50, 100, 0, 0)
            textView.layoutParams = paramsText

            val clickListener = {
                binding.mainEditText.append(symbol)
                lista.add(value.toString())
                dialog.dismiss() // Cerrar el diálogo
            }

            imageView.setOnClickListener { clickListener() }
            textView.setOnClickListener { clickListener() }

            layout.addView(imageView)
            layout.addView(textView)
        }

        scrollView.addView(layout)
        builder.setView(scrollView)

        dialog = builder.create() // Crear el diálogo aquí
        dialog.show()
    }



    private fun getImageResourceByName(name: String): Int {
        return when (name) {
            "\u03C0" -> com.example.calculadora.R.drawable.pi_image
            "e" -> com.example.calculadora.R.drawable.e_image
            "√2" -> com.example.calculadora.R.drawable.sqrt2_image
            "\u03A6" -> com.example.calculadora.R.drawable.phi_image
            "\u03B3" -> com.example.calculadora.R.drawable.gamma_image
            "ln(2)" -> com.example.calculadora.R.drawable.ln2_image
            "ln(10)" -> com.example.calculadora.R.drawable.ln10_image
            "c" -> com.example.calculadora.R.drawable.c_image
            "G" -> com.example.calculadora.R.drawable.g_image
            "h" -> com.example.calculadora.R.drawable.h_image
            else -> 0
        }
    }
    private fun mostrarHistorial() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM historial", null)
        val registros = ArrayList<String>()

        if (cursor.moveToFirst()) {
            do {
                val operacion = cursor.getString(cursor.getColumnIndexOrThrow("operacion"))
                val resultado = cursor.getString(cursor.getColumnIndexOrThrow("resultado"))
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"))
                registros.add("\n$fecha \n $operacion = $resultado \n")
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Historial")

        if (registros.isEmpty()) {
            val textView = TextView(this)
            textView.text = "No hay registros todavía"
            textView.textSize = 20f
            textView.gravity = Gravity.CENTER
            textView.height = 200
            builder.setView(textView)
        } else {
            val listView = ListView(this)
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, registros)
            listView.adapter = adapter
            builder.setView(listView)
        }

        builder.setPositiveButton("Cerrar") { dialog, _ -> dialog.dismiss() }
        builder.setNegativeButton("Borrar Historial") { dialog, _ ->
            borrarHistorial()
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun borrarHistorial() {
        val db = dbHelper.writableDatabase
        db.delete("historial", null, null)
        db.close()
        Toast.makeText(this, "Historial borrado", Toast.LENGTH_SHORT).show()
    }

    // Métodos de interacción del usuario
    private fun resultado() {
        if (lista.isEmpty() || lista.last() in setOf("+", "-", "x", "/")) {
            Toast.makeText(this, "Se ha producido un error", Toast.LENGTH_LONG).show()
            return
        }

        val resultado = calculo(lista)

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("operacion", binding.mainEditText.text.toString())
            put("fecha", SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()))
            put("resultado", resultado.toString())
        }
        db.insert("historial", null, values)
        db.close()

        lista.clear()
        lista.add(resultado.toString())

        val resultadoFinal: String

        if (resultado % 1 == 0.0) {
            resultadoFinal = resultado.toInt().toString()
        } else {
            resultadoFinal = resultado.toString()
        }

        binding.mainEditText.setText(resultadoFinal)
        resultadoCalculado = true
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
                    "x" -> numbers[i] * numbers[i + 1]
                    "/" -> {
                        if (numbers[i + 1] == 0.0) {
                            Toast.makeText(this, "Error: División por cero", Toast.LENGTH_LONG).show()
                            return
                        }
                        numbers[i] / numbers[i + 1]
                    }
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
