package com.example.turistapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.facebook.Profile
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.net.URL


class UsuarioActivity : AppCompatActivity() {
    private lateinit var etNombreU:TextView
    private lateinit var etEmailU:TextView
    private lateinit var requestQueue: RequestQueue
    private lateinit var ivFoto:ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var btnEliminar:Button
    private lateinit var etTelef:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuario)
        etNombreU=findViewById(R.id.etNombreU)
        etEmailU=findViewById(R.id.etEmailU)
        ivFoto=findViewById(R.id.ivFoto)
        etTelef=findViewById(R.id.etTelef)
        btnEliminar=findViewById(R.id.btnEliminar)
        val sharedPreference =  getSharedPreferences("datos_usuario", Context.MODE_PRIVATE)
        val nombre= sharedPreference.getString("nombreu","Nombre")!!
        val id=sharedPreference.getString("id","id")!!
        val email=sharedPreference.getString("email","Email")!!
        val imagen=sharedPreference.getString("imagen","Email")!!
        val telefono=sharedPreference.getString("telefono","Telefono")!!
        val sharedPreference2 =  getSharedPreferences("tipo_inicio", Context.MODE_PRIVATE)
        val sesion=sharedPreference2.getString("sesion","null")
        if (sesion.equals("facebook")){
            var imageURL = URL("https://graph.facebook.com/" + Profile.getCurrentProfile().id + "/picture?width=" + 500 +"&height=" + 500);
            Picasso.with(this).load(imageURL.toString()).fit().into(ivFoto);
            val nombrez=Profile.getCurrentProfile().name
            auth = FirebaseAuth.getInstance()
            //Enlaces
            val currentUser = auth.currentUser
            var email= currentUser?.email
            var tel=currentUser?.phoneNumber
            etTelef.setText(tel)
            etNombreU.setText(nombrez)
            etEmailU.setText(email)
            btnEliminar.setOnClickListener {

                MaterialAlertDialogBuilder(this)
                    .setTitle("Desea Eliminar este Usuario?")
                    .setMessage("Esta accion ya no es reversible")
                    .setNeutralButton("Cancelar") { dialog, which ->
                        // Respond to neutral button press
                    }
                    .setPositiveButton("Aceptar") { dialog, which ->
                        if (currentUser != null) {
                            currentUser.delete()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        var intent1: Intent = Intent(applicationContext,MainActivity::class.java)
                                        startActivity(intent1)
                                        finish()
                                        Toast.makeText(applicationContext,"Usuario Eliminado", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }
                    .show()
            }
        }else if(sesion.equals("google")){
            btnEliminar.setOnClickListener {
                auth = FirebaseAuth.getInstance()
                //Enlaces
                val currentUser = auth.currentUser

                MaterialAlertDialogBuilder(this)
                    .setTitle("Desea Eliminar este Usuario?")
                    .setMessage("Esta accion ya no es reversible")
                    .setNeutralButton("Cancelar") { dialog, which ->
                        // Respond to neutral button press
                    }
                    .setPositiveButton("Aceptar") { dialog, which ->
                        if (currentUser != null) {
                            currentUser.delete()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        var intent1: Intent = Intent(applicationContext,MainActivity::class.java)
                                        startActivity(intent1)
                                        finish()
                                        Toast.makeText(applicationContext,"Usuario Eliminado", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }
                    .show()
            }
            val acct = GoogleSignIn.getLastSignedInAccount(this)
            val user = FirebaseAuth.getInstance().currentUser
            val email=user!!.email
            val nombre=user.displayName
            val tel=user.phoneNumber
            val URL=acct!!.photoUrl.toString()
            val newUri=URL.dropLast(6)
            Picasso.with(this)
                .load(newUri)
                .resize(500, 500)
                .centerCrop()
                .into(ivFoto);
            etTelef.setText(tel)
            etEmailU.setText(email)
            etNombreU.setText(nombre)
        }else if (sesion.equals("email")){
            ivFoto.setImageResource(R.drawable.de)
            requestQueue= Volley.newRequestQueue(this)
            val URL="http://192.168.0.20:4500/usuarios/get_imagen/"+imagen;
            if(!imagen.equals("Email")){
                val respuestaConsulta = ImageRequest(
                    URL,
                    { response ->
                        ivFoto.setImageBitmap(response)
                    }, 0, 0, null, null
                ) {

                }
                requestQueue.add(respuestaConsulta)
            }
            etNombreU.setText(nombre)
            etEmailU.setText(email)
            etTelef.setText(telefono)
            ivFoto.setOnClickListener{
                val intent1: Intent = Intent(applicationContext,ActualizarUsuarioActivity::class.java)
                intent1.putExtra("imagen",URL)
                intent1.putExtra("id",id)
                intent1.putExtra("nombre",nombre)
                intent1.putExtra("telefono", telefono)
                startActivity(intent1)
                finish()
            }


            btnEliminar.setOnClickListener {


                MaterialAlertDialogBuilder(this)
                    .setTitle("Desea Eliminar este Usuario?")
                    .setMessage("Esta accion ya no es reversible")
                    .setNeutralButton("Cancelar") { dialog, which ->
                        // Respond to neutral button press
                    }
                    .setPositiveButton("Aceptar") { dialog, which ->
                        auth = FirebaseAuth.getInstance()
                        //Enlaces
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            currentUser.delete()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val ur="http://192.168.0.20:4500/usuarios/eliminar/"+id
                                        requestQueue= Volley.newRequestQueue(this)
                                        val jsonArrayRequest= JsonArrayRequest(ur,
                                            Response.Listener { response ->
                                                Toast.makeText(applicationContext,"No se pudo eliminar el Usuario",Toast.LENGTH_SHORT).show()
                                            }, Response.ErrorListener { error ->
                                                Toast.makeText(applicationContext,"Usuario Eliminado",Toast.LENGTH_SHORT).show()
                                                var intent1: Intent = Intent(applicationContext,MainActivity::class.java)
                                                startActivity(intent1)
                                                finish()
                                            });
                                        requestQueue.add(jsonArrayRequest)
                                    }
                                }
                        }
                    }
                    .show()


            }
        }
    }
}