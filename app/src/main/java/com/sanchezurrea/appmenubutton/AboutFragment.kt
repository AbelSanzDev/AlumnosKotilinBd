package com.sanchezurrea.appmenubutton

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sanchezurrea.appmenubutton.database.Alumno
import com.sanchezurrea.appmenubutton.database.dbAlumnos
import com.example.appmenubutton.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AboutFragment : Fragment(), AlumnosAdapter.OnItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AlumnosAdapter
    private lateinit var db: dbAlumnos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // handle arguments if needed
        }
        setHasOptionsMenu(true) // Enable options menu for this fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        // Set up the toolbar
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        // Initialize the RecyclerView
        recyclerView = view.findViewById(R.id.recId)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Initialize the database and adapter
        db = dbAlumnos(requireContext())
        db.openDatabase()
        adapter = AlumnosAdapter(db.leerTodos(), this)
        recyclerView.adapter = adapter

        // Set up the floating action button
        val fab: FloatingActionButton = view.findViewById(R.id.fab)
        fab.setOnClickListener {
            (activity as MainActivity).changeFrame(DbFragment())
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu) // Inflate your menu items

        // Find the search item in your menu
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter the data in the adapter based on the query
                adapter.filter.filter(newText)
                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater) // Call super after inflating
    }

    override fun onItemClick(alumno: Alumno) {
        val fragment = DbFragment.newInstance(alumno)
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.frmContenedor, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AboutFragment().apply {
                arguments = Bundle().apply {
                    // handle parameters if needed
                }
            }
    }
}
