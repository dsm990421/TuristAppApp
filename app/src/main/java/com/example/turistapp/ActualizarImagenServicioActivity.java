package com.example.turistapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class ActualizarImagenServicioActivity extends AppCompatActivity {
    Bitmap bitmap;
    RequestQueue requestQueue;
    ImageView ivFotoServicio;
    EditText etNombre_Servicio, etCostoServicio, etDescripcionServicio2;
    Button btnActualizarServicio,btnEliminarServicio;
    String id,nombre,precio,descripcion,URL,uri,getimagen,imagen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_imagen_servicio);
        ivFotoServicio=(ImageView) findViewById(R.id.ivFotoServicio);
        etNombre_Servicio=(EditText) findViewById(R.id.etNombre_Servicio);
        etCostoServicio=(EditText) findViewById(R.id.etCostoServicio);
        etDescripcionServicio2=(EditText) findViewById(R.id.etDescripcionServicio2);
        btnActualizarServicio=(Button) findViewById(R.id.btnActualizarServicio);
        btnEliminarServicio=(Button) findViewById(R.id.btnEliminarServicio);
        Intent intent1=getIntent();
        id=intent1.getStringExtra("id");
        nombre=intent1.getStringExtra("nombre");
        precio=intent1.getStringExtra("precio");
        descripcion=intent1.getStringExtra("descripcion");
        imagen=intent1.getStringExtra("imagen");
        etNombre_Servicio.setText(nombre);
        etCostoServicio.setText(precio);
        etDescripcionServicio2.setText(descripcion);
        URL="http://192.168.0.20:4500/menu/actualizar/"+id;
        uri="http://192.168.0.20:4500/menu/subirimagen/"+id+"/";
        getimagen="http://192.168.0.20:4500/menu/get_imagen/"+imagen;
        RequestQueue servicioJson= Volley.newRequestQueue(this);
        ImageRequest respuesta = new ImageRequest(getimagen,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        ivFotoServicio.setImageBitmap(bitmap);
                    }
                }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "No tiene Imagen", Toast.LENGTH_SHORT).show();
            }
        });
        servicioJson.add(respuesta);
        ivFotoServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        btnActualizarServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActualizarDatos();
            }
        });

        btnEliminarServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EliminarServicio();
            }
        });
    }

    protected void EliminarServicio(){

        new MaterialAlertDialogBuilder(this)
                .setTitle("Alerta")
                .setMessage("¿Desea eliminar este Servicio?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String en="http://192.168.0.20:4500/menu/eliminar/"+id;

                        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(en,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Toast.makeText(getApplicationContext(), "El servicio ha sido eliminado", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(getApplicationContext(), HomeNegocioActivity.class);
                                        startActivity(intent);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "No se pudo eliminar el servicio", Toast.LENGTH_SHORT).show();
                            }
                        }
                        );
                        requestQueue= Volley.newRequestQueue(ActualizarImagenServicioActivity.this);
                        requestQueue.add(jsonObjectRequest);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK && data!=null){
            try{

                Uri imageuri=data.getData();
                bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageuri);
                ivFotoServicio.setImageBitmap(bitmap);
                UploadImage();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "No se eligio una Imagen", Toast.LENGTH_SHORT).show();
        }


    }


    private void ActualizarDatos(){
        String n=etNombre_Servicio.getText().toString();
        String c=etCostoServicio.getText().toString();
        String d=etDescripcionServicio2.getText().toString();

        try {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("nombre",n);
            jsonObject.put("precio",c);
            jsonObject.put("descripcion",d);
            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.PUT, URL, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(getApplicationContext(), "Datos Actualizados con Exito", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(getApplicationContext(), HomeNegocioActivity.class);
                            startActivity(intent);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "No se pudo actualizar el Servicio", Toast.LENGTH_SHORT).show();
                }
            }
            );
            requestQueue= Volley.newRequestQueue(this);
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
                            Toast.makeText(getApplicationContext(), "Imagen Actualizada", Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "No se pudo subir la imagen", Toast.LENGTH_SHORT).show();
                }
            }
            );
            requestQueue= Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);
        }catch (JSONException e){

        }
    }




}