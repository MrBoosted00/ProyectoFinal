package com.example.winedroid.ui.fichavino

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.winedroid.R

class ComentarioAdapter(
    private val values: ArrayList<Comentario>
): RecyclerView.Adapter<ComentarioAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_comentario, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvComentarioNombre)
        //val ivImagen: ImageView = view.findViewById(R.id.ivBuscarImagen)
        val tvDescripcion: TextView = view.findViewById(R.id.tvComentarioComent)

        override fun toString(): String {
            return super.toString() + " '" + tvDescripcion.text + "'"
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.tvNombre.text = item.uid
        //Picasso.get().load(item.imagen).resize(150,150).into(holder.ivImagen)
        holder.tvDescripcion.text = item.comentario
    }
}