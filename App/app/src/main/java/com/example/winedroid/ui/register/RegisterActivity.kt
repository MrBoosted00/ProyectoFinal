package com.example.winedroid.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.winedroid.R
import com.example.winedroid.ui.login.LoginActivity
import com.example.winedroid.ui.perfil.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.properties.Delegates

class RegisterActivity : AppCompatActivity() {
    private lateinit var txtName: EditText
    private lateinit var txtLastName: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var txtConfPassword: EditText
    private lateinit var btnRegistro: Button
    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    //global variables
    private var firstName by Delegates.notNull<String>()
    private var lastName by Delegates.notNull<String>()
    private var email by Delegates.notNull<String>()
    private var password by Delegates.notNull<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        iniciarVista()
    }

/*Creamos un método para inicializar nuestros elementos del diseño y la autenticación y la base de datos de firebase*/

    private fun iniciarVista() {
        //llamamos nuestras vista
        txtName = findViewById(R.id.txtName)
        txtLastName = findViewById(R.id.txtLastName)
        txtEmail = findViewById(R.id.txtEmail)
        txtPassword = findViewById(R.id.txtPassword)
        txtConfPassword = findViewById(R.id.txtConfirmarPass)
        btnRegistro = findViewById(R.id.btnRegisterRegistro)
        //Creamos una instancia para guardar datos en la bd
        database =
            FirebaseDatabase.getInstance("https://winedroid-ca058-default-rtdb.europe-west1.firebasedatabase.app/")
        //Creamos una instancia de auth para guardar al usuario
        auth = FirebaseAuth.getInstance()
        //Reference que utilizaremos para escribir en la ruta especificada
        dbReference = database.getReference("Usuarios")

        btnRegistro.setOnClickListener(View.OnClickListener {
            createNewAccount()
        })
    }

    //Vamos a crear nuestro método para crear una nueva cuenta
    private fun createNewAccount() {
        //Obtenemos los datos de nuestras cajas de texto
        firstName = txtName.text.toString()
        lastName = txtLastName.text.toString()
        email = txtEmail.text.toString()
        password = txtPassword.text.toString()
        //Verificamos que los campos estén llenos
        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)
            && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)
        ) {
            if (password.length >= 6) {
                if (txtConfPassword.text.toString().equals(password)) {
                    Toast.makeText(
                        this, "Registrando Usuario",
                        Toast.LENGTH_SHORT
                    ).show()
                    //vamos a dar de alta el usuario con el correo y la contraseña
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) {
                            if(it.isSuccessful) {
                                val user: FirebaseUser = auth.currentUser!!
                                val currentUserDb = dbReference.child(user.uid)
                                val usuario = Usuario("$firstName $lastName",getString(R.string.descripcion_predeterminada),getString(R.string.foto_predeterminada))
                                currentUserDb.setValue(usuario)
                                //Por último nos vamos a la vista home
                                updateUserInfoAndGoHome()
                            }
                        }.addOnFailureListener {
                            // si el registro falla se mostrara este mensaje
                            Toast.makeText(
                                this, "El email introducido ya esta siendo utilizado",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    txtPassword.text.clear()
                    txtConfPassword.text.clear()
                    Toast.makeText(
                        this, "La contraseñas no coinciden",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this, "La contraseña debe contener 6 o mas caracteres",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(this, "Llene todos los campos", Toast.LENGTH_SHORT).show()
        }
    }
    //Esta funcion nos envia a home
    private fun updateUserInfoAndGoHome() {
        //Nos vamos a la actividad home
        Toast.makeText(this, "Usuario registrado", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)

    }

    //Metodo para verificar Email actualmente en desuso
    private fun verifyEmail(user: FirebaseUser) {
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Email " + user.getEmail(),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Error al verificar el correo ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}