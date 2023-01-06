package com.example.turistapp

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class LocalizacionNegocioLU : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var mMap:GoogleMap
    private lateinit var lastlocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var latitud:String
    private lateinit var longitud:String
    private lateinit var nombre:String
    companion object{
        private const val LOCATION_REQUEST_CODE=1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_localizacion_negocio_lu)
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this)
        val mapFragment= supportFragmentManager
            .findFragmentById(R.id.fgMapaLocalizacion) as SupportMapFragment
        mapFragment.getMapAsync(this)
        val bundle:Bundle?=intent.extras
        nombre=bundle!!.get("nombre").toString()
        latitud=bundle!!.get("latitud").toString()
        longitud=bundle!!.get("longitud").toString()
        Toast.makeText(applicationContext,"Para mas detalles dar clic en el marcador", Toast.LENGTH_LONG).show()

    }

    override fun onMapReady(googleMap: GoogleMap) {
        val lat=latitud.toDouble()
        val lon=longitud.toDouble()
        mMap=googleMap
        val sydney=LatLng(lat,lon)
        mMap.addMarker(MarkerOptions().position(sydney).title(nombre))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,150f))
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        TODO("Not yet implemented")
    }
}