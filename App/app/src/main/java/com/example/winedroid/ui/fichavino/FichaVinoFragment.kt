package com.example.winedroid.ui.fichavino

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.winedroid.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class FichaVinoFragment : Fragment() {
    private var columnCount = 1

    private var vino: Vino? = null
    private lateinit var tvNombre: TextView
    private lateinit var ivImagen: ImageView
    private lateinit var tvDescripcion: TextView
    private lateinit var tvValor: TextView
    private lateinit var rbRating: RatingBar
    private lateinit var tvDenominacion: TextView
    private lateinit var database: FirebaseDatabase
    private lateinit var dbReference: DatabaseReference
    private lateinit var rvComentario: RecyclerView
    private lateinit var etComentario: EditText
    private lateinit var btnComentar: Button
    private var comentario: Comentario? = null
    private lateinit var listaComentario: ArrayList<Comentario>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            vino = it.getSerializable("vino") as Vino?
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_ficha_vino, container, false)
        iniciarVista(root)
        return root
    }

    @SuppressLint("SetTextI18n")
    fun iniciarVista(root: View) {
        tvNombre = root.findViewById(R.id.tvVisualizarNombre)
        ivImagen = root.findViewById(R.id.ivVisualizarImagen)
        tvDescripcion = root.findViewById(R.id.tvVisualizarDescripcion)
        tvValor = root.findViewById(R.id.tvVisualizarValor)
        etComentario = root.findViewById(R.id.etVisualizarComentario)
        rbRating = root.findViewById(R.id.rbVisualizarRating)
        tvDenominacion = root.findViewById(R.id.tvVisualizarDenominacion)
        rvComentario = root.findViewById(R.id.rvVisualizarComentario)
        btnComentar = root.findViewById(R.id.btnVisualizarComentar)

        database =
            FirebaseDatabase.getInstance("https://winedroid-ca058-default-rtdb.europe-west1.firebasedatabase.app/")
        dbReference = database.reference.child("Vinos")

        tvNombre.text = "Nombre: " + vino?.nombre
        Picasso.get().load(vino?.imagen).into(ivImagen)
        tvDescripcion.text = "Descripcion: " + vino?.descripcion
        tvDenominacion.text = "Denominacion: " + vino?.denominacion
        btnComentar.setOnClickListener {
            guardarComentario()
        }
        recogerComentarios()
    }

    private fun recogerComentarios() {

        val refUser = dbReference.child(vino!!.nombre).child("listaComentarios")
        refUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaComentario = ArrayList()
                snapshot.children.forEach() {
                    val uid = it.child("uid").value.toString()
                    val texto = it.child("comentario").value.toString()
                    val valoracion = it.child("valoracion").value.toString()
                    comentario = Comentario(uid, texto, valoracion.toInt())
                    listaComentario.add(comentario!!)
                }
                recogerValoraciones(listaComentario)
                (rvComentario as RecyclerView).adapter =
                    ComentarioAdapter(listaComentario)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun guardarComentario() {
        comentario = Comentario(
            FirebaseAuth.getInstance().currentUser!!.uid,
            etComentario.text.toString(),
            rbRating.rating.toInt()
        )
        vino?.añadirComentario(comentario)
        val refUser = dbReference.child(vino!!.nombre).child("listaComentarios")
        refUser.setValue(vino!!.listaComentarios)
        Toast.makeText(activity?.baseContext, "Cambios Guardados", Toast.LENGTH_SHORT).show()
    }

    private fun recogerValoraciones(lista: ArrayList<Comentario>) {
        var valoraciones = 0
        for (i in 0 until lista.size) {
            valoraciones += listaComentario[i].valoracion
        }
        val valoracionVino = valoraciones.div(lista.size)
        rbRating.rating = valoracionVino.toFloat()
        val refUser = dbReference.child(vino!!.nombre).child("valoracion")
        refUser.setValue(valoracionVino)
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(vino: Vino) =
            FichaVinoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("vino", vino)
                }
            }
    }
}