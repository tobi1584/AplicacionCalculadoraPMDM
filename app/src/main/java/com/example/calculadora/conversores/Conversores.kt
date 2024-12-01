package com.example.calculadora.conversores

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.calculadora.CalculadoraSimple
import com.example.calculadora.R
import com.example.calculadora.databinding.ConversoresBinding

class Conversores : AppCompatActivity() {

    private lateinit var conversoresBinding: ConversoresBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        conversoresBinding = ConversoresBinding.inflate(layoutInflater)
        setContentView(conversoresBinding.root)

        initListeners()
    }

    private fun initListeners() {
        conversoresBinding.ConversorTextView.setOnClickListener {
            val intent = Intent(this, Conversores::class.java)
            startActivity(intent)
        }

        conversoresBinding.longitudImageButton.setOnClickListener {
            val intent = Intent(this, Longitud::class.java)
            startActivity(intent)
        }

        conversoresBinding.areaImageButton.setOnClickListener {
            val intent = Intent(this, Area::class.java)
            startActivity(intent)
        }

        conversoresBinding.tiempoImageButton.setOnClickListener {
            val intent = Intent(this, Tiempo::class.java)
            startActivity(intent)
        }

        conversoresBinding.sistemaNumeralImageButton.setOnClickListener {
            val intent = Intent(this, SistemaNumeral::class.java)
            startActivity(intent)
        }
    }
}