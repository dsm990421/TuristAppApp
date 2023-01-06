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
import org.json.JSONObject

class LoginNegocioActivity : AppCompatActivity() {//Variables globales para inicio de sesion
    private lateinit var etEmailNegocio:EditText
    private lateinit var etPasswordNegocio:EditText
    private lateinit var btnEntrarNegocio:Button
    private lateinit var btnMain:Button
    private lateinit var btnRegistroN:Button
    private lateinit var requestQueue: RequestQueue
    private var httpURI="http://192.168.0.20:4500/negocios/login"//URL para inicio de sesion de negocio

    override fun onCreate(savedInstanceState: Bundle?) {
        //Se inicializan las variables nesecarias
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_negocio)
        requestQueue= Volley.newRequestQueue(this)
        etEmailNegocio=findViewById(R.id.etEmailNegocio)
        etPasswordNegocio=findViewById(R.id.etPasswordNegocio)
        btnEntrarNegocio=findViewById(R.id.btnEntrarNegocio)
        btnMain=findViewById(R.id.btnMain)
        btnRegistroN=findViewById(R.id.btnRegistroN)
        btnMain.setOnClickListener { actividadPrincipal() }
        btnRegistroN.setOnClickListener { RegistrarNegocio() }
        btnEntrarNegocio.setOnClickListener { IniciarSesionNegocio() }
    }

    private fun IniciarSesionNegocio(){//Se reciben los datos desde el XML
        val email=etEmailNegocio.text.toString()
        val password=etPasswordNegocio.text.toString()
        val jsonObject= JSONObject()//Se crea un objeto JSON para enviar los datos de la solicitud
        jsonObject.put("email",email)
        jsonObject.put("password",password)
        val jsonObjectRequest= JsonObjectRequest(//Se hace una solicitud de un objeto JSON al servidor
            Request.Method.POST,httpURI,//Se manda la URL y el metodo
            jsonObject,
            Response.Listener { response ->//En caso de exito se guardaran los datos en dos Shared Preferences
                val id=response.get("id").toString()
                val nombre=response.get("nombre").toString()
                val email=response.get("email").toString()
                val latitud=response.get("latitud").toString()
                val longitud=response.get("longitud").toString()
                val telefono=response.get("telefono").toString()
                val descripcion=response.get("descripcion").toString()
                val premium=response.get("premium").toString()
                val ne=response.get("numero_ediciones").toString()
                val sharedPreference2 =  getSharedPreferences("inicio_sesion_negocio", MODE_PRIVATE)
                var editor2 = sharedPreference2.edit()
                editor2.putString("id",id)
                editor2.putString("nombre",nombre)
                editor2.putString("email",email)
                editor2.putString("latitud",latitud)
                editor2.putString("longitud", longitud)
                editor2.putString("telefono",telefono)
                editor2.putString("descripcion",descripcion)
                editor2.putString("premium",premium)
                editor2.putString("numero_ediciones",ne)
                editor2.commit()
                val sharedPreference =  getSharedPreferences("datos_negocio",Context.MODE_PRIVATE)
                var editor = sharedPreference.edit()
                editor.putString("premium", premium)
                editor.putString("id",id)
                editor.putString("numero_ediciones",ne)
                editor.commit()
                val sharedPreference1 = getSharedPreferences("id_negocio", Context.MODE_PRIVATE)
                var editor1 = sharedPreference1.edit()
                editor1.putString("id",id)
                editor1.commit()
                //Se inicia la actividad del Home del negocio
                var intent1: Intent = Intent(applicationContext,HomeNegocioActivity::class.java)
                startActivity(intent1)
                finish()

            }, Response.ErrorListener { error ->
                Toast.makeText(applicationContext,"Error al Autentificar", Toast.LENGTH_SHORT).show()
            });
        requestQueue.add(jsonObjectRequest)
    }

    private fun actividadPrincipal(){//En caso de querer volver al login de usuario
        var intent1: Intent = Intent(applicationContext,MainActivity::class.java)
        startActivity(intent1)
    }

    private fun RegistrarNegocio(){//En caso de querer registrar un negocio
        var intent1: Intent = Intent(applicationContext,RegistroNegociosActivity::class.java)
        startActivity(intent1)
    }


}