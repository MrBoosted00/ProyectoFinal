package com.example.winedroid.ui.fichavino

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.winedroid.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class ComentarioAdapter(
    private val values: ArrayList<Comentario>
): RecyclerView.Adapter<ComentarioAdapter.ViewHolder>() {
    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_comentario, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvComentarioNombre)
        val ivImagen: ImageView = view.findViewById(R.id.ivComentarioImagen)
        val tvDescripcion: TextView = view.findViewById(R.id.tvComentarioComent)
        val rbRatingBar:RatingBar = view.findViewById(R.id.rbComentarioRating)

        override fun toString(): String {
            return super.toString() + " '" + tvDescripcion.text + "'"
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        database =
            FirebaseDatabase.getInstance("https://winedroid-ca058-default-rtdb.europe-west1.firebasedatabase.app/")
        dbReference = database.reference.child("Usuarios")
        val userId = item.uid
        val refUser = dbReference.child(userId!!)
        refUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                holder.tvNombre.text = snapshot.child("nick").value.toString()
                holder.tvDescripcion.text = item.comentario
                Picasso.get().load(snapshot.child("fotoPerfil").value.toString()).resize(150,150).into(holder.ivImagen)
                holder.rbRatingBar.rating = item.valoracion.toFloat()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}