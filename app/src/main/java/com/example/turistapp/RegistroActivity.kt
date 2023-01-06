package com.example.turistapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.json.JSONObject

class RegistroActivity : AppCompatActivity() {
    //Variables globales a utilizar en la programacion de la actividad
    private lateinit var etNombre: EditText
    private lateinit var etEmail1: EditText
    private lateinit var etPassword1: EditText
    private lateinit var etPassword2: EditText
    private lateinit var etTelefono: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var btnIniciar: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var requestQueue: RequestQueue
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        //Enlaces
        etNombre = findViewById<EditText>(R.id.etNombre)
        etEmail1 = findViewById<EditText>(R.id.etEmail1)
        etPassword1 = findViewById<EditText>(R.id.etPassword1)
        etPassword2 = findViewById<EditText>(R.id.etPassword2)
        etTelefono = findViewById<EditText>(R.id.etTelefono)
        btnIniciar = findViewById<Button>(R.id.btnIniciar)
        btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        //Oyente
        btnRegistrar.setOnClickListener{Registrar()}
        btnIniciar.setOnClickListener { Iniciar() }
    }
    private fun Registrar() {
        //Guarda los datos de los edittext de las vistas
        var nombre = etNombre.text.toString();
        var email = etEmail1.text.toString();
        var telefono = etTelefono.text.toString();
        var contrasena = etPassword1.text.toString();
        var repetir = etPassword2.text.toString();
        //Corrobora que esten llenos todos los campos
        if (nombre.isEmpty() || email.isEmpty() || contrasena.isEmpty() || repetir.isEmpty()|| telefono.isEmpty()) {
            Toast.makeText(baseContext, "Debe llenar todos los datos", Toast.LENGTH_SHORT).show()
        } else {
            if (contrasena == repetir) {//Si coinciden las contraseñas en ambos campos
                requestQueue= Volley.newRequestQueue(this)
                val jsonObject= JSONObject()//Objeto con los valores a enviar
                jsonObject.put("nombre",nombre)
                jsonObject.put("email",email)
                jsonObject.put("telefono",telefono)
                jsonObject.put("password",repetir)
                FirebaseAuth.getInstance()//Se instancia Firebase y se crea un usuario en Firebase
                    .createUserWithEmailAndPassword(email,contrasena)
                    .addOnCompleteListener {
                        if(it.isSuccessful){//Si se ejecuta correctamente se guardaran los datos en el servidor mediante Volley
                            val jsonObjectRequest= JsonObjectRequest(//Se llamara a un JSON object en la llamada
                                Request.Method.POST,"http://192.168.0.20:4500/usuarios/registro",
                                jsonObject,
                                Response.Listener { response ->//Si es correcta la llamada se guardaran los datos nesecarios
                                    val id=response.get("id").toString()
                                    val nombre=response.get("nombre").toString()
                                    val email=response.get("email").toString()
                                    val sharedPreference2 =  getSharedPreferences("tipo_inicio",
                                        Context.MODE_PRIVATE)
                                    var editor2 = sharedPreference2.edit()
                                    editor2.putString("sesion","email")
                                    editor2.commit()
                                    val sharedPreference =  getSharedPreferences("datos_usuario",
                                        Context.MODE_PRIVATE)
                                    var editor = sharedPreference.edit()
                                    editor.putString("id",id)
                                    editor.putString("nombreu",nombre)
                                    editor.putString("email",email)
                                    editor.commit()
                                    var intent1: Intent = Intent(applicationContext,HomeActivity::class.java)//Se ejecuta el home de Usuario
                                    startActivity(intent1)
                                    finish()
                                }, Response.ErrorListener { error ->
                                    Toast.makeText(applicationContext,"Error Autentificar",Toast.LENGTH_SHORT).show()
                                });
                            requestQueue.add(jsonObjectRequest)
                        }
                        else{ Toast.makeText(this,"Error de Autentificacion",Toast.LENGTH_SHORT).show()}
                    }
            } else {
                Toast.makeText(
                    baseContext, "La contraseña no coincide",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    private fun Iniciar(){//Redirige al Login de Usuario
        val intent1: Intent = Intent(applicationContext,MainActivity::class.java)
        startActivity(intent1)
    }
}