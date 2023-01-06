package com.example.turistapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class SitioActivity : AppCompatActivity() {
    private lateinit var imagen_sitioN:ImageView
    private lateinit var tv_sitioN:TextView
    private lateinit var tv_descripcionN: TextView
    private lateinit var requestQueue: RequestQueue
    private lateinit var latitud:String
    private lateinit var longitud:String
    private lateinit var nombre:String
    private lateinit var id:String
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: CustomAdapter3
    private lateinit var recyclerView: RecyclerView
    private lateinit var URI:String
    private lateinit var tipo:String
    private lateinit var tv_tipoN:TextView
    private lateinit var tvCorreo:TextView
    private lateinit var tvTele:TextView
    private lateinit var tele:String
    private lateinit var corrr:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sitio)
        layoutManager= LinearLayoutManager(this)
        requestQueue= Volley.newRequestQueue(this)
        imagen_sitioN=findViewById(R.id.imagen_sitioN)
        tv_sitioN=findViewById(R.id.tv_sitioN)
        tv_descripcionN=findViewById(R.id.tv_descripcionN)
        tv_tipoN=findViewById(R.id.tv_tipoN)
        tvCorreo=findViewById(R.id.tvCorreo)
        tvTele=findViewById(R.id.tvTele)
        val bundle:Bundle?=intent.extras
        val imagen=bundle!!.get("imagen").toString()
        val descripcion=bundle!!.get("descripcion").toString()
        id=bundle!!.get("id").toString()
        URI="http://192.168.0.20:4500/menu/obtenerServicios/"+id
        nombre=bundle!!.get("nombre").toString()
        latitud=bundle!!.get("latitud").toString()
        longitud=bundle!!.get("longitud").toString()
        tipo=bundle!!.get("tipo").toString()
        tele=bundle!!.get("telefono").toString()
        corrr=bundle!!.get("email").toString()
        tv_sitioN.setText(nombre)
        tv_descripcionN.setText(descripcion)
        tv_tipoN.setText(tipo)
        tvTele.setText(tele)
        tvCorreo.setText(corrr)
        requestQueue= Volley.newRequestQueue(this)
        val URL="http://192.168.0.20:4500/negocios/get_imagen/"+imagen;
        val respuestaConsulta = ImageRequest(
            URL,
            { response ->
                imagen_sitioN.setImageBitmap(response)
            }, 0, 0, null, null
        ) {
            Toast.makeText(applicationContext,"ERROR RED",Toast.LENGTH_SHORT).show()
        }
        requestQueue.add(respuestaConsulta)
        //btnLocalizacionU.setOnClickListener { loca() }
        InicializarDatos()
        val btnLocalizacionU: View = findViewById(R.id.btnLocalizacionU)
        btnLocalizacionU.setOnClickListener { view ->
            loca()
        }
        recyclerView=findViewById(R.id.recycle_view3)
        recyclerView.layoutManager=layoutManager
    }

    private fun InicializarDatos(){
        val jsonArrayRequest= JsonArrayRequest(URI,
            Response.Listener { response ->
                var json: JSONObject? = null
                var tamano=response.length()
                var idn = Array<String?>(tamano) { null }
                var nombre = Array<String?>(tamano) { null }
                var precio = Array<String?>(tamano) { null }
                var imagenes = Array<String?>(tamano) { null }
                var descripcion = Array<String?>(tamano) { null }
                var negocioid = Array<String?>(tamano) { null }

                try {
                    for (i in 0 until response.length()) {
                        json = response.getJSONObject(i)
                        idn[i]=json.getString("_id")
                        nombre[i]=json.getString("nombre")
                        precio[i]=json.getString("precio")
                        imagenes[i]=json.getString("imagen")
                        descripcion[i]=json.getString("descripcion")
                        negocioid[i]=json.getString("negocio")
                    }
                    adapter= CustomAdapter3(nombre,precio,imagenes,descripcion,this)
                    recyclerView.adapter= adapter
                    adapter.setOnItemClickListener(object : CustomAdapter3.onItemClickListener{
                        override fun OnItemClick(position: Int) {

                        }
                    })
                } catch (e: JSONException) {

                }

            }, Response.ErrorListener { error ->
            });
        requestQueue.add(jsonArrayRequest)
    }



    private fun loca(){
        val intent1: Intent = Intent(applicationContext,LocalizacionNegocioLU::class.java)
         intent1.putExtra("latitud",latitud)
        intent1.putExtra("longitud", longitud)
        intent1.putExtra("nombre", nombre)
        startActivity(intent1)
    }
}