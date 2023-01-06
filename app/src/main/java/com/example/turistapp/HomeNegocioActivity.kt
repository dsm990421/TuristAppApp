package com.example.turistapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.turistapp.databinding.ActivityHomeBinding
import com.example.turistapp.databinding.ActivityHomeNegocioBinding
import com.facebook.login.LoginManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Text

class HomeNegocioActivity : AppCompatActivity() {
    private lateinit var drawer:DrawerLayout
    private lateinit var toggle:ActionBarDrawerToggle

    private lateinit var tv_nombreNeg:TextView
    private lateinit var imagen_negocio:ImageView
    private lateinit var tv_cpNeg:TextView
    private lateinit var tv_zonaNeg:TextView
    private lateinit var tv_emailNeg:TextView
    private lateinit var tv_telefono:TextView
    private lateinit var tv_descripcion:TextView
    private lateinit var btnverCoor:Button
    private lateinit var btnEditarNeg:Button
    private lateinit var Url:String
    private lateinit var requestQueue: RequestQueue
    private lateinit var imagen:String
    private lateinit var nombre:String
    private lateinit var latitud:String
    private lateinit var longitud:String
    private lateinit var id:String
    private lateinit var email:String
    private lateinit var telefono:String
    private lateinit var cp:String
    private lateinit var zona:String
    private lateinit var descripcion:String
    private lateinit var layoutManager:LinearLayoutManager
    private lateinit var adapter: CustomAdapter3
    private lateinit var recyclerView: RecyclerView
    private lateinit var URL:String
    private lateinit var btnAgregarServicio:Button
    private lateinit var im:String
    private lateinit var i:String
    private lateinit var binding: ActivityHomeNegocioBinding
    private lateinit var premium:String
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var tv_tiponegocio:TextView
    private lateinit var t:String
    private lateinit var ne:String
    companion object{
        private const val LOCATION_REQUEST_CODE=1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_negocio)
        layoutManager= LinearLayoutManager(this)
        requestQueue= Volley.newRequestQueue(this)
        binding = ActivityHomeNegocioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar2)

        val sharedPreference12 =  getSharedPreferences("datos_negocio", Context.MODE_PRIVATE)
        ne=sharedPreference12.getString("numero_ediciones","ne")!!

//        val view = binding.root
        val sharedPreference =  getSharedPreferences("sesionesi", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putBoolean("negocio",true)
        editor.commit()
        checarPermisos()
        requestQueue= Volley.newRequestQueue(this)
        tv_nombreNeg=findViewById(R.id.tv_nombreNeg)
        imagen_negocio=findViewById(R.id.imagen_negocio)
        tv_cpNeg=findViewById(R.id.tv_cpNeg)
        tv_zonaNeg=findViewById(R.id.tv_zonaNeg)
        tv_emailNeg=findViewById(R.id.tv_emailNeg)
        tv_telefono=findViewById(R.id.tv_telefono)
        tv_descripcion=findViewById(R.id.tv_descripcion)
        tv_tiponegocio=findViewById(R.id.tv_tiponegocio)
        val sharedPreference1 =  getSharedPreferences("id_negocio", Context.MODE_PRIVATE)
        var id=sharedPreference1.getString("id","null")
        Url="http://192.168.0.20:4500/negocios/getnegocio/"+id
        URL="http://192.168.0.20:4500/menu/obtenerServicios/"+id
        Inicializar()
        InicializarDatos()


        recyclerView=findViewById(R.id.recycle_view4)
        recyclerView.layoutManager=layoutManager
        imagen_negocio.setOnClickListener {
            if (premium.equals("no")){
                if(ne.equals("0")){
                    editar()
                }else{
                    MaterialAlertDialogBuilder(this)
                        .setTitle("¿Desea Convertirse en usuario premium?")
                        .setMessage("Conozca las ventajas que ofrece ser usuario premium")
                        .setNeutralButton("No") { dialog, which ->
                            Toast.makeText(applicationContext, "No es usuario premium",Toast.LENGTH_SHORT).show()
                        }
                        .setPositiveButton("Si") { dialog, which ->
                            var intent1: Intent = Intent(applicationContext,PagoActivity::class.java)
                            startActivity(intent1)
                        }
                        .show()
                }
            }else{
                editar()
            }
        }

    }

   private fun checarPermisos(){
       if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
           && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
           ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE
           )
           return
       }
   }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar2, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       when(item.itemId){
           R.id.btnEditarp->{
               if (premium.equals("no")){
                   MaterialAlertDialogBuilder(this)
                       .setTitle("¿Desea Convertirse en usuario premium?")
                       .setMessage("Conozca las ventajas que ofrece ser usuario premium")
                       .setNeutralButton("No") { dialog, which ->
                           Toast.makeText(applicationContext, "No es usuario premium",Toast.LENGTH_SHORT).show()
                       }
                       .setPositiveButton("Si") { dialog, which ->
                           var intent1: Intent = Intent(applicationContext,PagoActivity::class.java)
                           startActivity(intent1)
                       }
                       .show()
               }else{
                   editorneg()
               }
           }
           R.id.Premium->{
               vercoor()
           }
           R.id.btnaddS->{
               if (premium.equals("no")){
                   MaterialAlertDialogBuilder(this)
                       .setTitle("¿Desea Convertirse en usuario premium?")
                       .setMessage("Conozca las ventajas que ofrece ser usuario premium")
                       .setNeutralButton("No") { dialog, which ->
                           Toast.makeText(applicationContext, "No es usuario premium",Toast.LENGTH_SHORT).show()
                       }
                       .setPositiveButton("Si") { dialog, which ->
                           var intent1: Intent = Intent(applicationContext,PagoActivity::class.java)
                           startActivity(intent1)
                       }
                       .show()
               }else{
                   val intent1: Intent = Intent(applicationContext,AgregarServicioActivity::class.java)
                   startActivity(intent1)
                   finish()
               }

           }
            R.id.btnEliminarN->{
                MaterialAlertDialogBuilder(this)
                    .setTitle("Desea Eliminar este Negocio?")
                    .setMessage("Esta accion ya no es reversible")
                    .setNeutralButton("Cancelar") { dialog, which ->
                        // Respond to neutral button press
                    }
                    .setPositiveButton("Aceptar") { dialog, which ->
                        val u="http://192.168.0.20:4500/negocios/eliminar_negocio/"+id
                        val jsonArrayRequest= JsonArrayRequest(u,
                            Response.Listener { response ->
                                Toast.makeText(applicationContext,"No se pudo eliminar el negocio", Toast.LENGTH_SHORT).show()
                            }, Response.ErrorListener { error ->

                                val sharedPreference =  getSharedPreferences("sesionesi", Context.MODE_PRIVATE)
                                var editor = sharedPreference.edit()
                                editor.putBoolean("negocio",false)
                                editor.commit()
                                var intent1: Intent = Intent(applicationContext,MainActivity::class.java)
                                startActivity(intent1)
                                finish()
                                Toast.makeText(applicationContext,"Negocio Eliminado",Toast.LENGTH_LONG).show()
                            });
                        requestQueue.add(jsonArrayRequest)

                    }
                    .show()
            }
           R.id.btnlogout2->{
               val sharedPreference =  getSharedPreferences("sesionesi", Context.MODE_PRIVATE)
               var editor = sharedPreference.edit()
               editor.putBoolean("negocio",false)
               editor.commit()
               val intent:Intent= Intent(applicationContext, MainActivity::class.java)
               startActivity(intent)
               finish()
           }
       }


        return super.onOptionsItemSelected(item)
    }



    private fun editar(){
        val intent1: Intent = Intent(applicationContext,ActImagenNegocioActivity::class.java)
        intent1.putExtra("imagen",im)
        intent1.putExtra("id",i)
        startActivity(intent1)
        finish()
    }


    private fun InicializarDatos(){
        val jsonArrayRequest= JsonArrayRequest(URL,
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

                            val intent1: Intent = Intent(applicationContext,ActualizarImagenServicioActivity::class.java)
                            intent1.putExtra("id",idn[position])
                            intent1.putExtra("nombre",nombre[position])
                            intent1.putExtra("precio",precio[position])
                            intent1.putExtra("imagen",imagenes[position])
                            intent1.putExtra("descripcion",descripcion[position])
                            intent1.putExtra("negocioid", negocioid[position])
                            startActivity(intent1)
                        }
                    })
                } catch (e: JSONException) {

                }

            }, Response.ErrorListener { error ->
            });
        requestQueue.add(jsonArrayRequest)
    }



    private fun vercoor(){

        val intent1: Intent = Intent(applicationContext,LocalizacionNegocioLU::class.java)
        intent1.putExtra("latitud",latitud)
        intent1.putExtra("longitud", longitud)
        intent1.putExtra("nombre", nombre)
        startActivity(intent1)
    }

    private fun editorneg(){
        val sharedPreference =  getSharedPreferences("datos_negocios2", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString("id",id)
        editor.putString("nombre",nombre)
        editor.putString("email",email)
        editor.putString("imagen",imagen)
        editor.putString("telefono",telefono)
        editor.putString("cp",cp)
        editor.putString("zona",zona)
        editor.putString("descripcion",descripcion)
        editor.putString("tipo",t)
        editor.commit()
        val intent1: Intent = Intent(applicationContext,EditarNegocioActivity::class.java)
        startActivity(intent1)
    }

    private fun Inicializar(){
        val jsonObjectRequest= JsonObjectRequest(Url,
            Response.Listener { response ->
                id=response.get("_id").toString()
                nombre=response.get("nombre").toString()
                email=response.get("email").toString()
                imagen=response.get("imagen").toString()
                telefono=response.get("telefono").toString()
                cp=response.get("codigopostal").toString()
                zona=response.get("zona").toString()
                descripcion=response.get("descripcion").toString()
                latitud=response.get("latitud").toString()
                longitud=response.get("longitud").toString()
                premium=response.get("premium").toString()
                t=response.get("tipo").toString()
                ne=response.get("numero_ediciones").toString()
                im=imagen;
                i=id;
                val sharedPreference =  getSharedPreferences("coordenadas", Context.MODE_PRIVATE)
                var editor = sharedPreference.edit()
                editor.putString("latitud",latitud)
                editor.putString("longitud",longitud)
                editor.commit()
                val sharedPreference2 =  getSharedPreferences("AgregarServicio", Context.MODE_PRIVATE)
                var editor2 = sharedPreference2.edit()
                editor2.putString("idnegocio",id)
                editor2.commit()
                tv_nombreNeg.setText(nombre)
                tv_cpNeg.setText(cp)
                tv_zonaNeg.setText(zona)
                tv_emailNeg.setText(email)
                tv_telefono.setText(telefono)
                tv_descripcion.setText(descripcion)
                tv_tiponegocio.setText(t)
                val respuestaConsulta = ImageRequest(
                    "http://192.168.0.20:4500/negocios/get_imagen/"+imagen,
                    { response ->
                        imagen_negocio.setImageBitmap(response)
                    }, 0, 0, null, null
                ) {
                    Toast.makeText(applicationContext,"No tiene Imagen",Toast.LENGTH_SHORT).show()
                }
                requestQueue.add(respuestaConsulta)
            }, Response.ErrorListener { error ->
                Toast.makeText(applicationContext,"Error al Autentificar",Toast.LENGTH_SHORT).show()
            });
        requestQueue.add(jsonObjectRequest)

    }
}