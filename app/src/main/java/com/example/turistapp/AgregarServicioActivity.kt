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

class AgregarServicioActivity : AppCompatActivity() {
    private lateinit var etNombre_Producto:EditText
    private lateinit var etCosto:EditText
    private lateinit var etDescripcionServicio:EditText
    private lateinit var btnRegistrar:Button
    private lateinit var requestQueue: RequestQueue
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_servicio)
        etNombre_Producto=findViewById(R.id.etNombre_Producto)
        etCosto=findViewById(R.id.etCosto)
        etDescripcionServicio=findViewById(R.id.etDescripcionServicio)
        btnRegistrar=findViewById(R.id.btnRegistrarServicio)

        btnRegistrar.setOnClickListener { Registrar() }
    }

    private fun Registrar(){
        val sharedPreference1 =  getSharedPreferences("id_negocio", Context.MODE_PRIVATE)
        var id=sharedPreference1.getString("id","null")
        var nombre=etNombre_Producto.text.toString()
        var costo=etCosto.text.toString();
        var descripcion=etDescripcionServicio.text.toString()
        if (nombre.isEmpty() || costo.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(baseContext, "Debe llenar todos los datos", Toast.LENGTH_SHORT).show()

        }else{
            requestQueue= Volley.newRequestQueue(this)
            val jsonObject= JSONObject()
            jsonObject.put("nombre",nombre)
            jsonObject.put("precio",costo)
            jsonObject.put("descripcion",descripcion)
            jsonObject.put("negocio",id)
            val jsonObjectRequest= JsonObjectRequest(
                Request.Method.POST,"http://192.168.0.20:4500/menu/registro/",
                jsonObject,
                Response.Listener { response ->
                    var intent1: Intent = Intent(applicationContext,HomeNegocioActivity::class.java)
                    startActivity(intent1)
                    finish()
                }, Response.ErrorListener { error ->
                    Toast.makeText(applicationContext,"Error al Subir los Datos",Toast.LENGTH_SHORT).show()
                });
            requestQueue.add(jsonObjectRequest)
        }


    }
}