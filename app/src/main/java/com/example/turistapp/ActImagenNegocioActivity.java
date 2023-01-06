package com.example.turistapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
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

public class ActImagenNegocioActivity extends AppCompatActivity {
    ImageView ivFotoAIN;
    Button btnRegresar;
    String imagen, id,url;
    Bitmap bitmap;
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_imagen_negocio);
        ivFotoAIN=(ImageView) findViewById(R.id.ivFotoAIN);
        btnRegresar=(Button) findViewById(R.id.btnRegresar);

        Intent intent1=getIntent();
        imagen=intent1.getStringExtra("imagen");
        id=intent1.getStringExtra("id");
        url="http://192.168.0.20:4500/negocios/subirimagen/"+id;
        String UR="http://192.168.0.20:4500/negocios/get_imagen/"+imagen;
        RequestQueue servicioJson= Volley.newRequestQueue(this);
        ImageRequest respuesta = new ImageRequest(UR,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        ivFotoAIN.setImageBitmap(bitmap);
                    }
                }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "No tiene Imagen", Toast.LENGTH_SHORT).show();
            }
        });
        servicioJson.add(respuesta);

        ivFotoAIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), HomeNegocioActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK && data!=null){
            try{

                Uri imageuri=data.getData();
                bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageuri);
                ivFotoAIN.setImageBitmap(bitmap);
                UploadImage();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "No se eligio una Imagen", Toast.LENGTH_SHORT).show();
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
            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                String u="http://192.168.0.20:4500/negocios/actualizar/"+id;
                                JSONObject jsonObject2=new JSONObject();
                                jsonObject2.put("numero_ediciones","1");
                                JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.PUT, u, jsonObject2,
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
                                requestQueue= Volley.newRequestQueue(ActImagenNegocioActivity.this);
                                requestQueue.add(jsonObjectRequest);
                            }catch (JSONException e){

                            }
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