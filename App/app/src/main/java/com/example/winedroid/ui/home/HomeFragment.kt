package com.example.winedroid.ui.home

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.PermissionRequest
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.winedroid.R
import com.example.winedroid.ui.buscar.HomeAdapter
import com.example.winedroid.ui.buscar.VinoAdapter
import com.example.winedroid.ui.fichavino.Comentario
import com.example.winedroid.ui.fichavino.Vino
import com.google.firebase.database.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.util.ArrayList

class HomeFragment : Fragment() {

    private var columnCount = 1
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    lateinit var lista_vinos: ArrayList<Vino>
    lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        pedirMultiplesPermisos()
        iniciarVista(root)

        return root
    }

    private fun iniciarVista(root : View){
        lista_vinos = ArrayList()
        database =
            FirebaseDatabase.getInstance("https://winedroid-ca058-default-rtdb.europe-west1.firebasedatabase.app/")

        databaseReference = database.reference.child("Vinos")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach() {
                    val nickname = it.child("nombre").value.toString()
                    val vino = Vino(
                        nickname,
                        it.child("descripcion").value.toString(),
                        it.child("imagen").value.toString(),
                        it.child("valoracion").value.toString().toInt(),
                        it.child("denominacion").value.toString(),
                        it.child("listaComentarios").value as ArrayList<Comentario>?
                    )
                    lista_vinos.add(vino)

                    if (root is RecyclerView) {
                        with(root) {
                            (root as RecyclerView).layoutManager = when {
                                columnCount <= 1 -> LinearLayoutManager(context)
                                else -> GridLayoutManager(context, columnCount)
                            }
                            // Lo cargamos
                            val fm = fragmentManager
                            (root as RecyclerView).adapter =
                                HomeAdapter(lista_vinos,fm!!)
                        }
                    }
                }
            }
        })
    }

    private fun pedirMultiplesPermisos() {
        // Indicamos el permisos y el manejador de eventos de los mismos
        Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // ccomprbamos si tenemos los permisos de todos ellos
                    if (report.areAllPermissionsGranted()) {
                        //Toast.makeText(getContext(), "¡Todos los permisos concedidos!", Toast.LENGTH_SHORT).show();
                    }

                    // comprobamos si hay un permiso que no tenemos concedido ya sea temporal o permanentemente
                    if (report.isAnyPermissionPermanentlyDenied()) {
                        // abrimos un diálogo a los permisos
                        //openSettingsDialog();
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                     token?.continuePermissionRequest()
                }
            }).withErrorListener(object : PermissionRequestErrorListener {
                override fun onError(error: DexterError?) {

                }
            })
            .onSameThread()
            .check()
    }
}