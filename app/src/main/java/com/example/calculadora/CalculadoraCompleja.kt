package com.example.calculadora

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.calculadora.conversores.Conversores
import com.example.calculadora.databinding.CalculadoraComplejaBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.pow
import kotlin.math.sqrt

class CalculadoraCompleja : AppCompatActivity() {

    private lateinit var binding: CalculadoraComplejaBinding
    private val lista = ArrayList<String>()
    private lateinit var myButtons: Map<Button, String>
    private var resultadoCalculado = false
    private lateinit var dbHelper: SQLite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CalculadoraComplejaBinding.inflate(layoutInflater)

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
            binding.tanButton to "tan(",
            binding.lgButton to "lg(",
            binding.lnButton to "ln(",
            binding.squareRootButton to "√"
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


        binding.othersImageButton2.setOnClickListener {
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


        binding.secondButton.setOnClickListener {
            val currentText = binding.senButton.text.toString()

            if (currentText == "sin") {
                binding.degButton.isEnabled = false
                binding.degButton.setTextColor(ContextCompat.getColor(this, R.color.gray))
                binding.senButton.text = "sin⁻¹"
                binding.cosButton.text = "cos⁻¹"
                binding.tanButton.text = "tan⁻¹"
            } else {
                binding.degButton.isEnabled = true
                binding.degButton.setTextColor(ContextCompat.getColor(this, R.color.black))
                binding.senButton.text = "sin"
                binding.cosButton.text = "cos"
                binding.tanButton.text = "tan"
            }
        }

        binding.degButton.setOnClickListener {
            val currentText = binding.degButton.text.toString()
            if (currentText == getString(R.string.deg)) {
                binding.secondButton.isEnabled = false
                binding.secondButton.setTextColor(ContextCompat.getColor(this, R.color.gray))
                binding.degButton.text = getString(R.string.rad)
            } else {
                binding.secondButton.isEnabled = true
                binding.secondButton.setTextColor(ContextCompat.getColor(this, R.color.black))
                binding.degButton.text = getString(R.string.deg)
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

        binding.ConversorTextView.setOnClickListener {
            val intent = Intent(this, Conversores::class.java)
            startActivity(intent)
        }

        binding.factorialButton.setOnClickListener {
            val num = encontrarNumeros()
            if (num != null && num.isNotEmpty() && num.none { it in setOf('+', '-', 'x', '/') }) {
                calcularFactorial(num)
            } else {
                Toast.makeText(this, "Error: Entrada inválida", Toast.LENGTH_LONG).show()
            }
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
                anadirSeno(num)
            } else {
                Toast.makeText(this, "Error: Entrada inválida", Toast.LENGTH_LONG).show()
            }
        }

        binding.cosButton.setOnClickListener {
            val num = encontrarNumeros()
            // Verificar si el número es válido
            if (num != null && num.isNotEmpty() && num.none { it in setOf('+', '-', 'x', '/') }) {
                anadirCoseno(num)
            } else {
                Toast.makeText(this, "Error: Entrada inválida", Toast.LENGTH_LONG).show()
            }
        }

        binding.tanButton.setOnClickListener {
            val num = encontrarNumeros()
            // Verificar si el número es válido
            if (num != null && num.isNotEmpty() && num.none { it in setOf('+', '-', 'x', '/') }) {
                anadirTangente(num)
            } else {
                Toast.makeText(this, "Error: Entrada inválida", Toast.LENGTH_LONG).show()
            }
        }

        binding.lgButton.setOnClickListener {
            val num = encontrarNumeros()
            if (num != null && num.isNotEmpty() && num.none { it in setOf('+', '-', 'x', '/') }) {
                anadirLogaritmo(num)
            } else {
                Toast.makeText(this, "Error: Entrada inválida", Toast.LENGTH_LONG).show()
            }
        }

        binding.lnButton.setOnClickListener {
            val num = encontrarNumeros()
            if (num != null && num.isNotEmpty() && num.none { it in setOf('+', '-', 'x', '/') }) {
                anadirLogaritmoNep(num)
            } else {
                Toast.makeText(this, "Error: Entrada inválida", Toast.LENGTH_LONG).show()
            }
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
                val currentText = binding.editText.text.toString()
                if (currentText.toDoubleOrNull() != null) {
                    binding.editText.setText(symbol)
                    lista.clear()
                    lista.add(value.toString())
                } else {
                    binding.editText.append(symbol)
                    lista.add(value.toString())
                }
                dialog.dismiss()
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


    private fun anadirLogaritmo(num: String) {
        try {
            val num2 = num.toDouble()
            val formattedNum2 = String.format("%.1f", num2).toDouble() // Redondear a 1 decimal

            val num3 = num

            val resultado = Math.log10(formattedNum2)
            val formatResultado = String.format("%.3f", resultado)

            lista.clear()
            lista.add(formatResultado)

            binding.editText.setText("")
            binding.editText.append("lg(")
            binding.editText.append(num3)
            binding.editText.append(")")
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Error: No se pudo calcular el logaritmo", Toast.LENGTH_LONG).show()
        }
    }

    private fun anadirLogaritmoNep(num: String) {
        try {
            val num2 = num.toDouble()
            val formattedNum2 = String.format("%.1f", num2).toDouble() // Redondear a 1 decimal

            val num3 = num

            val resultado = Math.log(formattedNum2)
            val formatResultado = String.format("%.3f", resultado)

            lista.clear()
            lista.add(formatResultado)

            binding.editText.setText("")
            binding.editText.append("ln(")
            binding.editText.append(num3)
            binding.editText.append(")")
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Error: No se pudo calcular el logaritmo", Toast.LENGTH_LONG).show()
        }
    }

    private fun anadirSeno(num: String) {
        try {
            val currentText = binding.degButton.text.toString()
            if (currentText == getString(R.string.deg)) {
                val currentText2 = binding.senButton.text.toString()
                if (currentText2 == "sin") {
                    // Convertir el número a Double
                    val num2 = num.toDouble()
                    val formattedNum2 =
                        String.format("%.1f", num2).toDouble() // Redondear a 1 decimal

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
                } else {
                    // Convertir el número a Double
                    val num2 = num.toDouble()
                    val formattedNum2 = String.format("%.1f", num2).toDouble() // Redondear a 1 decimal

                    // Guardar el número original
                    val num3 = num

                    // Calcular el seno
                    val resultado = Math.asin(formattedNum2)
                    val resultado2 = Math.toDegrees(resultado)
                    val formatResultado = String.format("%.3f", resultado2)

                    // Limpiar la lista y agregar el resultado
                    lista.clear()
                    lista.add(formatResultado)

                    // Mostrar el resultado en el EditText
                    binding.editText.setText("")
                    binding.editText.append("arcsin(")
                    binding.editText.append(num3)
                    binding.editText.append(")")
                }
            } else {
                // Convertir el número a Double
                val num2 = num.toDouble()
                val formattedNum2 = String.format("%.1f", num2).toDouble() // Redondear a 1 decimal

                // Guardar el número original
                val num3 = num

                // Calcular el seno
                val resultado = Math.sin(formattedNum2)
                val formatResultado = String.format("%.3f", resultado)

                // Limpiar la lista y agregar el resultado
                lista.clear()
                lista.add(formatResultado)

                // Mostrar el resultado en el EditText
                binding.editText.setText("")
                binding.editText.append("sen(")
                binding.editText.append(num3)
                binding.editText.append(")")
            }
        } catch (e: NumberFormatException) { // Capturar errores de conversión
            Toast.makeText(this, "Error: No se pudo calcular el seno", Toast.LENGTH_LONG).show()
        }
    }

    private fun anadirCoseno(num: String) {
        try {
            val currentText = binding.degButton.text.toString()
            if (currentText == getString(R.string.deg)) {
                val currentText2 = binding.cosButton.text.toString()
                if (currentText2 == "cos") {
                    // Convertir el número a Double
                    val num2 = num.toDouble()
                    val formattedNum2 =
                        String.format("%.1f", num2).toDouble() // Redondear a 1 decimal

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
                } else {
                    // Convertir el número a Double
                    val num2 = num.toDouble()
                    val formattedNum2 = String.format("%.1f", num2).toDouble() // Redondear a 1 decimal

                    // Guardar el número original
                    val num3 = num

                    // Calcular el coseno
                    val resultado = Math.acos(formattedNum2)
                    val resultado2 = Math.toDegrees(resultado)
                    val formatResultado = String.format("%.3f", resultado2)

                    // Limpiar la lista y agregar el resultado
                    lista.clear()
                    lista.add(formatResultado)

                    // Mostrar el resultado en el EditText
                    binding.editText.setText("")
                    binding.editText.append("arccos(")
                    binding.editText.append(num3)
                    binding.editText.append(")")
                }
            } else {
                // Convertir el número a Double
                val num2 = num.toDouble()
                val formattedNum2 = String.format("%.1f", num2).toDouble() // Redondear a 1 decimal

                // Guardar el número original
                val num3 = num

                // Calcular el coseno
                val resultado = Math.cos(formattedNum2)
                val formatResultado = String.format("%.3f", resultado)

                // Limpiar la lista y agregar el resultado
                lista.clear()
                lista.add(formatResultado)

                // Mostrar el resultado en el EditText
                binding.editText.setText("")
                binding.editText.append("cos(")
                binding.editText.append(num3)
                binding.editText.append(")")
            }
        } catch (e: NumberFormatException) { // Capturar errores de conversión
            Toast.makeText(this, "Error: No se pudo calcular el coseno", Toast.LENGTH_LONG).show()
        }
    }

    private fun anadirTangente(num: String) {
        try {
            val currentText = binding.degButton.text.toString()
            if (currentText == getString(R.string.deg)) {
                val currentText2 = binding.tanButton.text.toString()
                if (currentText2 == "tan") {
                    // Convertir el número a Double
                    val num2 = num.toDouble()
                    val formattedNum2 =
                        String.format("%.1f", num2).toDouble() // Redondear a 1 decimal

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
                } else {
                    // Convertir el número a Double
                    val num2 = num.toDouble()
                    val formattedNum2 = String.format("%.1f", num2).toDouble() // Redondear a 1 decimal

                    // Guardar el número original
                    val num3 = num

                    // Calcular la tangente
                    val resultado = Math.atan(formattedNum2)
                    val resultado2 = Math.toDegrees(resultado)
                    val formatResultado = String.format("%.3f", resultado2)

                    // Limpiar la lista y agregar el resultado
                    lista.clear()
                    lista.add(formatResultado)

                    // Mostrar el resultado en el EditText
                    binding.editText.setText("")
                    binding.editText.append("arctan(")
                    binding.editText.append(num3)
                    binding.editText.append(")")
                }
            } else {
                // Convertir el número a Double
                val num2 = num.toDouble()
                val formattedNum2 = String.format("%.1f", num2).toDouble() // Redondear a 1 decimal

                // Guardar el número original
                val num3 = num

                // Calcular la tangente
                val resultado = Math.tan(formattedNum2)
                val formatResultado = String.format("%.3f", resultado)

                // Limpiar la lista y agregar el resultado
                lista.clear()
                lista.add(formatResultado)

                // Mostrar el resultado en el EditText
                binding.editText.setText("")
                binding.editText.append("tan(")
                binding.editText.append(num3)
                binding.editText.append(")")
            }
        } catch (e: NumberFormatException) { // Capturar errores de conversión
            Toast.makeText(this, "Error: No se pudo calcular la tangente", Toast.LENGTH_LONG).show()
        }
    }


    private fun anadirRaiz(num: String) {
        try {
            val num2 = num.toDouble()
            val num3 = num

            if (num2 < 0) {
                Toast.makeText(this, "Error: Número negativo", Toast.LENGTH_LONG).show()
                return
            }

            val resultado = sqrt(num2)
            val formatResultado = String.format("%.3f", resultado)

            lista.clear()
            lista.add(formatResultado)

            binding.editText.setText("")
            binding.editText.append("√(")
            binding.editText.append(num3)
            binding.editText.append(")")
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Error: No se pudo calcular la raíz cuadrada", Toast.LENGTH_LONG).show()
        }
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



    private fun calcularFactorial(num: String) {
        try {
            val num2 = num.toInt()
            val num3 = num

            if (num2 < 0) {
                Toast.makeText(this, "Error: Número negativo", Toast.LENGTH_LONG).show()
                return
            }

            val resultado = factorial(num2)
            lista.clear()
            lista.add(resultado.toString())

            binding.editText.setText("")
            binding.editText.append(num3)
            binding.editText.append("!")
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Error: Número inválido", Toast.LENGTH_LONG).show()
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


        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("operacion", binding.editText.text.toString())
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