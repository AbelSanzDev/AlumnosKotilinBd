package com.sanchezurrea.appmenubutton

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sanchezurrea.appmenubutton.database.Alumno
import com.example.appmenubutton.R
import com.google.android.material.imageview.ShapeableImageView

class AlumnosAdapter(
    private var alumnosList: List<Alumno>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<AlumnosAdapter.AlumnoViewHolder>(), Filterable {

    private var filteredAlumnosList: List<Alumno> = alumnosList

    interface OnItemClickListener {
        fun onItemClick(alumno: Alumno)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlumnoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alumn_item, parent, false)
        return AlumnoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlumnoViewHolder, position: Int) {
        holder.bind(filteredAlumnosList[position], itemClickListener)
    }

    override fun getItemCount(): Int {
        return filteredAlumnosList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.replace(" ", "")?.lowercase()?.trim() ?: ""
                filteredAlumnosList = if (query.isEmpty()) {
                    alumnosList
                } else {
                    alumnosList.filter {
                        it.nombre.replace(" ", "").lowercase().contains(query) ||
                                it.especialidad.replace(" ", "").lowercase().contains(query)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredAlumnosList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredAlumnosList = results?.values as List<Alumno>? ?: alumnosList
                notifyDataSetChanged()
            }
        }
    }

    inner class AlumnoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val foto: ShapeableImageView = itemView.findViewById(R.id.foto)
        private val nombre: TextView = itemView.findViewById(R.id.txtAlumnoNombre)
        private val carrera: TextView = itemView.findViewById(R.id.txtAlumnoCarrera)
        private val matricula: TextView = itemView.findViewById(R.id.txtMatricula)

        fun bind(alumno: Alumno, clickListener: OnItemClickListener) {
            nombre.text = alumno.nombre
            carrera.text = alumno.especialidad
            matricula.text = alumno.matricula.toString()

            // Load the photo using Glide
            Glide.with(foto.context)
                .load(alumno.foto)
                .placeholder(R.mipmap.foto) // Show a placeholder image if the photoUrl is not available
                .error(R.mipmap.ewwow) // Show an error image if the load fails
                .into(foto)

            itemView.setOnClickListener {
                clickListener.onItemClick(alumno)
            }
        }
    }
}
