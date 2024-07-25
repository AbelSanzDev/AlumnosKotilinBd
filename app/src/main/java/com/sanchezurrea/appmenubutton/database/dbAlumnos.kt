package com.sanchezurrea.appmenubutton.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class dbAlumnos (private val context: Context) {
   private val dbHelper : AlumnosDbHelper = AlumnosDbHelper(context)
   private lateinit var db: SQLiteDatabase

   private val leerRegistro = arrayOf(
      DefinirDB.Alumnos.ID,
      DefinirDB.Alumnos.MATRICULA,
      DefinirDB.Alumnos.NOMBRE,
      DefinirDB.Alumnos.DOMICILIO,
      DefinirDB.Alumnos.ESPECIALIDAD,
      DefinirDB.Alumnos.FOTO,
   )

   fun openDatabase() {
      db = dbHelper.writableDatabase
   }

   fun insertarAlumno(alumno: Alumno): Long {
      if (matriculaExiste(alumno.matricula))
         return 0;
      val value = ContentValues().apply {
         put(DefinirDB.Alumnos.MATRICULA, alumno.matricula)
         put(DefinirDB.Alumnos.NOMBRE, alumno.nombre)
         put(DefinirDB.Alumnos.DOMICILIO, alumno.domicilio)
         put(DefinirDB.Alumnos.ESPECIALIDAD, alumno.especialidad)
         put(DefinirDB.Alumnos.FOTO, alumno.foto)
      }
//      db.delete("alumnos", null, null)
      return db.insert(DefinirDB.Alumnos.TABLA, null, value)
   }

   fun actualizarAlumno(alumno: Alumno, id: Int): Int {
      val values = ContentValues().apply {
         put(DefinirDB.Alumnos.MATRICULA, alumno.matricula)
         put(DefinirDB.Alumnos.NOMBRE, alumno.nombre)
         put(DefinirDB.Alumnos.DOMICILIO, alumno.domicilio)
         put(DefinirDB.Alumnos.ESPECIALIDAD, alumno.especialidad)
         put(DefinirDB.Alumnos.FOTO, alumno.foto)
      }
      return db.update(DefinirDB.Alumnos.TABLA, values, "${DefinirDB.Alumnos.ID} = $id", null)
   }

   fun actualizarAlumno(alumno: Alumno, matricula: String): Int {
      val values = ContentValues().apply {
         put(DefinirDB.Alumnos.MATRICULA, alumno.matricula)
         put(DefinirDB.Alumnos.NOMBRE, alumno.nombre)
         put(DefinirDB.Alumnos.DOMICILIO, alumno.domicilio)
         put(DefinirDB.Alumnos.ESPECIALIDAD, alumno.especialidad)
         put(DefinirDB.Alumnos.FOTO, alumno.foto)
      }
      return db.update(DefinirDB.Alumnos.TABLA, values, "${DefinirDB.Alumnos.MATRICULA} = $matricula", null)
   }

   fun borrarAlumno(id: Int): Int {
      return db.delete(DefinirDB.Alumnos.TABLA, "${DefinirDB.Alumnos.ID} = ?", arrayOf(id.toString()))
   }


   fun borrarAlumno(id: String): Int {
      return db.delete(DefinirDB.Alumnos.TABLA, "${DefinirDB.Alumnos.MATRICULA} = ?", arrayOf(id))
   }

   fun mostrarAlumnos(cursor: Cursor): Alumno {
      return Alumno().apply {
         id = cursor.getInt(0)
         matricula = cursor.getString(1)
         nombre = cursor.getString(2)
         domicilio = cursor.getString(3)
         especialidad = cursor.getString(4)
         foto = cursor.getString(5)
      }
   }

   fun getAlumno(id: Long): Alumno {
      val db = dbHelper.readableDatabase
      val cursor = db.query(DefinirDB.Alumnos.TABLA, leerRegistro,
         "${DefinirDB.Alumnos.ID} = ?",
         arrayOf(id.toString()), null, null, null)
      cursor.moveToFirst()
      val alumno = mostrarAlumnos(cursor)
      cursor.close()
      return alumno
   }


   fun getAlumno(id: String): Alumno {
      val db = dbHelper.readableDatabase

      val cursor = db.query(DefinirDB.Alumnos.TABLA, leerRegistro,
         "${DefinirDB.Alumnos.MATRICULA} = ?",
         arrayOf(id), null, null, null)

      val alumno: Alumno
      if (cursor.moveToFirst()) {
         alumno = mostrarAlumnos(cursor)
      } else {
         alumno = Alumno() // or handle as needed
      }
      cursor.close()

      return alumno
   }

   fun leerTodos(): ArrayList<Alumno> {
      val cursor = db.query(DefinirDB.Alumnos.TABLA, leerRegistro, null, null, null, null, null)
      val listaAlumno = ArrayList<Alumno>()
      cursor.moveToFirst()

      while (!cursor.isAfterLast) {
         val alumno = mostrarAlumnos(cursor)
         listaAlumno.add(alumno)
         cursor.moveToNext()
      }
      cursor.close()
      return listaAlumno
   }

   fun matriculaExiste(matricula: String): Boolean {
      val query = "SELECT COUNT(*) FROM ${DefinirDB.Alumnos.TABLA} WHERE ${DefinirDB.Alumnos.MATRICULA} = ?"
      val selectionArgs = arrayOf(matricula)

      val cursor = db.rawQuery(query, selectionArgs)
      cursor.use {
         it.moveToFirst()
         val count = it.getInt(0)
         return count > 0
      }
   }

   fun close() {
      dbHelper.close()
   }
}