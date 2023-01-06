package com.example.turistapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class CoordenadasActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {//Se agrega herencia de dos clases de Maps
    //Variables para utilizar en la programacion
    private lateinit var mMap:GoogleMap
    private lateinit var lastlocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var latitud:String
    private lateinit var longitud:String
    private lateinit var btnAceptar:Button

    companion object{//Variable con el codigo de solicitud de localizacion
        private const val LOCATION_REQUEST_CODE=1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coordenadas)
        //Se enlazan las llamadas
        btnAceptar=findViewById(R.id.btnAceptarC)
        //Se inicializa la variable para localizar
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this)
        val mapFragment= supportFragmentManager//Se inicializa el fragment de Google Maps
            .findFragmentById(R.id.fgMapaRegistro) as SupportMapFragment
        mapFragment.getMapAsync(this)
        btnAceptar.setOnClickListener { RegresarDatos(); }
    }

    private fun RegresarDatos(){//Para regresar los datos se regresara a la pantalla de registro de negocios
        val intent: Intent = Intent(this, RegistroNegociosActivity::class.java)
        startActivity(intent)
        finish()
    }
    override fun onMapReady(googleMap: GoogleMap) {//El metodo que se ejecuta una vez este listo el mapa
        mMap=googleMap
        mMap.uiSettings.isZoomControlsEnabled=true
        mMap.setOnMarkerClickListener(this)
        setUpMap()
    }
    private fun setUpMap(){
        //Checa los permisos
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            return
        }
        mMap.isMyLocationEnabled=true
        fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { Location->//Si es exitosa la localizacion se pondra
            //una marca donde fue la ultima localizacion del usuario
            if (Location!=null){
                lastlocation=Location
                val currentlatlong= LatLng(Location.latitude, Location.longitude)
                latitud=Location.latitude.toString()
                longitud=Location.longitude.toString()
                placeMarker(currentlatlong)//Se coloca la marca
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentlatlong,100f))//Se muestra con un acercamiento de 100
                val sharedPreference =  getSharedPreferences("coordenadas", Context.MODE_PRIVATE)//Se guardan las coordenadas para posteriormente checarlas
                var editor = sharedPreference.edit()
                editor.putString("latitud",latitud)
                editor.putString("longitud",longitud)
                editor.commit()
            }
        }
    }
    private fun placeMarker(currentlatLong: LatLng){//Se coloca la marca con la leyenda aqui se encuentra su negocio
        val markerOptions= MarkerOptions().position(currentlatLong)
        markerOptions.title("Aqui se encuentra su negocio!!")
        mMap.addMarker(markerOptions)
    }

    override fun onMarkerClick(p0: Marker): Boolean = false
}