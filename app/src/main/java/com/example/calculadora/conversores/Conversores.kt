package com.example.calculadora.conversores

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.calculadora.R

class Conversores : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.conversores)

        val longitudButton: ImageButton = findViewById(R.id.longitudImageButton)
        longitudButton.setOnClickListener {
            val intent = Intent(this, Longitud::class.java)
            startActivity(intent)
        }

    }


}