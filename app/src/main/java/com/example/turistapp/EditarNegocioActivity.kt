package com.example.turistapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class EditarNegocioActivity : AppCompatActivity() {
    private lateinit var etNombreEN:EditText
    private lateinit var btnCoordenadasNE:Button
    private lateinit var CPNE:EditText
    private lateinit var spZonaNE:Spinner
    private lateinit var etEmailNE:EditText
    private lateinit var etTelefonoNE:EditText
    private lateinit var etDescripcion:EditText
    private lateinit var btnAct:Button
    private lateinit var nom:String
    private lateinit var em:String
    private lateinit var im:String
    private lateinit var tel:String
    private lateinit var copos:String
    private lateinit var zon:String
    private lateinit var descip:String
    private lateinit var id:String
    private lateinit var lati:String
    private lateinit var long:String
    private lateinit var requestQueue: RequestQueue
    private lateinit var spTipoN:Spinner
    private lateinit var tipo:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_negocio)


        val sharedPreference =  getSharedPreferences("coordenadas", Context.MODE_PRIVATE)
        lati=sharedPreference.getString("latitud", "null").toString()
        long=sharedPreference.getString("longitud", "null").toString()
        val sharedPreference2 =  getSharedPreferences("datos_negocios2", Context.MODE_PRIVATE)
        id=sharedPreference2.getString("id","id")!!
        nom=sharedPreference2.getString("nombre","nombre")!!
        em=sharedPreference2.getString("email","email")!!
        tel=sharedPreference2.getString("telefono","tele")!!
        copos=sharedPreference2.getString("cp","cp")!!
        zon=sharedPreference2.getString("zona","zona")!!
        descip=sharedPreference2.getString("descripcion","descripcion")!!
        tipo=sharedPreference2.getString("tipo","tipo")!!
        spTipoN=findViewById(R.id.spTipoN)
        etNombreEN=findViewById(R.id.etNombreEN)
        btnCoordenadasNE=findViewById(R.id.btnCoordenadasNE)
        CPNE=findViewById(R.id.etCPNE)
        spZonaNE=findViewById(R.id.spZonaNE)
        etTelefonoNE=findViewById(R.id.etTelefonoNE)
        etDescripcion=findViewById(R.id.etDescripcion)
        btnAct=findViewById(R.id.btnAct)

        etNombreEN.setText(nom)
        CPNE.setText(copos)
        etTelefonoNE.setText(tel)
        etDescripcion.setText(descip)


        var list_of_items = arrayOf("Tlalmanalco-Centro", "San Rafael", "Tlalmanalco")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, list_of_items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spZonaNE.setAdapter(adapter)
        var po=100;
        when(zon){
            "centro"->{
                po=0
            }
            "san-rafael"->{
                po=1
            }
            "tlalmanalco"->{
                po=2
            }
        }

        var list_of_items2 = arrayOf("Comida", "Turismo")
        val adapter2 = ArrayAdapter(this, R.layout.spinner_item, list_of_items2)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTipoN.setAdapter(adapter2)
        var p02=100
        when(tipo){
            "comida"->{
                p02=0
            }
            "turismo"->{
                p02=1
            }
        }
        spTipoN.setSelection(p02)
        spTipoN.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
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



        spZonaNE.setSelection(po)

        spZonaNE.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                zon="null"
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                    0->{
                        zon="centro";
                    }
                    1->{
                        zon="san-rafael"
                    }
                    2->{
                        zon="tlalmanalco"
                    }
                }
            }
        }

        btnCoordenadasNE.setOnClickListener { obtenerCoordenadas() }
        btnAct.setOnClickListener { actualizar() }
    }

    private fun obtenerCoordenadas(){
        val intent: Intent = Intent(this, CoordenadasActivity2::class.java)
        startActivity(intent)
        finish()

    }

    private fun actualizar(){
        var nombre = etNombreEN.text.toString();
        var codigopostal = CPNE.text.toString();
        var telefono = etTelefonoNE.text.toString();
        var descripcion = etDescripcion.text.toString();
        if (nombre.isEmpty() || codigopostal.isEmpty() || telefono.isEmpty() ||
            descripcion.isEmpty()|| zon.equals("null") || lati.equals("null")||long.equals("null")) {
            Toast.makeText(baseContext, "Debe llenar todos los datos u Obtener las Coordenadas", Toast.LENGTH_SHORT).show()
        }else{
            requestQueue= Volley.newRequestQueue(this)
            val jsonObject= JSONObject()
            jsonObject.put("nombre",nombre)
            jsonObject.put("latitud", lati)
            jsonObject.put("longitud", long)
            jsonObject.put("codigopostal", codigopostal)
            jsonObject.put("zona", zon)
            jsonObject.put("telefono", telefono)
            jsonObject.put("descripcion",descripcion)
            jsonObject.put("tipo",tipo)

            val jsonObjectRequest= JsonObjectRequest(
                Request.Method.PUT,"http://192.168.0.20:4500/negocios/actualizar/"+id,
                jsonObject,
                Response.Listener { response ->
                    Toast.makeText(applicationContext, "Negocio Actualizado Satisfactoriamente", Toast.LENGTH_SHORT).show()
                    val intent:Intent=Intent(this, HomeNegocioActivity::class.java)
                    startActivity(intent)
                    finish()
                }, Response.ErrorListener { error ->
                    Toast.makeText(applicationContext,"Error al Actualizar",Toast.LENGTH_SHORT).show()
                });
            requestQueue.add(jsonObjectRequest)
        }
    }


}