package com.example.winedroid.ui.perfil

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
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
    private lateinit var btnGuardar: Button
    private lateinit var etDescripcion: EditText
    private lateinit var etNick: EditText
    private lateinit var ivImagen: ImageView
    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private var photoUri: Uri? = null
    private val GALERIA = 50
    private val CAMARA = 51

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

    fun iniciarVista(root: View) {
        etDescripcion = root.findViewById(R.id.etEpDescripcion)
        etNick = root.findViewById(R.id.etEpNick)
        btnGuardar = root.findViewById(R.id.btnEpGuardar)
        ivImagen = root.findViewById(R.id.ivEpFoto)
        etNick.setText(user?.getNick())
        etDescripcion.setText(user?.getDescripcion())
        if (!user?.getFotoPerfil().equals("null")) {
            Picasso.get().load(user?.getFotoPerfil()).into(ivImagen)
        }
        database =
            FirebaseDatabase.getInstance("https://winedroid-ca058-default-rtdb.europe-west1.firebasedatabase.app/")
        //Reference que utilizaremos para escribir en la ruta especificada
        dbReference = database.getReference("Usuarios")

        btnGuardar.setOnClickListener(View.OnClickListener {
            subirImagenAFirebase()
        })

        ivImagen.setOnClickListener {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(activity?.checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED
                    || activity?.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    val permisos = arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permisos,CAMARA)
                }else{
                    mostrarDialogoFoto()
                }
            }else
                mostrarDialogoFoto()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == CAMARA){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                seleccionarImagenCamara()
            else
                Toast.makeText(activity?.baseContext,"Debes dar acceso a la camara",Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarUsuario(fotoUrl: String) {
        val fUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        val currentUserDb = dbReference.child(fUser.uid)
        user = Usuario(etNick.text.toString(), etDescripcion.text.toString(), fotoUrl)
        currentUserDb.setValue(user)
        Toast.makeText(activity?.baseContext, "Cambios Guardados", Toast.LENGTH_SHORT).show()
        val fm = fragmentManager
        val perfil = PerfilFragment()
        val transaction = fm!!.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, perfil)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun seleccionarImagenGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALERIA)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALERIA && resultCode == Activity.RESULT_OK && data != null) {
            photoUri = data.data!!

            val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, photoUri)

            ivImagen.setImageBitmap(bitmap)
        }

        if (requestCode == CAMARA && resultCode == Activity.RESULT_OK && data != null) {

            ivImagen.setImageURI(photoUri)
        }
    }

    private fun mostrarDialogoFoto() {
        val fotoDialogo =
            AlertDialog.Builder(context)
        fotoDialogo.setTitle("Seleccionar Acción")
        val fotoDialogoItems = arrayOf(
            "Seleccionar fotografía de galería",
            "Capturar fotografía desde la cámara"
        )
        fotoDialogo.setItems(
            fotoDialogoItems
        ) { dialog, which ->
            when (which) {
                0 -> seleccionarImagenGaleria()
                1 -> seleccionarImagenCamara()
            }
        }
        fotoDialogo.show()
    }

    private fun subirImagenAFirebase() {
        if (photoUri == null) {
            guardarUsuario(user!!.getFotoPerfil())
        } else {
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/imagenes/$filename")

            ref.putFile(photoUri!!).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    guardarUsuario(it.toString())
                }
            }
        }
    }

    private fun seleccionarImagenCamara() {
        val value = ContentValues()
        value.put(MediaStore.Images.Media.TITLE, "Imagen")
        photoUri =
            activity?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, value)
        val camaraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        camaraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        startActivityForResult(camaraIntent, CAMARA)
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