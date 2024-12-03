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

    // Declarar variables
    private lateinit var binding: CalculadoraComplejaBinding
    private val lista = ArrayList<String>()
    private lateinit var myButtons: Map<Button, String>
    private var resultadoCalculado = false
    private lateinit var dbHelper: SQLite

    // Función que se ejecuta al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializar el binding
        binding = CalculadoraComplejaBinding.inflate(layoutInflater)

        // Inicializar la base de datos
        dbHelper = SQLite(this, "CalculadoraDB", null, 1)

        // Establecer el contenido de la actividad
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

    // Función que se ejecuta al destruir la actividad
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

    // Función que se ejecuta al destruir la actividad
    private fun initListeners() {
        // Asignar un listener a cada botón
        myButtons.forEach { (button, value) ->
            button.setOnClickListener {
                // Verificar si el resultado ya fue calculado
                if (resultadoCalculado) {
                    // Limpiar la lista y el EditText
                    if (value in setOf("+", "-", "x", "/")) {
                        // Si el valor es un operador, limpiar la lista y agregar el resultado
                        lista.add(value)
                        binding.editText.append(value)
                    } else { // Si el valor es un número
                        lista.clear()
                        // Verificar si el valor es una constante
                        if (value == "\u03C0") {
                            // Agregar el valor de PI a la lista
                            binding.editText.setText(Math.PI.toString())
                            lista.add(Math.PI.toString())
                        } else if (value == "e") { // Agregar el valor de E a la lista
                            // Agregar el valor de E a la lista
                            binding.editText.setText("e")
                            lista.add(Math.E.toString())
                        } else { // Agregar el valor numérico a la lista
                            // Agregar el valor numérico a la lista
                            binding.editText.setText(value)
                            lista.add(value)
                        }
                    }
                    // Cambiar el estado de la variable
                    resultadoCalculado = false
                } else { // Si el resultado no fue calculado
                    // Verificar si el valor es un operador
                    if (value == "\u03C0") { // Si el valor es PI
                        // Agregar el valor de PI a la lista
                        val piValue = Math.PI
                        lista.add(piValue.toString())
                        binding.editText.append("\u03C0")
                    } else if (value == "e") { // Si el valor es E
                        val eValue = Math.E
                        lista.add(eValue.toString())
                        binding.editText.append("e")
                    } else if (value == ".") { // Si el valor es un punto
                        // Verificar si la lista está vacía o si el último elemento es un operador
                        if (lista.isEmpty() || lista.last() == ".") {
                            // Mostrar un mensaje de error
                            Toast.makeText(this, "Operación inválida", Toast.LENGTH_SHORT).show()
                        } else { // Si no hay errores
                            // Agregar el punto a la lista y al EditText
                            lista.add(value)
                            binding.editText.append(value)
                        }
                    } else { // Si el valor es un número o un operador
                        // Agregar el valor a la lista y al EditText
                        lista.add(value)
                        binding.editText.append(value)
                    }
                }
            }
        }


        // Asignar un listener a los botones especiales
        binding.othersImageButton2.setOnClickListener {
            val popupMenu = PopupMenu(this, it)

            // Agregar elementos al menú emergente
            popupMenu.menu.add(0, 1, 0, "Historial")
            popupMenu.menu.add(0, 2, 1, "Magnitudes")
            popupMenu.setOnMenuItemClickListener { menuItem ->
                // Verificar el elemento seleccionado
                when (menuItem.itemId) {
                    1 -> { // Mostrar el historial
                        mostrarHistorial()
                        Toast.makeText(this, "Historial seleccionado", Toast.LENGTH_SHORT).show()
                        true
                    }
                    2 -> { // Mostrar las magnitudes
                        mostrarMagnitudes()
                        true
                    } // Otros elementos
                    else -> false
                }
            }
            // Mostrar el menú emergente
            popupMenu.show()
        }


        // Asignar un listener a los botones especiales
        binding.secondButton.setOnClickListener {
            // Verificar el texto actual del botón
            val currentText = binding.senButton.text.toString()

            // Cambiar el texto del botón y habilitar o deshabilitar los botones de funciones trigonométricas
            if (currentText == "sin") {
                binding.degButton.isEnabled = false
                binding.degButton.setTextColor(ContextCompat.getColor(this, R.color.gray))
                binding.senButton.text = "sin⁻¹"
                binding.cosButton.text = "cos⁻¹"
                binding.tanButton.text = "tan⁻¹"
            } else { // Si el texto es diferente
                binding.degButton.isEnabled = true
                binding.degButton.setTextColor(ContextCompat.getColor(this, R.color.black))
                binding.senButton.text = "sin"
                binding.cosButton.text = "cos"
                binding.tanButton.text = "tan"
            }
        }

        // Asignar un listener al botón de grados/radianes
        binding.degButton.setOnClickListener {
            // Verificar el texto actual del botón
            val currentText = binding.degButton.text.toString()
            // Cambiar el texto del botón y habilitar o deshabilitar el botón de la segunda función
            if (currentText == getString(R.string.deg)) {
                binding.secondButton.isEnabled = false
                binding.secondButton.setTextColor(ContextCompat.getColor(this, R.color.gray))
                binding.degButton.text = getString(R.string.rad)
            } else { // Si el texto es diferente
                binding.secondButton.isEnabled = true
                binding.secondButton.setTextColor(ContextCompat.getColor(this, R.color.black))
                binding.degButton.text = getString(R.string.deg)
            }
        }

        // Asignar un listener al botón de igual
        binding.equalsButton.setOnClickListener {
            resultado()
        }

        // Asignar un listener al botón de borrar
        binding.cleanAllButton.setOnClickListener {
            lista.clear()
            binding.editText.setText("")
        }

        // Asignar un listener al botón de borrar
        binding.deleteButton.setOnClickListener {
            deleteLast()
        }

        // Asignar un listener al botón de porcentaje
        binding.percentButton.setOnClickListener {
            calculatePercentage()
        }

        // Asignar un listener al botón de factorial
        binding.switchSimpleButton.setOnClickListener {
            val intent = Intent(this, CalculadoraSimple::class.java)
            startActivity(intent)
        }

        // Asignar un listener al botón de conversores
        binding.ConversorTextView.setOnClickListener {
            val intent = Intent(this, Conversores::class.java)
            startActivity(intent)
        }

        // Asignar un listener a los botones de funciones
        binding.factorialButton.setOnClickListener {
            // Encontrar el número en la lista
            val num = encontrarNumeros()
            // Verificar si el número es válido
            if (num != null && num.isNotEmpty() && num.none { it in setOf('+', '-', 'x', '/') }) {
                // Calcular el factorial
                calcularFactorial(num)
            } else { // Si el número no es válido
                // Mostrar un mensaje de error
                Toast.makeText(this, "Error: Entrada inválida", Toast.LENGTH_LONG).show()
            }
        }

        // Asignar un listener al botón de fracción
        binding.funcReciButton.setOnClickListener {
            // Encontrar el número en la lista
            val num = encontrarNumeros()
            // Verificar si el número es válido
            if (num != null && num.isNotEmpty() && num.none { it in setOf('+', '-', 'x', '/') }) {
                // Calcular el inverso
                anadirFraccion(num)
            } else { // Si el número no es válido
                // Mostrar un mensaje de error
                Toast.makeText(this, "Error: Entrada inválida", Toast.LENGTH_LONG).show()
            }
        }

        // Asignar un listener al botón de raíz cuadrada
        binding.squareRootButton.setOnClickListener{
            // Encontrar el número en la lista
            val num = encontrarNumeros()
            // Verificar si el número es válido
            if (num != null && num.isNotEmpty() && num.none { it in setOf('+', '-', 'x', '/') }) {
                // Calcular la raíz cuadrada
                anadirRaiz(num)
            } else { // Si el número no es válido
                // Mostrar un mensaje de error
                Toast.makeText(this, "Error: Entrada inválida", Toast.LENGTH_LONG).show()
            }
        }

        // Asignar un listener al botón de potencia
        binding.xRaisedYButton.setOnClickListener {
            // Verificar si la lista no está vacía y si el último elemento es un número
            if (lista.isNotEmpty() && lista.last().toDoubleOrNull() != null) {
                // Si el último elemento es un número, agregamos el operador "^"
                lista.add("^")
                binding.editText.append("^")
            } else { // Si el último elemento no es un número
                // Mostrar un mensaje de error
                Toast.makeText(this, "Primero debes ingresar un número base", Toast.LENGTH_LONG).show()
            }
        }

        // Asignar un listener al botón de seno
        binding.senButton.setOnClickListener {
            val num = encontrarNumeros()
            // Verificar si el número es válido
            if (num != null && num.isNotEmpty() && num.none { it in setOf('+', '-', 'x', '/') }) {
                // Calcular el seno
                anadirSeno(num)
            } else { // Si el número no es válido
                // Mostrar un mensaje de error
                Toast.makeText(this, "Error: Entrada inválida", Toast.LENGTH_LONG).show()
            }
        }

        // Asignar un listener al botón de coseno
        binding.cosButton.setOnClickListener {
            // Encontrar el número en la lista
            val num = encontrarNumeros()
            // Verificar si el número es válido
            if (num != null && num.isNotEmpty() && num.none { it in setOf('+', '-', 'x', '/') }) {
                // Calcular el coseno
                anadirCoseno(num)
            } else { // Si el número no es válido
                // Mostrar un mensaje de error
                Toast.makeText(this, "Error: Entrada inválida", Toast.LENGTH_LONG).show()
            }
        }

        // Asignar un listener al botón de tangente
        binding.tanButton.setOnClickListener {
            // Encontrar el número en la lista
            val num = encontrarNumeros()
            // Verificar si el número es válido
            if (num != null && num.isNotEmpty() && num.none { it in setOf('+', '-', 'x', '/') }) {
                // Calcular la tangente
                anadirTangente(num)
            } else { // Si el número no es válido
                // Mostrar un mensaje de error
                Toast.makeText(this, "Error: Entrada inválida", Toast.LENGTH_LONG).show()
            }
        }

        // Asignar un listener al botón de logaritmo
        binding.lgButton.setOnClickListener {
            // Encontrar el número en la lista
            val num = encontrarNumeros()
            // Verificar si el número es válido
            if (num != null && num.isNotEmpty() && num.none { it in setOf('+', '-', 'x', '/') }) {
                // Calcular el logaritmo
                anadirLogaritmo(num)
            } else { // Si el número no es válido
                // Mostrar un mensaje de error
                Toast.makeText(this, "Error: Entrada inválida", Toast.LENGTH_LONG).show()
            }
        }

        // Asignar un listener al botón de logaritmo natural
        binding.lnButton.setOnClickListener {
            // Encontrar el número en la lista
            val num = encontrarNumeros()
            // Verificar si el número es válido
            if (num != null && num.isNotEmpty() && num.none { it in setOf('+', '-', 'x', '/') }) {
                // Calcular el logaritmo natural
                anadirLogaritmoNep(num)
            } else { // Si el número no es válido
                // Mostrar un mensaje de error
                Toast.makeText(this, "Error: Entrada inválida", Toast.LENGTH_LONG).show()
            }
        }
    }


    // Función que se ejecuta al destruir la actividad
    private fun mostrarMagnitudes() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Magnitudes")

        // Crear un ScrollView y un GridLayout
        val scrollView = ScrollView(this)
        val layout = GridLayout(this)
        layout.rowCount = 5
        layout.columnCount = 2

        // Crear una lista de constantes
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

        // Crear un diálogo y una variable para el diálogo
        lateinit var dialog: AlertDialog // Declarar la variable antes

        // Recorrer la lista de constantes
        for ((name, symbol, value) in constants) {
            // Crear un ImageView y un TextView
            val imageView = ImageView(this)
            imageView.setImageResource(getImageResourceByName(symbol))
            val paramsImage = GridLayout.LayoutParams()

            // Establecer los parámetros de la imagen
            paramsImage.width = 200
            paramsImage.height = 200
            paramsImage.setMargins(50, 70, 0, 0)
            imageView.layoutParams = paramsImage

            // Crear un TextView
            val textView = TextView(this)
            textView.text = name
            textView.gravity = Gravity.CENTER
            val paramsText = GridLayout.LayoutParams()

            // Establecer los parámetros del texto
            paramsText.width = GridLayout.LayoutParams.WRAP_CONTENT
            paramsText.height = GridLayout.LayoutParams.WRAP_CONTENT
            paramsText.setMargins(50, 100, 0, 0)
            textView.layoutParams = paramsText

            // Crear un listener para los elementos
            val clickListener = {
                // Obtener el texto actual del EditText
                val currentText = binding.editText.text.toString()
                // Verificar si el texto actual es un número
                if (currentText.toDoubleOrNull() != null) { // Si el texto es un número
                    binding.editText.setText(symbol)
                    lista.clear()
                    lista.add(value.toString())
                } else { // Si el texto no es un número
                    binding.editText.append(symbol)
                    lista.add(value.toString())
                }
                // Cerrar el diálogo
                dialog.dismiss()
            }

            // Asignar un listener a los elementos
            imageView.setOnClickListener { clickListener() }
            textView.setOnClickListener { clickListener() }

            // Agregar los elementos al layout
            layout.addView(imageView)
            layout.addView(textView)
        }

        // Agregar el layout al ScrollView
        scrollView.addView(layout)
        builder.setView(scrollView)

        dialog = builder.create() // Crear el diálogo aquí
        dialog.show()
    }



    // Función que se ejecuta al destruir la actividad
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

    // Función que se ejecuta al destruir la actividad
    private fun mostrarHistorial() {
        // Crear un diálogo de alerta
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM historial", null)
        val registros = ArrayList<String>()

        // Recorrer los registros de la base de datos
        if (cursor.moveToFirst()) {
            // Recorrer los registros de la base de datos
            do {
                // Obtener los valores de la base de datos
                val operacion = cursor.getString(cursor.getColumnIndexOrThrow("operacion"))
                val resultado = cursor.getString(cursor.getColumnIndexOrThrow("resultado"))
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"))
                registros.add("\n$fecha \n $operacion = $resultado \n")
            } while (cursor.moveToNext()) // Mientras haya registros
        }

        // Cerrar la base de datos
        cursor.close()
        db.close()

        // Crear un diálogo de alerta
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Historial")

        // Verificar si no hay registros
        if (registros.isEmpty()) {
            // Mostrar un mensaje si no hay registros
            val textView = TextView(this)
            textView.text = "No hay registros todavía"
            textView.textSize = 20f
            textView.gravity = Gravity.CENTER
            textView.height = 200
            builder.setView(textView)
        } else { // Si hay registros
            // Crear un ListView y un ArrayAdapter
            val listView = ListView(this)
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, registros)
            listView.adapter = adapter
            builder.setView(listView)
        }

        // Agregar botones al diálogo
        builder.setPositiveButton("Cerrar") { dialog, _ -> dialog.dismiss() }
        builder.setNegativeButton("Borrar Historial") { dialog, _ ->
            borrarHistorial()
            dialog.dismiss()
        }
        builder.create().show()
    }

    // Función que se ejecuta al destruir la actividad
    private fun borrarHistorial() {
        val db = dbHelper.writableDatabase
        db.delete("historial", null, null)
        db.close()
        Toast.makeText(this, "Historial borrado", Toast.LENGTH_SHORT).show()
    }


    // Función que se ejecuta al destruir la actividad
    private fun anadirLogaritmo(num: String) {
        try {
            // Convertir el número a Double
            val num2 = num.toDouble()
            val formattedNum2 = String.format("%.1f", num2).toDouble() // Redondear a 1 decimal

            // Guardar el número original
            val num3 = num

            // Calcular el logaritmo
            val resultado = Math.log10(formattedNum2)
            val formatResultado = String.format("%.3f", resultado)

            // Limpiar la lista y agregar el resultado
            lista.clear()
            lista.add(formatResultado)

            // Mostrar el resultado en el EditText
            binding.editText.setText("")
            binding.editText.append("lg(")
            binding.editText.append(num3)
            binding.editText.append(")")
        } catch (e: NumberFormatException) { // Capturar errores de conversión
            // Mostrar un mensaje de error
            Toast.makeText(this, "Error: No se pudo calcular el logaritmo", Toast.LENGTH_LONG).show()
        }
    }

    // Función que se ejecuta al destruir la actividad
    private fun anadirLogaritmoNep(num: String) {
        // Convertir el número a Double
        try {
            // Convertir el número a Double
            val num2 = num.toDouble()
            val formattedNum2 = String.format("%.1f", num2).toDouble() // Redondear a 1 decimal

            // Guardar el número original
            val num3 = num

            // Calcular el logaritmo
            val resultado = Math.log(formattedNum2)
            val formatResultado = String.format("%.3f", resultado)

            // Limpiar la lista y agregar el resultado
            lista.clear()
            lista.add(formatResultado)

            // Mostrar el resultado en el EditText
            binding.editText.setText("")
            binding.editText.append("ln(")
            binding.editText.append(num3)
            binding.editText.append(")")
        } catch (e: NumberFormatException) { // Capturar errores de conversión
            // Mostrar un mensaje de error
            Toast.makeText(this, "Error: No se pudo calcular el logaritmo", Toast.LENGTH_LONG).show()
        }
    }

    // Función que se ejecuta al destruir la actividad
    private fun anadirSeno(num: String) {
        try {
            // Convertir el número a Double
            val currentText = binding.degButton.text.toString()
            // Verificar si el texto actual es "deg"
            if (currentText == getString(R.string.deg)) {
                // Verificar si el texto actual es "sin"
                val currentText2 = binding.senButton.text.toString()
                // Verificar si el texto actual es "sin"
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
                } else { // Si el texto actual es diferente
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
            } else { // Si el texto actual es diferente
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
            // Mostrar un mensaje de error
            Toast.makeText(this, "Error: No se pudo calcular el seno", Toast.LENGTH_LONG).show()
        }
    }

    // Función que se ejecuta al destruir la actividad
    private fun anadirCoseno(num: String) {
        // Convertir el número a Double
        try {
            // Convertir el número a Double
            val currentText = binding.degButton.text.toString()
            // Verificar si el texto actual es "deg"
            if (currentText == getString(R.string.deg)) {
                // Verificar si el texto actual es "cos"
                val currentText2 = binding.cosButton.text.toString()
                // Verificar si el texto actual es "cos"
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
                } else { // Si el texto actual es diferente
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
            } else { // Si el texto actual es diferente
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
            // Mostrar un mensaje de error
            Toast.makeText(this, "Error: No se pudo calcular el coseno", Toast.LENGTH_LONG).show()
        }
    }

    // Función que se ejecuta al destruir la actividad
    private fun anadirTangente(num: String) {
        // Convertir el número a Double
        try {
            // Convertir el número a Double
            val currentText = binding.degButton.text.toString()
            // Verificar si el texto actual es "deg"
            if (currentText == getString(R.string.deg)) {
                // Verificar si el texto actual es "tan"
                val currentText2 = binding.tanButton.text.toString()
                // Verificar si el texto actual es "tan"
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
                } else { // Si el texto actual es diferente
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
            } else { // Si el texto actual es diferente
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
            // Mostrar un mensaje de error
            Toast.makeText(this, "Error: No se pudo calcular la tangente", Toast.LENGTH_LONG).show()
        }
    }


    // Función que se ejecuta al destruir la actividad
    private fun anadirRaiz(num: String) {
        // Convertir el número a Double
        try {
            // Convertir el número a Double
            val num2 = num.toDouble()
            // Guardar el número original
            val num3 = num

            // Verificar si el número es negativo
            if (num2 < 0) {
                // Mostrar un mensaje de error
                Toast.makeText(this, "Error: Número negativo", Toast.LENGTH_LONG).show()
                return
            }

            // Calcular la raíz cuadrada
            val resultado = sqrt(num2)
            val formatResultado = String.format("%.3f", resultado)

            // Limpiar la lista y agregar el resultado
            lista.clear()
            lista.add(formatResultado)

            // Mostrar el resultado en el EditText
            binding.editText.setText("")
            binding.editText.append("√(")
            binding.editText.append(num3)
            binding.editText.append(")")
        } catch (e: NumberFormatException) { // Capturar errores de conversión
            // Mostrar un mensaje de error
            Toast.makeText(this, "Error: No se pudo calcular la raíz cuadrada", Toast.LENGTH_LONG).show()
        }
    }


    // Función que se ejecuta al destruir la actividad
    private fun encontrarNumeros(): String? {
        // Crear un conjunto de símbolos
        val simbols = setOf("+", "-", "x", "/", "^")
        var num = ""

        // Verificar si la lista es nula o vacía
        if (lista.isNullOrEmpty()) {
            // Mostrar un mensaje de error
            Toast.makeText(this, "Error: Lista vacía o nula", Toast.LENGTH_LONG).show()
            return null
        }

        // Buscar los números desde el final de la lista
        for (i in lista.asReversed()) {
            // Verificar si el elemento actual está en el conjunto de símbolos
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
        } catch (e: NumberFormatException) { // Capturar errores de conversión
            // Mostrar un mensaje de error
            Toast.makeText(this, "Error: No se pudo calcular el inverso", Toast.LENGTH_LONG).show()
        }
    }



    // Función que se ejecuta al destruir la actividad
    private fun calcularFactorial(num: String) {
        // Convertir el número a Int
        try {
            // Convertir el número a Int
            val num2 = num.toInt()
            // Guardar el número original
            val num3 = num

            // Verificar si el número es negativo
            if (num2 < 0) {
                // Mostrar un mensaje de error
                Toast.makeText(this, "Error: Número negativo", Toast.LENGTH_LONG).show()
                return
            }

            // Calcular el factorial
            val resultado = factorial(num2)
            lista.clear()
            lista.add(resultado.toString())

            // Mostrar el resultado en el EditText
            binding.editText.setText("")
            binding.editText.append(num3)
            binding.editText.append("!")
        } catch (e: NumberFormatException) { // Capturar errores de conversión
            // Mostrar un mensaje de error
            Toast.makeText(this, "Error: Número inválido", Toast.LENGTH_LONG).show()
        }
    }

    // Función que se ejecuta al destruir la actividad
    private fun factorial(n: Int): Int {
        // Calcular el factorial de forma recursiva
        return if (n == 0) 1 else n * factorial(n - 1)
    }

    // Función que se ejecuta al destruir la actividad
    private fun resultado() {
        // Verificar si la lista está vacía o si el último elemento es un operador
        if (lista.isEmpty() || lista.last() in setOf("+", "-", "x", "/")) {
            // Mostrar un mensaje de error
            Toast.makeText(this, "Se ha producido un error", Toast.LENGTH_LONG).show()
            return
        }

        // Calcular el resultado
        val resultado = calculo(lista)


        // Guardar el resultado en la base de datos
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("operacion", binding.editText.text.toString())
            put("fecha", SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()))
            put("resultado", resultado.toString())
        }
        db.insert("historial", null, values)
        db.close()


        // Limpiar la lista y agregar el resultado
        lista.clear()
        lista.add(resultado.toString())

        // Mostrar el resultado en el EditText
        val resultadoFinal: String

        // Verificar si el resultado es un número entero
        if (resultado % 1 == 0.0) {
            // Convertir el resultado a Int y luego a String
            resultadoFinal = resultado.toInt().toString()
        } else {
            // Convertir el resultado a String
            resultadoFinal = resultado.toString()
        }

        // Mostrar el resultado en el EditText
        binding.editText.setText(resultadoFinal)
        resultadoCalculado = true
    }

    // Función que se ejecuta al destruir la actividad
    private fun deleteLast() {
        // Verificar si la lista no está vacía
        if (lista.isNotEmpty()) {
            // Eliminar el último elemento de la lista
            lista.removeAt(lista.size - 1)
            binding.editText.setText(lista.joinToString(""))
        }
    }

    // Función que se ejecuta al destruir la actividad
    private fun calculatePercentage() {
        // Verificar si la lista no está vacía y si el último elemento es un número
        if (lista.isNotEmpty() && lista.last().toDoubleOrNull() != null) {
            // Calcular el porcentaje
            val number = lista.joinToString("").toDouble()
            val percentage = number / 100
            lista.clear()
            lista.add(percentage.toString())
            binding.editText.setText(percentage.toString())
        } else { // Si el último elemento no es un número
            // Mostrar un mensaje de error
            Toast.makeText(this, "No se puede calcular el porcentaje", Toast.LENGTH_LONG).show()
        }
    }

    // Función que se ejecuta al destruir la actividad
    private fun calculo(lista: ArrayList<String>): Double {
        // Verificar si los paréntesis están balanceados
        if (!areParenthesesBalanced(lista)) {
            Toast.makeText(this, "Paréntesis no balanceados", Toast.LENGTH_LONG).show()
            throw IllegalArgumentException("Paréntesis no balanceados")
        }

        // Resolver paréntesis primero
        while (lista.contains("(")) {
            // Encontrar el índice del último paréntesis de apertura
            val openIndex = lista.lastIndexOf("(")
            val closeIndex = lista.subList(openIndex, lista.size).indexOf(")") + openIndex
            // Verificar si no se encuentra el paréntesis de cierre
            if (closeIndex == -1) {
                // Mostrar un mensaje de error
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



    // Función que se ejecuta al destruir la actividad
    private fun parseInput(
        // Función que se ejecuta al destruir la actividad
        lista: ArrayList<String>,
        operators: Set<String>,
        numbers: MutableList<Double>,
        operations: MutableList<String>
    ) {
        // Parsear la entrada
        var number = ""
        var isNegative = false

        // Recorrer la lista de entrada
        for (item in lista) {
            // Verificar si el elemento actual es un operador
            if (item in operators) {
                // Verificar si el operador es una resta
                if (item == "-" && (number.isEmpty() || operations.isNotEmpty() && operations.last() in operators)) {
                    // Marcar el número como negativo
                    isNegative = true
                } else { // Si el operador no es una resta
                    // Verificar si hay un número
                    if (number.isNotEmpty()) {
                        // Agregar el número a la lista
                        numbers.add(if (isNegative) -number.toDouble() else number.toDouble())
                        number = ""
                        isNegative = false
                    }
                    operations.add(item)
                }
            } else { // Si el elemento actual no es un operador
                number += item
            }
        }

        //  Agregar el último número a la lista
        if (number.isNotEmpty()) {
            // Agregar el número a la lista
            numbers.add(if (isNegative) -number.toDouble() else number.toDouble())
        }
    }

    // Función que se ejecuta al destruir la actividad
    private fun validateExpression(numbers: List<Double>, operations: List<String>) {
        // Verificar si la expresión es válida
        if (numbers.size - 1 != operations.size) { // Si la cantidad de números no es igual a la cantidad de operaciones
            // Mostrar un mensaje de error
            Toast.makeText(this, "La expresión no es válida", Toast.LENGTH_LONG).show()
        }
    }

    // Función que se ejecuta al destruir la actividad
    private fun performOperations(
        numbers: MutableList<Double>,
        operations: MutableList<String>,
        targetOperations: Set<String>
    ) {
        // Realizar las operaciones
        var i = 0
        // Recorrer la lista de operaciones
        while (i < operations.size) {
            // Verificar si la operación actual está en el conjunto de operaciones objetivo
            if (operations[i] in targetOperations) {
                // Realizar la operación
                val result = when (operations[i]) { // Realizar la operación correspondiente
                    "^" -> numbers[i].pow(numbers[i + 1])
                    "x" -> numbers[i] * numbers[i + 1]
                    "/" -> numbers[i] / numbers[i + 1]
                    "+" -> numbers[i] + numbers[i + 1]
                    "-" -> numbers[i] - numbers[i + 1]
                    else -> throw IllegalArgumentException("Operación no válida.")
                }
                // Reemplazar los números y las operaciones
                numbers[i] = result
                numbers.removeAt(i + 1)
                operations.removeAt(i)
            } else { // Si la operación actual no está en el conjunto de operaciones objetivo
                i++
            }
        }
    }


    // Función que se ejecuta al destruir la actividad
    private fun roundIfDivision(result: Double, lista: ArrayList<String>, operators: Set<String>): Double {
        // Redondear el resultado si es una división
        val lastOperation = lista.lastOrNull { it in operators }
        return if (lastOperation == "/") { // Si la última operación es una división
            Math.round(result * 1000.0) / 1000.0
        } else { // Si la última operación no es una división
            result
        }
    }

    // Función que se ejecuta al destruir la actividad
    private fun areParenthesesBalanced(lista: ArrayList<String>): Boolean {
        // Verificar si los paréntesis están balanceados
        var balance = 0
        // Recorrer la lista de entrada
        for (item in lista) { // Recorrer la lista de entrada
            // Verificar si el elemento actual es un paréntesis
            when (item) { // Verificar si el elemento actual es un paréntesis
                "(" -> balance++
                ")" -> balance--
            }
            // Verificar si el balance es negativo
            if (balance < 0) return false // Más paréntesis de cierre que de apertura
        }
        return balance == 0
    }



}