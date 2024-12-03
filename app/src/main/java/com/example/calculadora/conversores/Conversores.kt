package com.example.calculadora.conversores

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.calculadora.CalculadoraSimple
import com.example.calculadora.databinding.ConversoresBinding

class Conversores : AppCompatActivity() {

    // ViewBinding
    private lateinit var conversoresBinding: ConversoresBinding

    // Función que se ejecuta al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializar ViewBinding
        conversoresBinding = ConversoresBinding.inflate(layoutInflater)
        setContentView(conversoresBinding.root)

        initListeners()
    }

    // Función para inicializar los listeners de los botones
    private fun initListeners() {
        // Listener para el botón de la calculadora
        conversoresBinding.CalculadoraTextView.setOnClickListener {
            val intent = Intent(this, CalculadoraSimple::class.java)
            startActivity(intent)
        }

        conversoresBinding.longitud.setOnClickListener {
            val intent = Intent(this, Longitud::class.java)
            startActivity(intent)
        }

        conversoresBinding.masa.setOnClickListener {
            val intent = Intent(this, Masa::class.java)
            startActivity(intent)
        }

        conversoresBinding.area.setOnClickListener {
            val intent = Intent(this, Area::class.java)
            startActivity(intent)
        }

        conversoresBinding.tiempo.setOnClickListener {
            val intent = Intent(this, Tiempo::class.java)
            startActivity(intent)
        }


        conversoresBinding.datos.setOnClickListener {
            val intent = Intent(this, Datos::class.java)
            startActivity(intent)
        }

        conversoresBinding.descuento.setOnClickListener {
            val intent = Intent(this, Descuento::class.java)
            startActivity(intent)
        }

        conversoresBinding.volumen.setOnClickListener {
            val intent = Intent(this, Volumen::class.java)
            startActivity(intent)
        }

        conversoresBinding.sistNum.setOnClickListener {
            val intent = Intent(this, SistemaNumeral::class.java)
            startActivity(intent)
        }

        conversoresBinding.velocidad.setOnClickListener {
            val intent = Intent(this, Velocidad::class.java)
            startActivity(intent)
        }

        conversoresBinding.temperatura.setOnClickListener {
            val intent = Intent(this, Temperatura::class.java)
            startActivity(intent)
        }

        conversoresBinding.imc.setOnClickListener {
            val intent = Intent(this, Imc::class.java)
            startActivity(intent)
        }
    }
}