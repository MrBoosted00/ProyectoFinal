package com.example.winedroid.ui.fichavino

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.winedroid.R
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FichaVinoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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
    private lateinit var dbReferenceComentarios: DatabaseReference
    private lateinit var listaComentario: ArrayList<Comentario>
    private lateinit var lista: ArrayList<String>
    lateinit var lista_ids: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            vino = it.getSerializable("vino") as Vino?
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_ficha_vino, container, false)
        tvNombre = root.findViewById(R.id.tvVisualizarNombre)
        ivImagen = root.findViewById(R.id.ivVisualizarImagen)
        tvDescripcion = root.findViewById(R.id.tvVisualizarDescripcion)
        tvValor = root.findViewById(R.id.tvVisualizarValor)
        rbRating = root.findViewById(R.id.rbVisualizarRating)
        tvDenominacion = root.findViewById(R.id.tvVisualizarDenominacion)
        rvComentario = root.findViewById(R.id.rvVisualizarComentario)

        tvNombre.text = "Nombre: " + vino?.nombre
        Picasso.get().load(vino?.imagen).into(ivImagen)
        tvDescripcion.text = "Descripcion: " + vino?.descripcion
        rbRating.rating = vino?.valoracion!!.toFloat()
        tvDenominacion.text = "Denominacion: " + vino?.denominacion
        listaComentario = ArrayList()

        lista = ArrayList()

        sacarComentarios()
        /*(rvComentario as RecyclerView).adapter =
            ComentarioAdapter(listaComentario)*/

        return root
    }

    fun sacarComentarios() {
        database =
            FirebaseDatabase.getInstance("https://winedroid-ca058-default-rtdb.europe-west1.firebasedatabase.app/")
        dbReference = database.reference.child("Vinos")
            val refUser = dbReference.child(vino!!.nombre).child("listaComentarios")
            refUser.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach(){
                        val uid = it.child("uid").value.toString()
                        val texto = it.child("comentario").value.toString()
                        val valoracion = it.child("valoracion").value.toString()
                        val comentario = Comentario(uid, texto, valoracion.toInt())
                        listaComentario.add(comentario)
                    }
                    (rvComentario as RecyclerView).adapter =
                        ComentarioAdapter(listaComentario)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
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