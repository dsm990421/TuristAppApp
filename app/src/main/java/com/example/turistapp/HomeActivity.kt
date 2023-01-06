package com.example.turistapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.*
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.turistapp.databinding.ActivityHomeBinding
import com.example.turistapp.databinding.ActivityMainBinding
import com.example.turistapp.ui.ChatbotActivity
import com.facebook.login.LoginManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.json.JSONObject

class HomeActivity : AppCompatActivity(){
//Se agregan las variables a utilzar durante la programacion de la actividad
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var auth: FirebaseAuth
    private lateinit var id:String
    private lateinit var requestQueue: RequestQueue
    private var httpURI="http://192.168.0.20:4500/usuarios/getDatos"
    companion object{
        private const val LOCATION_REQUEST_CODE=1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        //Splash
        Thread.sleep(1000) // HACK:
        setTheme(R.style.Theme_TuristApp)
        super.onCreate(savedInstanceState)
        Toast.makeText(applicationContext,"Seleccione la zona que desea conocer",Toast.LENGTH_LONG).show()
        binding = ActivityHomeBinding.inflate(layoutInflater)
//        val view = binding.root
        setContentView(binding.root)

        requestQueue= Volley.newRequestQueue(this)
        //Configuracion del Appbar
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.busquedaFragment
            ),
            binding.drawerLayout
        )
        //Configuracion del navcontroller
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
        //Bottom Navigation
        binding.bottomNavigationView.setupWithNavController(navController)
        // DrawerLayout
        binding.navView.setupWithNavController(navController)
        binding.navView.setNavigationItemSelectedListener { navigation_drawer->
            when(navigation_drawer.itemId){
                R.id.one -> {
                    //Inicia el chatbot
                    startActivity(Intent(this, ChatbotActivity::class.java))
                    true
                }
                R.id.two -> {
                    //Inicia la actividad de registro de negocios
                    val intent1: Intent = Intent(applicationContext,RegistroNegociosActivity::class.java)
                    startActivity(intent1)
                    true
                }
                R.id.btnCerrarSesion -> {
                    //Se cierra la sesion del usuario
                    FirebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    Toast.makeText(this, "Cerrar Sesion", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        getDatos()
        //Enlaces
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE
            )
            return
        }
    }
    private fun getDatos(){//Este metodo recibe todos los datos del usuario que acaba de iniciar sesion
        val sharedPreference =  getSharedPreferences("datos_usuario",Context.MODE_PRIVATE)
        //Guarda los datos en un shared preferences
        id= sharedPreference.getString("id","Nombre")!!
        val jsonObject= JSONObject()
        jsonObject.put("id",id)
        val jsonObjectRequest= JsonObjectRequest(
            Request.Method.POST,httpURI,
            jsonObject,
            Response.Listener { response ->
                val id=response.get("id").toString()
                val nombre=response.get("nombre").toString()
                val email=response.get("email").toString()
                val imagen=response.get("imagen").toString()
                val telefono=response.get("telefono").toString()
                val sharedPreference =  getSharedPreferences("datos_usuario", MODE_PRIVATE)
                var editor = sharedPreference.edit()
                editor.putString("id",id)
                editor.putString("nombreu",nombre)
                editor.putString("email",email)
                editor.putString("imagen",imagen)
                editor.putString("telefono",telefono)
                editor.commit()
            }, Response.ErrorListener { error ->

            });
        requestQueue.add(jsonObjectRequest)
    }


    override fun onSupportNavigateUp(): Boolean {//Para agregar la barra superior
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //Para crear el menu de opciones
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.btnlogout-> {
                //Cierra la sesion del usuario
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                Toast.makeText(this, "Cerrar Sesion", Toast.LENGTH_SHORT).show()
            }
            R.id.ImUser-> {
                //Redigirige a la pantalla del usuario
                val intent1: Intent = Intent(applicationContext,UsuarioActivity::class.java)
                startActivity(intent1)
            }
        }
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}