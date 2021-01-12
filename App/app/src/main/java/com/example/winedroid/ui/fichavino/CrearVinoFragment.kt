package com.example.winedroid.ui.fichavino

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.winedroid.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class CrearVinoFragment : Fragment() {
    private lateinit var ivImagen: ImageView
    private lateinit var etNombre: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etDenominacion: EditText
    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var etComentario: EditText
    private lateinit var tvValoracion: TextView
    private lateinit var rbRating: RatingBar
    private lateinit var btnGuardar: Button
    private var valoracion: Int = 0
    private var photoUri: Uri? = null
    private val GALERIA = 50
    private val CAMARA = 51
    private var vino: Vino? = null
    private var comen: Comentario? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_crear_vino, container, false)
        iniciarVista(root)
        return root
    }

    private fun iniciarVista(root: View) {
        ivImagen = root.findViewById(R.id.ivCrearImagen)
        etNombre = root.findViewById(R.id.etCrearNombre)
        etDescripcion = root.findViewById(R.id.etCrearDescripcion)
        etDenominacion = root.findViewById(R.id.etCrearDenominacion)
        etComentario = root.findViewById(R.id.etCrearComentario)
        tvValoracion = root.findViewById(R.id.tvCrearValor)
        rbRating = root.findViewById(R.id.rbCrearRating)
        btnGuardar = root.findViewById(R.id.btnCrearGuardar)

        database =
            FirebaseDatabase.getInstance("https://winedroid-ca058-default-rtdb.europe-west1.firebasedatabase.app/")
        //Reference que utilizaremos para escribir en la ruta especificada
        dbReference = database.getReference("Vinos")

        rbRating.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
                valoracion = rating.toInt()
            }

        ivImagen.setOnClickListener {
            mostrarDialogoFoto()
        }
        btnGuardar.setOnClickListener {
            subirImagenAFirebase()
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

    private fun seleccionarImagenGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALERIA)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALERIA && resultCode == Activity.RESULT_OK && data != null) {
            photoUri = data.data!!

            val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, photoUri)

            ivImagen.setImageBitmap(bitmap)
        }

        if (requestCode == CAMARA && resultCode == Activity.RESULT_OK && data != null) {
            val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, photoUri)

            ivImagen.setImageBitmap(bitmap)
        }
    }

    private fun subirImagenAFirebase() {
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/imagenes/$filename")

        ref.putFile(photoUri!!).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                guardarVino(it.toString())
            }
        }
    }

    private fun guardarVino(fotoUrl: String) {
        val vinoDb = dbReference.child(etNombre.text.toString())
        comen = Comentario(
            FirebaseAuth.getInstance().currentUser!!.uid,
            etComentario.text.toString(),
            valoracion
        )
        vino = Vino(
            etNombre.text.toString(),
            etDescripcion.text.toString(),
            fotoUrl,
            valoracion,
            etDenominacion.text.toString(),
            ArrayList()
        )
        vino!!.añadirComentario(comen)
        vinoDb.setValue(vino)
        Toast.makeText(activity?.baseContext, "Cambios Guardados", Toast.LENGTH_SHORT).show()
    }

}