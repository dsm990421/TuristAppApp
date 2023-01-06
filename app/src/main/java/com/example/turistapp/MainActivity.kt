package com.example.turistapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class MainActivity : AppCompatActivity() {
    //Se inicializan todas las variables que se van a utilizar durante la programacion de la actividad
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnEntrar: Button
    private lateinit var btnGoogle: Button
    private lateinit var btnFacebook: Button
    private lateinit var btnRegistro: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    private lateinit var requestQueue: RequestQueue
    //Se hace llamada a la URL de la api para verificiar si existe el usuario en la BD de MongoDB
    private var httpURI="http://192.168.0.20:4500/usuarios/login"
    private lateinit var btnLoginN:Button

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Se busca si existe ya iniciada una sesion con negocio
        val sharedPreference =  getSharedPreferences("sesionesi", Context.MODE_PRIVATE)
        var negocios=sharedPreference.getBoolean("negocio",false)//En caso de no existir se dara el valor false por defecto
            //Facebook
        if (negocios){//Si existe se iniciara el home del negocio que haya hecho login
            var intent1: Intent = Intent(applicationContext,HomeNegocioActivity::class.java)
            startActivity(intent1)
            finish()//Se finalizara la actividad Main
        }else{
        callbackManager = CallbackManager.Factory.create();//se instancia CallbackManager para el login de Facebook
        //Volley
        requestQueue=Volley.newRequestQueue(this)//Se inicializa la llamada a Volley
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()//Se instancia la autentificacion de Firebase
            if(auth.currentUser!=null){// Si hay algun usuario que haya iniciado sesion se dirigira al home del usuario
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }else{// en caso contrario se inicializaran las variables de inicio de sesion de Google
                val gso= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                googleSignInClient= GoogleSignIn.getClient(this,gso)
                //Enlaces a la vista
                etEmail = findViewById<EditText>(R.id.etEmail)
                etPassword = findViewById<EditText>(R.id.etPassword)
                btnEntrar = findViewById<Button>(R.id.btnEntrar)
                btnGoogle = findViewById<Button>(R.id.btnGoogle)
                btnFacebook = findViewById<Button>(R.id.btnFacebook)
                btnRegistro = findViewById<Button>(R.id.btnRegistro)
                btnLoginN=findViewById(R.id.btnLoginN)
                googleSignInClient.signOut()//Si existe algun usuario que por alguna razon haya hecho login pero no se procesara bien
                //se cerrara su sesion
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                //Oyente del boton Registro para la actividad de registro de usuario
                btnRegistro.setOnClickListener {
                    val intent:Intent=Intent(this, RegistroActivity::class.java)
                    startActivity(intent)
                }
                btnGoogle.setOnClickListener { signInGoogle(); }//Se inicia el proceso de Login por medio de Google
                btnEntrar.setOnClickListener { consultaUsuario() }//Se inicia el proceso de Login por medio de Email
                btnFacebook.setOnClickListener { loginFacebook() }//Se inicia el proceso de Login por medio de Facebook
                btnLoginN.setOnClickListener { IrAnegocio() }//Se inicia la actividad para Login de Negocio
            }
        }

    }

    private fun IrAnegocio(){
        var intent1: Intent = Intent(applicationContext,LoginNegocioActivity::class.java)
        startActivity(intent1)
        finish()
    }


    private fun loginFacebook(){
        //Se inicia el proceso de login de facebook haciendo llamada a el metodo para entrar con permisos de lectura
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
        //Hace un callback el cual regresara dos casos, el de exito y el de fallo
        LoginManager.getInstance().registerCallback(callbackManager, object:
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {//Regresara el resultado de login
                result?.let {//Caso de exito
                    val token=it.accessToken//Se recibe el token de acceso generado
                    val credential= FacebookAuthProvider.getCredential(token.token)//Envia el token para recibir las credenciales de inicio
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        //En firebase se registrara el usuario con el que se acceso
                        if (it.isSuccessful){//En caso de ser correcto el login
                            val sharedPreference =  getSharedPreferences("tipo_inicio",Context.MODE_PRIVATE)
                            var editor = sharedPreference.edit()
                            editor.putString("sesion","facebook")

                            editor.commit()
                            var intent1: Intent = Intent(applicationContext,HomeActivity::class.java)
                            startActivity(intent1)
                            finish()
                        }else{//Si no es correcto
                            Toast.makeText(applicationContext,"Error",Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            override fun onCancel() {
            }
            override fun onError(error: FacebookException?) {
            }
        })
    }

    private fun consultaUsuario(){
        val email=etEmail.text.toString()
        val password=etPassword.text.toString()
        val jsonObject=JSONObject()//Se  inicializa un objeto JSON que contendra los datos de la solicitud a Volley
        jsonObject.put("email",email)
        jsonObject.put("password",password)
        val jsonObjectRequest=JsonObjectRequest(//Se hace una peticion de un JSON al servidor
            Request.Method.POST,httpURI,
            jsonObject,//Se envian los datos de la URL, el objeto con los datos de la peticion y el metodo de la llamada
            Response.Listener { response ->//En response se encuentra el JSON recibido en caso de exito
                //Se guardan en variables los datos de la respuesta y dentro de shared preferences que se utilizaran mas adelante
                //Tambien se guardara una variable con el tipo de inicio de sesion
                val id=response.get("id").toString()
                val nombre=response.get("nombre").toString()
                val email=response.get("email").toString()
                val sharedPreference2 =  getSharedPreferences("tipo_inicio",Context.MODE_PRIVATE)
                var editor2 = sharedPreference2.edit()
                editor2.putString("sesion","email")
                editor2.commit()
                val sharedPreference =  getSharedPreferences("datos_usuario",Context.MODE_PRIVATE)
                var editor = sharedPreference.edit()
                editor.putString("id",id)
                editor.putString("nombreu",nombre)
                editor.putString("email",email)
                editor.commit()
                IniciarSesion()
             }, Response.ErrorListener { error ->
                Toast.makeText(applicationContext,"Error al Autentificar",Toast.LENGTH_SHORT).show()
            });
        requestQueue.add(jsonObjectRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {//Se hace llamada en caso de resultado
        //dentro del callbackManager
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun signInGoogle(){
        //Para el login de Google se inicia un intent de inicio de sesion
        val signInIntent=googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }
    private var launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        //Si tiene exito la solicitud de inicio de sesion se guardara el resultado
            result->
        if (result.resultCode== Activity.RESULT_OK){
            val task= GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }
    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account: GoogleSignInAccount?= task.result
            if (account!=null){
                updateUI(account)
            }
        }else{
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        //Se actualizara la credencial del inicio de sesion con el token para registrarnos posteriormente en Firebase con el usuario
        val credential= GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener{
            if (it.isSuccessful){
                //Si es correcto se guarda el tipo de inicio de sesion y se iniciara el home activity
                val sharedPreference =  getSharedPreferences("tipo_inicio",Context.MODE_PRIVATE)
                var editor = sharedPreference.edit()
                editor.putString("sesion","google")
                editor.commit()
                val intent:Intent=Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun IniciarSesion(){
        //Se iniciara sesion en Firebase con los datos introducidos en los EditText
        var email= etEmail.text.toString();
        var password = etPassword.text.toString();
        if(email.isEmpty()|| password.isEmpty()){
            Toast.makeText(baseContext, "Debe introducir todos los datos", Toast.LENGTH_SHORT).show()
        }else{
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        val sharedPreference2 =  getSharedPreferences("tipo_inicio",Context.MODE_PRIVATE)
                        var editor2 = sharedPreference2.edit()
                        editor2.putString("sesion","email")
                        editor2.commit()
                        var intent1: Intent = Intent(applicationContext,HomeActivity::class.java)
                        startActivity(intent1)
                        finish()
                    }
                    else Toast.makeText(this,"Error de Autentificacion",Toast.LENGTH_LONG).show()
                }

        }

    }




//    private fun savedata(email:String){
//        val intent1: Intent = Intent(this,HomeActivity::class.java).apply {
//            putExtra("email", email)
//        }
//        startActivity(intent1)
//    }
}