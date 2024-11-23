package com.example.calculadora

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calculadora.databinding.CalculadoraComplejaBinding

class CalculadoraCompleja : AppCompatActivity() {

    private lateinit var binding: CalculadoraComplejaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CalculadoraComplejaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
        initListeners()
    }



    private fun initComponents() {

    }

    private fun initListeners() {
        binding.switchSimpleButton.setOnClickListener{
            val intent = Intent(this, CalculadoraSimple::class.java)
            startActivity(intent)
        }
    }


}