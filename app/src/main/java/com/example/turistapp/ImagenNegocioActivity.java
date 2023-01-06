package com.example.turistapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.auth.viewmodel.RequestCodes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class ImagenNegocioActivity extends AppCompatActivity {
private String uri;
ImageView ivimageViewC;
Button btncarga,btnSubir;
RequestQueue requestQueue;
JsonObjectRequest jsonObjectRequest;
Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen_negocio);
        SharedPreferences prefe= getSharedPreferences("datos_negocio",
                Context.MODE_PRIVATE);
        uri=prefe.getString("url", null);
        ivimageViewC=(ImageView) findViewById(R.id.ivimageViewC);
        btncarga=(Button) findViewById(R.id.btncarga);
        btnSubir=(Button) findViewById(R.id.btnSubir);

        btnSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            UploadImage();
            }
        });
        btncarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
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
                ivimageViewC.setImageBitmap(bitmap);
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
        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.POST, uri, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            SharedPreferences s1=getSharedPreferences("datos_negocio", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor= s1.edit();
                            String nombre= response.getString("image");
                            editor.putString("imagen",nombre);
                            editor.commit();
                            Toast.makeText(ImagenNegocioActivity.this, nombre, Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(getApplicationContext(), PrimerPantallaNegocioActivity.class);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ImagenNegocioActivity.this, "No se pudo subir la imagen", Toast.LENGTH_SHORT).show();
            }
        }
        );
            requestQueue= Volley.newRequestQueue(ImagenNegocioActivity.this);
            requestQueue.add(jsonObjectRequest);
        }catch (JSONException e){

        }
    }
}