package com.example.turistapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.ToggleButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

//Variables para la programacion del fragment
private lateinit var adapter: CustomAdapter4
private lateinit var recyclerView: RecyclerView
private lateinit var array: ArrayList<CustomAdapter>
private lateinit var tipo:Spinner
private lateinit var t:String
class BusquedaFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view:View=inflater.inflate(R.layout.fragment_busqueda, container, false)
        //Se enlazan las vistas del fragment
        val layoutManager= LinearLayoutManager(context)
        tipo=view.findViewById(R.id.tipo)
        //Segun el adaptador buscara los negocios de comida y turismo
        var list_of_items = arrayOf("Comida", "Turismo")
        val requestQueue:RequestQueue=Volley.newRequestQueue(context)
        val adapter2 = context?.let { ArrayAdapter(it, R.layout.spinner_item, list_of_items) }
        if (adapter2 != null) {
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        tipo.setAdapter(adapter2)
        recyclerView=view.findViewById(R.id.recycle_viewbu)
        recyclerView.layoutManager=layoutManager
        tipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                t="null"
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                    0->{
                        t="comida";
                        //En caso de ser comida buscara los negocios que sean del tipo comida
                        val URL="http://192.168.0.20:4500/negocios/buscarnegocio/"+t
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
                                    adapter= CustomAdapter4(descripcion,tipo,imagenes,zona,nombre,context)
                                    recyclerView.adapter= adapter
                                    adapter.setOnItemClickListener(object : CustomAdapter4.onItemClickListener{
                                        override fun OnItemClick(position: Int) {
                                            val intent1: Intent = Intent(context,SitioActivity::class.java)
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
                    1->{
                        t="turismo";
                        val URL="http://192.168.0.20:4500/negocios/buscarnegocio/"+t
                        //En caso de ser comida buscara los negocios que sean del tipo turismo
                        val jsonArrayRequest= JsonArrayRequest(URL,
                            Response.Listener { response ->
                                var json2: JSONObject? = null
                                var tamano=response.length()
                                var idn = Array<String?>(tamano) { null }
                                var nombre = Array<String?>(tamano) { null }
                                var zona = Array<String?>(tamano) { null }
                                var zonac = Array<String?>(tamano) { null }
                                var imagenes = Array<String?>(tamano) { null }
                                var descripcion = Array<String?>(tamano) { null }
                                var latitud = Array<String?>(tamano) { null }
                                var longitud = Array<String?>(tamano) { null }
                                var tipo = Array<String?>(tamano) { null }
                                var email = Array<String?>(tamano) { null }
                                var telefono = Array<String?>(tamano) { null }
                                try {
                                    for (i in 0 until response.length()) {
                                        json2 = response.getJSONObject(i)
                                        idn[i]=json2.getString("_id")
                                        nombre[i]=json2.getString("nombre")
                                        zona[i]=json2.getString("zona")
                                        if(zona[i].equals("centro")){
                                            zonac[i]="Tlalmanalco Centro"
                                        }else if (zona[i].equals("san-rafael")){
                                            zonac[i]="San Rafael"
                                        }else if(zona[i].equals("tlalmanalco")){
                                            zonac[i]="Tlalmanalco"
                                        }
                                        imagenes[i]=json2.getString("imagen")
                                        descripcion[i]=json2.getString("descripcion")
                                        latitud[i]=json2.getString("latitud")
                                        longitud[i]=json2.getString("longitud")
                                        email[i]=json2.getString("email")
                                        telefono[i]=json2.getString("telefono")
                                        tipo[i]=json2.getString("tipo").capitalize()
                                    }
                                    adapter= CustomAdapter4(descripcion,tipo,imagenes,zonac,nombre,context)
                                    recyclerView.adapter= adapter
                                    adapter.setOnItemClickListener(object : CustomAdapter4.onItemClickListener{
                                        override fun OnItemClick(position: Int) {
                                            val intent1: Intent = Intent(context,SitioActivity::class.java)
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
            }
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BusquedaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }

            }
    }




}