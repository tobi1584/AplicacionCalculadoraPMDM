package com.example.calculadora

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLite(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_CONSTANTE = "CREATE TABLE constante (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, valor FLOAT, unidad_de_medida TEXT);"

        // Fecha en formato ISO8601 YYYY-MM-DD HH:MM:SS
        // db?.execSQL("Select datetime('now', 'localtime')")

        val CREATE_TABLE_HISTORIAL = "CREATE TABLE historial (id_calculo INTEGER PRIMARY KEY AUTOINCREMENT, operacion TEXT, fecha TEXT);"
        db?.execSQL(CREATE_TABLE_CONSTANTE)
        db?.execSQL(CREATE_TABLE_HISTORIAL)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

}