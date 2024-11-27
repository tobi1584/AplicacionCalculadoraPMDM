package com.example.calculadora.conversores

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.calculadora.R

class Longitud : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.longitud)

        val optionsSpinner: Spinner = findViewById(R.id.spinner1)
        val selectedOptionTextView: TextView = findViewById(R.id.seleccion)

        val options = arrayOf("Kilómetro km", "Metro m", "Decímetro dm", "Centímetro cm", "Milímetro mm", "Micrómetro µm", "Nanómetro nm", "Picómetro pm", "Milla náutica nmi", "Milla mi", "Yarda yd", "Pie ft", "Pulgada in", "Pársec pc", "Distancia lunar ld", "Unidad astronómica ua", "Año luz ly")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        optionsSpinner.adapter = adapter

        optionsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedOptionTextView.text = "${options[position]}"
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

}