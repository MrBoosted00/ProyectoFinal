package com.example.winedroid.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.winedroid.ui.forgotpass.ForgotPasswordActivity
import com.example.winedroid.MainActivity
import com.example.winedroid.R
import com.example.winedroid.ui.perfil.Usuario
import com.example.winedroid.ui.register.RegisterActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.properties.Delegates

class LoginActivity : AppCompatActivity() {
    //global variables
    private val GOOGLE_SIGN_IN = 100
    private var email by Delegates.notNull<String>()
    private var password by Delegates.notNull<String>()
    private lateinit var txtEmail: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnLoginG: Button
    private lateinit var btnRegistro: Button
    private lateinit var txtForgotPass: TextView
    private lateinit var txtPass: EditText
    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase


    //Creamos nuestra variable de autenticación firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var user : FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        iniciarVista()
    }

    /*Creamos un método para inicializar nuestros elementos del diseño y la autenticación de firebase*/
    private fun iniciarVista() {
        if (comprobarSession()) {
            goHome()
        } else {
            txtEmail = findViewById(R.id.txtEmail)
            txtPass = findViewById(R.id.txtPass)
            btnLogin = findViewById(R.id.btnLogin)
            btnLoginG = findViewById(R.id.btnLoginG)
            btnRegistro = findViewById(R.id.btnRegistro)
            txtForgotPass = findViewById(R.id.txtForgotPassword)
            mAuth = FirebaseAuth.getInstance()
            database =
                FirebaseDatabase.getInstance("https://winedroid-ca058-default-rtdb.europe-west1.firebasedatabase.app/")
            dbReference = database.getReference("Usuarios")

            // OnClick botones
            btnLogin.setOnClickListener(View.OnClickListener {
                loginUser()
            })
            btnLoginG.setOnClickListener(View.OnClickListener {
                loginGoogle()
            })
            btnRegistro.setOnClickListener(View.OnClickListener {
                startActivity(Intent(this, RegisterActivity::class.java))
            })
            txtForgotPass.setOnClickListener(View.OnClickListener {
                startActivity(Intent(this, ForgotPasswordActivity::class.java))
            })
        }
    }


    //Iniciar sesion con email y pass
    private fun loginUser() {
        //Obtenemos usuario y contraseña
        email = txtEmail.text.toString()
        password = txtPass.text.toString()
        //Verificamos que los campos no este vacios
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            //Iniciamos sesión con el método signIn y enviamos usuario y contraseña
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {
                    //Verificamos que la tarea se ejecutó correctamente
                        task ->
                    if (task.isSuccessful) {
                        guardarDatosUser()
                        // Si se inició correctamente la sesión vamos a la vista Home de la aplicación
                        goHome() // Creamos nuestro método en la parte de abajo
                    } else {
                        // sino le avisamos el usuairo que orcurrio un problema
                        Toast.makeText(
                            this, "Ha fallado la autenticacion",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Toast.makeText(this, "Introduzca los campos", Toast.LENGTH_SHORT).show()
        }
    }

    //Inicio de session mediante google client
    private fun loginGoogle() {
        val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        val googleClient = GoogleSignIn.getClient(this, googleConf)
        googleClient.signOut()

        startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Request code del inicio de google
        if (requestCode == GOOGLE_SIGN_IN && resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credencial = GoogleAuthProvider.getCredential(account.idToken, null)
                    mAuth.signInWithCredential(credencial).addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            email = account.email ?: ""
                            val user: FirebaseUser = mAuth.currentUser!!
                            val photo = user.photoUrl
                            val currentUserDb = dbReference.child(user.uid)
                            val usuario = Usuario(user.displayName, getString(R.string.descripcion_predeterminada), photo.toString())
                            currentUserDb.setValue(usuario)
                            guardarDatosUser()
                            goHome()
                        } else {
                            Toast.makeText(
                                this, "Ha fallado la autenticacion",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } catch (e: ApiException) {
                // sino le avisamos el usuairo que orcurrio un problema
                Toast.makeText(
                    this, "hola amigo",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    //Metodo para guardar datos en preferencias y que se inicie sesion automaticamente
    private fun guardarDatosUser() {
        val prefs =
            getSharedPreferences(getString(R.string.file_prefs), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.apply()
    }

    //Funcion para comprobar si existe una session iniciada
    private fun comprobarSession(): Boolean {
        //Comprobamos si existe una sesion iniciada
        val prefs = getSharedPreferences(getString(R.string.file_prefs), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        return email != null
    }

    //Nos lleva al home
    private fun goHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }
}
