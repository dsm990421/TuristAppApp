package com.example.turistapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class ActualizarUsuarioActivity extends AppCompatActivity {
    ImageView ivFotoA;
    EditText etNombreAc, etTelefonoAc;
    Button btnActualizarU;
    Bitmap bitmap;
    RequestQueue requestQueue;
    String uri, enlace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_usuario);
        ivFotoA=(ImageView) findViewById(R.id.ivFotoA);
        etNombreAc=(EditText) findViewById(R.id.etNombreAc);
        etTelefonoAc=(EditText) findViewById(R.id.etTelefonoAc);
        btnActualizarU=(Button) findViewById(R.id.btnActualizarU);
        Intent intent1=getIntent();
        String nombre=intent1.getStringExtra("nombre");
        String telefono=intent1.getStringExtra("telefono");
        String id=intent1.getStringExtra("id");
        uri="http://192.168.0.20:4500/usuarios/subirimagen/"+id+"/";
        enlace="http://192.168.0.20:4500/usuarios/actualizar/"+id+"/";
        etNombreAc.setText(nombre);
        etTelefonoAc.setText(telefono);
        String URL=intent1.getStringExtra("imagen");
        RequestQueue servicioJson= Volley.newRequestQueue(this);
        ImageRequest respuesta = new ImageRequest(URL,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        ivFotoA.setImageBitmap(bitmap);
                    }
                }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        servicioJson.add(respuesta);

        ivFotoA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        btnActualizarU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActualizarDatos();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK && data!=null){
            try{

                Uri imageuri=data.getData();
                bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageuri);
                ivFotoA.setImageBitmap(bitmap);
                UploadImage();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "No se eligio una Imagen", Toast.LENGTH_SHORT).show();
        }
    }


    private void ActualizarDatos(){
        String nombre=etNombreAc.getText().toString();
        String telefono=etTelefonoAc.getText().toString();

        try {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("nombre",nombre);
            jsonObject.put("telefono",telefono);
            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.PUT, enlace, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(ActualizarUsuarioActivity.this, "Datos Actualizados con Exito", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ActualizarUsuarioActivity.this, "No se pudo actualizar al usuario", Toast.LENGTH_SHORT).show();
                }
            }
            );
            requestQueue= Volley.newRequestQueue(ActualizarUsuarioActivity.this);
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void UploadImage(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        String image= Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        String name= String.valueOf(Calendar.getInstance().getTimeInMillis());

        try{
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("imagen",image);
            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.POST, uri, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                                Toast.makeText(ActualizarUsuarioActivity.this, "Imagen Actualizada", Toast.LENGTH_SHORT).show();
                                                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ActualizarUsuarioActivity.this, "No se pudo subir la imagen", Toast.LENGTH_SHORT).show();
                }
            }
            );
            requestQueue= Volley.newRequestQueue(ActualizarUsuarioActivity.this);
            requestQueue.add(jsonObjectRequest);
        }catch (JSONException e){

        }
    }



}