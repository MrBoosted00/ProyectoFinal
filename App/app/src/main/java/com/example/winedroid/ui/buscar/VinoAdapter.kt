package com.example.winedroid.ui.buscar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.winedroid.R
import com.example.winedroid.ui.fichavino.Vino
import com.example.winedroid.ui.perfil.PerfilFragment
import com.squareup.picasso.Picasso

/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class VinoAdapter(
    private val values: List<Vino>,private val fm: FragmentManager
 ): RecyclerView.Adapter<VinoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.tvNombre.text = item.nombre
        Picasso.get().load(item.imagen).resize(150,150).into(holder.ivImagen)
        holder.tvDescripcion.text = item.descripcion
        holder.rlRelative.setOnClickListener(View.OnClickListener {
            val detalle = PerfilFragment()
            val transaction: FragmentTransaction = fm!!.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, detalle)
            transaction.addToBackStack(null)
            transaction.commit()
        })
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvBuscarNombre)
        val ivImagen: ImageView = view.findViewById(R.id.ivBuscarImagen)
        val tvDescripcion: TextView = view.findViewById(R.id.tvBuscarDescripcion)
        val rlRelative: RelativeLayout = view.findViewById(R.id.rlBuscarRelative)

        override fun toString(): String {
            return super.toString() + " '" + tvDescripcion.text + "'"
        }
    }
}