package com.example.winedroid.ui.perfil

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.winedroid.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.*

class EditarPerfilFragment : Fragment() {

    private var user: Usuario? = null
    private lateinit var btnGuardar : Button
    private lateinit var etDescripcion : EditText
    private lateinit var etNick : EditText
    private lateinit var ivImagen : ImageView
    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getSerializable("user") as Usuario?
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_editar_perfil, container, false)
        iniciarVista(root)
        return root

    }

    fun iniciarVista(root:View){
        etDescripcion = root.findViewById(R.id.etEpDescripcion)
        etNick = root.findViewById(R.id.etEpNick)
        btnGuardar = root.findViewById(R.id.btnEpGuardar)
        ivImagen = root.findViewById(R.id.ivEpFoto)
        etNick.setText(user?.getNick())
        etDescripcion.setText(user?.getDescripcion())
        Picasso.get().load(user?.getFotoPerfil()).into(ivImagen)
        database = FirebaseDatabase.getInstance("https://winedroid-ca058-default-rtdb.europe-west1.firebasedatabase.app/")
        //Reference que utilizaremos para escribir en la ruta especificada
        dbReference = database.getReference("Usuarios")

        btnGuardar.setOnClickListener(View.OnClickListener {
            subirImagenAFirebase()
        })

        ivImagen.setOnClickListener {
            seleccionarImagen()
        }
    }

    private fun guardarUsuario(fotoUrl: String){
        val fUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        val currentUserDb = dbReference.child(fUser.uid)
        user = Usuario(etNick.text.toString(),etDescripcion.text.toString(),fotoUrl)
        currentUserDb.setValue(user)
        Toast.makeText(activity?.baseContext, "Cambios Guardados", Toast.LENGTH_SHORT).show()
        val fm = fragmentManager
        val perfil = PerfilFragment()
        val transaction = fm!!.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, perfil)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun seleccionarImagen(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,0)
    }
    var photoUri : Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            photoUri = data.data!!

            val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver,photoUri)

            ivImagen.setImageBitmap(bitmap)
        }
    }

    private fun subirImagenAFirebase(){
        if(photoUri == null){
            guardarUsuario(user!!.getFotoPerfil())
        }else {
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/imagenes/$filename")

            ref.putFile(photoUri!!).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    guardarUsuario(it.toString())
                }
            }
        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment EditarPerfilFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(user: Usuario) =
            EditarPerfilFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("user", user)
                }
            }
    }
}