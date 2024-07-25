package com.sanchezurrea.appmenubutton

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.sanchezurrea.appmenubutton.database.Alumno
import com.sanchezurrea.appmenubutton.database.dbAlumnos
import com.example.appmenubutton.R

private const val ARG_ALUMNO = "alumno"
private const val PICK_IMAGE_REQUEST = 1
private const val REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 2

class DbFragment : Fragment(R.layout.fragment_db) {

    private var alumno: Alumno? = null
    private lateinit var db: dbAlumnos
    private lateinit var btnGuardar: Button
    private lateinit var btnBuscar: Button
    private lateinit var btnBorrar: Button
    private lateinit var btnSubirFoto: Button
    private lateinit var inMatricula: EditText
    private lateinit var inNombre: EditText
    private lateinit var inDomicilio: EditText
    private lateinit var inEspecialidad: EditText
    private lateinit var imgAlumno: ImageView
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            alumno = it.getParcelable(ARG_ALUMNO)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnGuardar = view.findViewById(R.id.btnGuardar)
        btnBuscar = view.findViewById(R.id.btnBuscar)
        btnBorrar = view.findViewById(R.id.btnBorrar)
        btnSubirFoto = view.findViewById(R.id.btnSubirFoto)
        inMatricula = view.findViewById(R.id.inMatricula)
        inNombre = view.findViewById(R.id.inNombre)
        inDomicilio = view.findViewById(R.id.inDomicilio)
        inEspecialidad = view.findViewById(R.id.inEspecialidad)
        imgAlumno = view.findViewById(R.id.imgAlumno)

        alumno?.let {
            // Pre-fill form with alumno data
            inMatricula.setText(it.matricula)
            inNombre.setText(it.nombre)
            inDomicilio.setText(it.domicilio)
            inEspecialidad.setText(it.especialidad)
            loadAlumnoImage(it.foto)
            btnBorrar.isEnabled = true
        } ?: run {
            btnBorrar.isEnabled = false
        }

        btnSubirFoto.setOnClickListener {

                openImageChooser()

        }

        btnGuardar.setOnClickListener {
            if (validateInputs()) {
                saveOrUpdateAlumno()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Faltó información por capturar",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btnBuscar.setOnClickListener {
            val matricula = inMatricula.text.toString()
            if (matricula.isNotEmpty()) {
                searchAlumno(matricula)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Faltó capturar matrícula",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btnBorrar.setOnClickListener {
            val matricula = inMatricula.text.toString()
            if (matricula.isNotEmpty()) {
                confirmDeleteAlumno(matricula)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Faltó capturar matrícula",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun validateInputs(): Boolean {
        return inNombre.text.isNotEmpty() &&
                inDomicilio.text.isNotEmpty() &&
                inMatricula.text.isNotEmpty() &&
                inEspecialidad.text.isNotEmpty()
    }

    private fun saveOrUpdateAlumno() {
        db = dbAlumnos(requireContext())
        db.openDatabase()

        val isRegistered = db.matriculaExiste(inMatricula.text.toString())
        val currentAlumno = if (isRegistered) db.getAlumno(inMatricula.text.toString()) else null

        val newImageUrl = imageUri?.toString()
        val updatedImageUrl = newImageUrl ?: currentAlumno?.foto

        val alumno = Alumno().apply {
            nombre = inNombre.text.toString()
            matricula = inMatricula.text.toString()
            domicilio = inDomicilio.text.toString()
            especialidad = inEspecialidad.text.toString()
            foto = updatedImageUrl
        }

        val msg: String
        if (isRegistered) {
            msg = "Estudiante con matrícula ${alumno.matricula} se actualizó."
            db.actualizarAlumno(alumno, alumno.matricula)
        } else {
            val id = db.insertarAlumno(alumno)
            msg = "Se agregó con éxito con ID $id"
            clearForm()
        }

        Toast.makeText(
            requireContext(),
            msg,
            Toast.LENGTH_SHORT
        ).show()

        db.close()
    }

    private fun searchAlumno(matricula: String) {
        db = dbAlumnos(requireContext())
        db.openDatabase()

        val alumno = db.getAlumno(matricula)
        if (alumno.id != 0) {
            inNombre.setText(alumno.nombre)
            inDomicilio.setText(alumno.domicilio)
            inEspecialidad.setText(alumno.especialidad)
            loadAlumnoImage(alumno.foto)
            btnBorrar.isEnabled = true
        } else {
            Toast.makeText(
                requireContext(),
                "No se encontró la matrícula",
                Toast.LENGTH_SHORT
            ).show()
        }

        db.close()
    }

    private fun confirmDeleteAlumno(matricula: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmación")
        builder.setMessage("¿Está seguro de que desea borrar al alumno con matrícula $matricula?")
        builder.setPositiveButton("Sí") { dialog, _ ->
            deleteAlumno(matricula)
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }

    private fun deleteAlumno(matricula: String) {
        db = dbAlumnos(requireContext())
        db.openDatabase()

        val status = db.borrarAlumno(matricula)
        if (status != 0) {
            clearForm()
            Toast.makeText(
                requireContext(),
                "Se borró el usuario con la matrícula $matricula",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                requireContext(),
                "No se encontró la matrícula",
                Toast.LENGTH_SHORT
            ).show()
        }

        db.close()
    }

    private fun clearForm() {
        inMatricula.setText("")
        inNombre.setText("")
        inDomicilio.setText("")
        inEspecialidad.setText("")
        imgAlumno.setImageResource(R.mipmap.foto)
        imageUri = null
        btnBorrar.isEnabled = false
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            loadSelectedImage()
        }
    }

    private fun loadSelectedImage() {
        Glide.with(this)
            .load(imageUri)
            .placeholder(R.mipmap.foto)
            .error(R.mipmap.foto)
            .into(imgAlumno)
    }

    private fun loadAlumnoImage(imageUrl: String?) {
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.mipmap.foto)
                .error(R.mipmap.foto)
                .into(imgAlumno)
        } else {
            imgAlumno.setImageResource(R.mipmap.foto)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL_STORAGE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                openImageChooser()
            } else {
                Toast.makeText(requireContext(), "Permiso denegado para acceder a archivos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(alumno: Alumno) =
            DbFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_ALUMNO, alumno)
                }
            }
    }
}
