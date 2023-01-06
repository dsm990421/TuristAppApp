package com.example.turistapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Camera
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class RegistroNegociosActivity : AppCompatActivity(){
    //Varaibles globales a utilizar en la programacion de la actividad
    private lateinit var spZona:Spinner
    private lateinit var etNombre:EditText
    private lateinit var etCP:EditText
    private lateinit var etEmail1:EditText
    private lateinit var etTelefono:EditText
    private lateinit var etDescripcion:EditText
    private lateinit var btnRegistrar:Button
    private lateinit var zona:String
    private lateinit var etPass:EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var requestQueue: RequestQueue
    private lateinit var mMap:GoogleMap
    private lateinit var latitud:String
    private lateinit var longitud:String
    private lateinit var btnCoordenadas: Button
    private lateinit var tipo:String
    private lateinit var spTipo:Spinner
    companion object{
        private const val LOCATION_REQUEST_CODE=1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_negocios)
        //Se llama a un shared preferences que tendra los datos a guardar de la actividad de coordenadas
        val sharedPreference =  getSharedPreferences("coordenadas", Context.MODE_PRIVATE)
        latitud=sharedPreference.getString("latitud", "null").toString()
        longitud=sharedPreference.getString("longitud", "null").toString()
        //Se enlazan las vistas
        spZona = findViewById<Spinner>(R.id.spZona)
        spTipo=findViewById(R.id.spTipo)
        etNombre=findViewById(R.id.etNombre)
        etCP=findViewById(R.id.etCP)
        etEmail1=findViewById(R.id.etEmail1)
        etTelefono=findViewById(R.id.etTelefono)
        etDescripcion=findViewById(R.id.etDescripcion)
        etPass=findViewById(R.id.etPass)
        btnRegistrar=findViewById(R.id.btnRegistrar)
        btnCoordenadas=findViewById(R.id.btnCoordenadas)
        btnCoordenadas.setOnClickListener { Redirigir() }
        btnRegistrar.setOnClickListener { Registrar() }
        auth = FirebaseAuth.getInstance()
        //Se crean arreglos para mostrar dentro de los Spinner Zona y Tipo de negocio
        var list_of_items = arrayOf("Tlalmanalco-Centro", "San Rafael", "Tlalmanalco")
        var list_of_items2 = arrayOf("Comida", "Turismo")
        val adapter2 = ArrayAdapter(this, R.layout.spinner_item, list_of_items2)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, list_of_items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spZona.setAdapter(adapter)
        spTipo.setAdapter(adapter2)
        spTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            //Se guarda el tipo de negocio segun la posicion en la que se coloca el spinner
            override fun onNothingSelected(parent: AdapterView<*>?) {
                tipo="null"
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                    0->{
                        tipo="comida";
                    }
                    1->{
                        tipo="turismo"
                    }
                }
            }
        }
        spZona.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            //Se coloca valor a zona segun la posicion en la que se coloca el spinner
            override fun onNothingSelected(parent: AdapterView<*>?) {
                    zona="null"
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                    0->{
                        zona="centro";
                    }
                    1->{
                        zona="san-rafael"
                    }
                    2->{
                        zona="tlalmanalco"
                    }
                }
            }
        }
    }


    private fun Redirigir(){
        //Hace una llamada a Coordenadas Activity para poder ver la localizacion del negocio
        val intent:Intent=Intent(this, CoordenadasActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun Registrar() {
        //Se guardan los valores introducidos dentro de los edit text en las vistas
        var nombre = etNombre.text.toString();
        var codigopostal = etCP.text.toString();
        var email = etEmail1.text.toString();
        var telefono = etTelefono.text.toString();
        var descripcion = etDescripcion.text.toString();
        var password = etPass.text.toString()

        if (nombre.isEmpty() || codigopostal.isEmpty() || email.isEmpty()|| telefono.isEmpty() ||
            descripcion.isEmpty() || password.isEmpty() || zona.equals("null") || latitud.equals("null")||longitud.equals("null")) {
            //Si algun campo esta nulo no se podra continuar
            Toast.makeText(baseContext, "Debe llenar todos los datos u Obtener las Coordenadas", Toast.LENGTH_SHORT).show()
        } else {
            requestQueue= Volley.newRequestQueue(this)
                val jsonObject= JSONObject()//Se crea un objeto con los datos a enviar para guardar en el servidor
                jsonObject.put("nombre",nombre)
                jsonObject.put("latitud", latitud)
                jsonObject.put("longitud", longitud)
                jsonObject.put("codigopostal", codigopostal)
                jsonObject.put("zona", zona)
                jsonObject.put("email",email)
                jsonObject.put("telefono", telefono)
                jsonObject.put("descripcion",descripcion)
                jsonObject.put("password",password)
                jsonObject.put("tipo",tipo)
            val jsonObjectRequest= JsonObjectRequest(//Se solicita mediante el metodo post el registro de un negocio
                    Request.Method.POST,"http://192.168.0.20:4500/negocios/registro",
                    jsonObject,
                    Response.Listener { response ->
                        var id= response.get("_id").toString()
                        var nom=response.get("nombre").toString()
                        var lat=response.get("latitud").toString()
                        var long=response.get("longitud").toString()
                        var zon=response.get("zona").toString()
                        var emai=response.get("email").toString()
                        var tele=response.get("telefono").toString()
                        var descrip=response.get("descripcion").toString()
                        var ima=response.get("imagen").toString()
                        var premium=response.get("premium").toString()
                        var tip=response.get("tipo").toString()
                        var ne=response.get("numero_ediciones").toString()
                        var url="http://192.168.0.20:4500/negocios/subirimagen/"+id+"/"
                        val sharedPreference =  getSharedPreferences("datos_negocio",Context.MODE_PRIVATE)
                        var editor = sharedPreference.edit()
                        editor.putString("id",id)
                        editor.putString("url",url)
                        editor.putString("nombre", nom)
                        editor.putString("latitud",lat)
                        editor.putString("longitud",long)
                        editor.putString("zona", zon)
                        editor.putString("email",emai)
                        editor.putString("telefono",tele)
                        editor.putString("descripcion", descrip)
                        editor.putString("premium", premium)
                        editor.putString("tipo", tip)
                        editor.putString("numero_ediciones",ne)
                        editor.commit()
                        val sharedPreference1 =  getSharedPreferences("id_negocio",Context.MODE_PRIVATE)
                        var editor1 = sharedPreference1.edit()
                        editor1.putString("id",id)
                        editor1.commit()
                        Toast.makeText(applicationContext, "Negocio Registrado Satisfactoriamente", Toast.LENGTH_SHORT).show()
                        val intent:Intent=Intent(this, HomeNegocioActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, Response.ErrorListener { error ->
                        Toast.makeText(applicationContext,"Error al Registrar",Toast.LENGTH_SHORT).show()
                    });
                requestQueue.add(jsonObjectRequest)
        }
    }


}











