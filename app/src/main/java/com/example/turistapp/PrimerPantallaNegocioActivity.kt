package com.example.turistapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley

class PrimerPantallaNegocioActivity : AppCompatActivity() {
    private lateinit var tvNombreN: TextView
    private lateinit var btnContinuar: Button
    private lateinit var requestQueue: RequestQueue
    private lateinit var ivFotoN: ImageView
    private lateinit var spOpcion: Spinner
    private lateinit var opcion:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_primer_pantalla_negocio)
        val sharedPreference =  getSharedPreferences("datos_negocio", Context.MODE_PRIVATE)
        val nombre= sharedPreference.getString("nombre","Nombre")!!
        val imagen=sharedPreference.getString("imagen","imagen")!!
        spOpcion=findViewById(R.id.spOpcion)
        tvNombreN=findViewById(R.id.tvNombreN)
        btnContinuar=findViewById(R.id.btnContinuar)
        ivFotoN=findViewById(R.id.ivFotoN)
        requestQueue= Volley.newRequestQueue(this)
        val URL="http://192.168.0.20:4500/negocios/get_imagen/"+imagen;
        val respuestaConsulta = ImageRequest(
            URL,
            { response ->
                ivFotoN.setImageBitmap(response)
            }, 0, 0, null, null
        ) {

        }
        requestQueue.add(respuestaConsulta)


        tvNombreN.setText("Bienvenido "+nombre)
        var list_of_items = arrayOf("Si", "No")
        val adapter = ArrayAdapter(this, R.layout.spinner2, list_of_items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spOpcion.setAdapter(adapter)
        spOpcion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                opcion="null"
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                    0->{
                        opcion="si";
                    }
                    1->{
                       opcion="no"
                    }
                }
            }
        }
        btnContinuar.setOnClickListener { desicion() }
    }

    private fun desicion(){
        if (opcion.equals("null")){
            Toast.makeText(applicationContext, "Debe elegir alguna opcion", Toast.LENGTH_SHORT).show()
        }else{
            if (opcion.equals("si")){
                val intent1: Intent = Intent(applicationContext,PagoActivity::class.java)
                startActivity(intent1)
            }else if (opcion.equals("no")){
                val intent1: Intent = Intent(applicationContext,HomeNegocioActivity::class.java)
                startActivity(intent1)
            }
        }
    }


}