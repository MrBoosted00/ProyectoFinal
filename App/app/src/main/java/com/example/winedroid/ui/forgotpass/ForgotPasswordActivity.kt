package com.example.winedroid.ui.forgotpass

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
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var btnSend: Button
    //Firebase references
    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        initialise()
    }
    private fun initialise() {
        etEmail = findViewById<View>(R.id.etEmail) as EditText
        btnSend = findViewById<View>(R.id.btnSend) as Button
        mAuth = FirebaseAuth.getInstance()
        btnSend.setOnClickListener(View.OnClickListener {
            sendPasswordResetEmail()
        })
    }
    //Funcion que envia un email para resetear la contraseña
    private fun sendPasswordResetEmail() {
        val email = etEmail.text.toString()
        if (!TextUtils.isEmpty(email)) {
            mAuth!!
                .sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Email Enviado", Toast.LENGTH_SHORT).show()
                        goLogin()
                    } else {
                        Toast.makeText(this, "No se encontró el usuario con este correo", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Agregue el correo", Toast.LENGTH_SHORT).show()
        }
    }
    //nos manda al login
    private fun goLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}