package com.example.turistapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class ZonaActivity : AppCompatActivity() {
    //Variables globales a utilizar en la programacion
    private lateinit var adapter: CustomAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var URL:String
    private lateinit var zonap:String
    private lateinit var imagen_zona1:ImageView
    private lateinit var tv_zona:TextView
    private lateinit var item_Detail:TextView
    private lateinit var layoutManager:LinearLayoutManager
    private lateinit var requestQueue:RequestQueue

    //Arreglos con los datos de las 3 zonas disponibles en la App
    val titles = arrayOf("Tlalmanalco",
        "San Rafael",
        "Tlalmanalco Centro")

    val details= arrayOf("Pueblo con Encanto",
        "Ponte en Contacto con la Naturaleza",
        "Un emblema del municipio")
    val images = intArrayOf(R.drawable.centro,
        R.drawable.sanrafa,
        R.drawable.tlalma)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zona)
        layoutManager= LinearLayoutManager(this)
        requestQueue= Volley.newRequestQueue(this)
        imagen_zona1=findViewById(R.id.imagen_zona1)
        tv_zona=findViewById(R.id.tv_zona)
        item_Detail=findViewById(R.id.item_Detail)
        val sharedPreference =  getSharedPreferences("posicion_zona", MODE_PRIVATE)
        var posicion=sharedPreference.getInt("posicion",100)
        if (posicion==0){
            zonap="tlalmanalco"
        } else if(posicion==1){
            zonap="san-rafael"
        } else if (posicion==2){
            zonap="centro"
        }
        URL="http://192.168.0.20:4500/negocios/lista_negocios/"+zonap
        imagen_zona1.setImageResource(images[posicion])
        tv_zona.setText(titles[posicion])
        item_Detail.setText(details[posicion])
        InicializarDatos()
        recyclerView=findViewById(R.id.recycle_view2)
        recyclerView.layoutManager=layoutManager
    }

    private fun InicializarDatos(){
        val jsonArrayRequest= JsonArrayRequest(URL,
            Response.Listener { response ->
                var json: JSONObject? = null
                var tamano=response.length()
                var idn = Array<String?>(tamano) { null }
                var nombre = Array<String?>(tamano) { null }
                var zona = Array<String?>(tamano) { null }
                var imagenes = Array<String?>(tamano) { null }
                var descripcion = Array<String?>(tamano) { null }
                var latitud = Array<String?>(tamano) { null }
                var longitud = Array<String?>(tamano) { null }
                var tipo = Array<String?>(tamano) { null }
                var email = Array<String?>(tamano) { null }
                var telefono = Array<String?>(tamano) { null }
                try {
                    for (i in 0 until response.length()) {
                        json = response.getJSONObject(i)
                        idn[i]=json.getString("_id")
                        nombre[i]=json.getString("nombre")
                        zona[i]=json.getString("zona")
                        if(zona[i].equals("centro")){
                            zona[i]="Tlalmanalco Centro"
                        }else if (zona[i].equals("san-rafael")){
                            zona[i]="San Rafael"
                        }else if(zona[i].equals("tlalmanalco")){
                            zona[i]="Tlalmanalco"
                        }
                        imagenes[i]=json.getString("imagen")
                        descripcion[i]=json.getString("descripcion")
                        latitud[i]=json.getString("latitud")
                        longitud[i]=json.getString("longitud")
                        email[i]=json.getString("email")
                        telefono[i]=json.getString("telefono")
                        tipo[i]=json.getString("tipo").capitalize()
                    }
                    adapter= CustomAdapter(descripcion,tipo,imagenes,zona,nombre,this)
                    recyclerView.adapter= adapter
                    adapter.setOnItemClickListener(object : CustomAdapter.onItemClickListener{
                        override fun OnItemClick(position: Int) {
                            val intent1: Intent = Intent(applicationContext,SitioActivity::class.java)
                            intent1.putExtra("id",idn[position])
                            intent1.putExtra("imagen",imagenes[position])
                            intent1.putExtra("descripcion",descripcion[position])
                            intent1.putExtra("nombre",nombre[position])
                            intent1.putExtra("latitud",latitud[position])
                            intent1.putExtra("longitud", longitud[position])
                            intent1.putExtra("tipo", tipo[position])
                            intent1.putExtra("email", email[position])
                            intent1.putExtra("telefono",telefono[position])
                            startActivity(intent1)
                        }
                    })
                } catch (e: JSONException) {

                }

            }, Response.ErrorListener { error ->
            });
        requestQueue.add(jsonArrayRequest)
    }





}