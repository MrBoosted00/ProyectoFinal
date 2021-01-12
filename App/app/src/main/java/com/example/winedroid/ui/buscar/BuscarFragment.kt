package com.example.winedroid.ui.buscar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.winedroid.R
import com.example.winedroid.ui.fichavino.Comentario
import com.example.winedroid.ui.fichavino.Vino
import com.google.firebase.database.*


/**
 * A fragment representing a list of Items.
 */
class BuscarFragment : Fragment() {

    private var columnCount = 1

    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var listaVinos: ArrayList<Vino>
    private lateinit var btnBuscar: Button
    private lateinit var root: View
    private lateinit var etTexto: EditText
    private lateinit var recyclerView: RecyclerView
    private var busqueda: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_buscar_list, container, false)
        iniciarVista(root)
        return root
    }

    private fun iniciarVista(root: View) {
        recyclerView = root.findViewById(R.id.list)
        btnBuscar = root.findViewById(R.id.btnBuscarBusqueda)
        etTexto = root.findViewById(R.id.etBuscarTexto)
        database =
            FirebaseDatabase.getInstance("https://winedroid-ca058-default-rtdb.europe-west1.firebasedatabase.app/")

        databaseReference = database.reference.child("Vinos")
        btnBuscar.setOnClickListener {
            busqueda = etTexto.text.toString()
            if (!busqueda.equals("")) {
                buscarVinoPorNombre()
            } else {
                buscarVino()
            }
        }
    }

    private fun buscarVinoPorNombre() {
        listaVinos = ArrayList()
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val nickname = it.child("nombre").value.toString()
                    if (nickname.toLowerCase().contains(busqueda!!.toLowerCase())) {
                        val vino = Vino(
                            nickname,
                            it.child("descripcion").value.toString(),
                            it.child("imagen").value.toString(),
                            it.child("valoracion").value.toString().toInt(),
                            it.child("denominacion").value.toString(),
                            it.child("listaComentarios").value as java.util.ArrayList<Comentario>?
                        )
                        listaVinos.add(vino)
                    }
                    with(recyclerView) {
                        (recyclerView as RecyclerView).layoutManager = when {
                            columnCount <= 1 -> LinearLayoutManager(context)
                            else -> GridLayoutManager(context, columnCount)
                        }
                        // Lo cargamos
                        val fm = fragmentManager
                        (recyclerView as RecyclerView).adapter =
                            VinoAdapter(listaVinos, fm!!)
                    }
                }
            }
        })
    }

    private fun buscarVino() {
        listaVinos = ArrayList()
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val nickname = it.child("nombre").value.toString()
                    val vino = Vino(
                        nickname,
                        it.child("descripcion").value.toString(),
                        it.child("imagen").value.toString(),
                        it.child("valoracion").value.toString().toInt(),
                        it.child("denominacion").value.toString(),
                        it.child("listaComentarios").value as java.util.ArrayList<Comentario>?
                    )
                    listaVinos.add(vino)
                    with(recyclerView) {
                        (recyclerView as RecyclerView).layoutManager = when {
                            columnCount <= 1 -> LinearLayoutManager(context)
                            else -> GridLayoutManager(context, columnCount)
                        }
                        // Lo cargamos
                        val fm = fragmentManager
                        (recyclerView as RecyclerView).adapter =
                            VinoAdapter(listaVinos, fm!!)
                    }
                }
            }
        })
    }
}