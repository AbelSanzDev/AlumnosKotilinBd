package com.sanchezurrea.appmenubutton.database

import android.provider.BaseColumns

class DefinirDB {
    object Alumnos : BaseColumns {
        const val TABLA = "alumnos"
        const val ID = "id"
        const val MATRICULA = "matricula"
        const val NOMBRE = "nombre"
        const val DOMICILIO = "domicilio"
        const val ESPECIALIDAD = "especialidad"
        const val FOTO = "foto"
    }
}