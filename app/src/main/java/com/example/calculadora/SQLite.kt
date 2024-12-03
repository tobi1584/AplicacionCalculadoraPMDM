package com.example.calculadora

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// SQLiteOpenHelper es una clase auxiliar que maneja la creación y actualización de la base de datos.
class SQLite(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, 3) {
    // Se ejecuta la primera vez que se accede a la base de datos
    override fun onCreate(db: SQLiteDatabase?) {
        // Se ejecutan las sentencias SQL de creación de las tablas
        val CREATE_TABLE_CONSTANTE = "CREATE TABLE constante (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, valor FLOAT, unidad_de_medida TEXT);"

        // Fecha en formato ISO8601 YYYY-MM-DD HH:MM:SS
        // db?.execSQL("Select datetime('now', 'localtime')")

        // Se crea la tabla historial con los campos id_calculo, operacion, resultado y fecha
        val CREATE_TABLE_HISTORIAL = "CREATE TABLE historial (id_calculo INTEGER PRIMARY KEY AUTOINCREMENT, operacion TEXT, resultado TEXT, fecha TEXT);"
        db?.execSQL(CREATE_TABLE_CONSTANTE)
        db?.execSQL(CREATE_TABLE_HISTORIAL)

    }

    // Se ejecuta cuando se detecta un cambio en la versión de la base de datos
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS constante")
        db?.execSQL("DROP TABLE IF EXISTS historial")
        onCreate(db)
    }



}