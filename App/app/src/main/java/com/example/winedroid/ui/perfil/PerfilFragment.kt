package com.example.winedroid.ui.perfil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.winedroid.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class PerfilFragment : Fragment() {
    private lateinit var ivFoto: ImageView
    private lateinit var tvNick: TextView
    private lateinit var tvDescipcion: TextView
    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var btnGenerarQr: Button
    private lateinit var btnEditarPerfil: Button
    private lateinit var usuario : Usuario
    private var imgUrl : String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_perfil, container, false)
        iniciarVista(root)
        return root
    }
    fun iniciarVista(root: View) {
        ivFoto = root.findViewById(R.id.ivPerfilFoto)
        tvNick = root.findViewById(R.id.tvPerfilNick)
        tvDescipcion = root.findViewById(R.id.tvPerfilDescripcion)
        btnGenerarQr = root.findViewById(R.id.btnPerfilGenerarQr)
        btnEditarPerfil = root.findViewById(R.id.btnPerfilEditar)
        //CODIGO PARA SACAR ALGO(en este caso nick) DE LA BASE DE DATOS
        database = FirebaseDatabase.getInstance("https://winedroid-ca058-default-rtdb.europe-west1.firebasedatabase.app/")
        dbReference = database.reference.child("Usuarios")
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid
        val refUser = dbReference.child(userId!!)
        refUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tvNick.text = snapshot.child("nick").value.toString()
                tvDescipcion.text = snapshot.child("descripcion").value.toString()
                imgUrl = snapshot.child("fotoPerfil").value.toString()
                if(!imgUrl.equals("null")){
                    Picasso.get().load(imgUrl).into(ivFoto)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })




        btnEditarPerfil.setOnClickListener(View.OnClickListener {
            editarPerfil(root)
        })

    }

    private fun editarPerfil(root: View){
        usuario = Usuario(tvNick.text.toString(),tvDescipcion.text.toString(),imgUrl)
        val fm = fragmentManager
        val ep: EditarPerfilFragment = EditarPerfilFragment.newInstance(usuario)
        val transaction = fm!!.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, ep)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}