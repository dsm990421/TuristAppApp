package com.example.turistapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

public class PagoActivity extends AppCompatActivity {
    Button btnContinuar,btnOmitir;
    PaymentSheet paymentSheet;
    String paymentIntentClientSecret;
    RequestQueue requestQueue;
    PaymentSheet.CustomerConfiguration customerConfig;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago);
        btnContinuar=(Button) findViewById(R.id.btnContinuar);
        btnOmitir=(Button) findViewById(R.id.btnOmitir);
        requestQueue= Volley.newRequestQueue(this);
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);
        ObtenerDatosPago();
        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (paymentIntentClientSecret != null) {
                    presentPaymentSheet();
                }
            }
        });

        btnOmitir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), HomeNegocioActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void presentPaymentSheet() {
        final PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("TuristAPP")
                .customer(customerConfig)
                // Set `allowsDelayedPaymentMethods` to true if your business can handle payment methods
                // that complete payment after a delay, like SEPA Debit and Sofort.
                .allowsDelayedPaymentMethods(true)
                .build();

        paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, configuration);
    }


    private void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(this, "Se cancelo la transaccion", Toast.LENGTH_LONG).show();

        } else if(paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Toast.makeText(this, ((PaymentSheetResult.Failed) paymentSheetResult).getError().getMessage(), Toast.LENGTH_LONG).show();

        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            ObtenerDatosPago();
            Toast.makeText(this, "Se ha completado el Pago!!!", Toast.LENGTH_LONG).show();
            SharedPreferences s1=getSharedPreferences("datos_negocio", Context.MODE_PRIVATE);
            id=s1.getString("id","id");
            String uri="http://192.168.0.20:4500/negocios/actualizar/"+id;
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("premium","si");
                JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.PUT, uri, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Intent intent=new Intent(getApplicationContext(), HomeNegocioActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(PagoActivity.this, "¡Ya eres usuario premium!", Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PagoActivity.this, "No se pudo actualizar al usuario", Toast.LENGTH_SHORT).show();
                    }
                }
                );
                requestQueue= Volley.newRequestQueue(this);
                requestQueue.add(jsonObjectRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void ObtenerDatosPago() {
        String URL =  "http://192.168.0.20:4500/pagos/pagar";
        StringRequest stringRequest = new StringRequest(URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            customerConfig = new PaymentSheet.CustomerConfiguration(
                                    res.getString("customer"),
                                    res.getString("ephemeralKey")
                            );
                            paymentIntentClientSecret = res.getString("paymentIntent");
                            PaymentConfiguration.init(getApplicationContext(), res.getString("publishableKey"));
                        } catch (JSONException e) { e.printStackTrace(); }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Ha ocurrido un error", Toast.LENGTH_LONG).show();
                    }
                }
        );
        requestQueue.add(stringRequest);
    }




}