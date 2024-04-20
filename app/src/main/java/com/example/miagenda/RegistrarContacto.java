package com.example.miagenda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegistrarContacto extends AppCompatActivity {
    EditText nombre, telefono;
    Button botonAgregar, botonRegresar;
    Configuraciones objConfiguracion = new Configuraciones();
    String URL = objConfiguracion.URLserver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_contacto);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.baseline_book_24);
        setTitle("My Agenda");

        nombre = findViewById(R.id.txtNombreCompleto);
        telefono = findViewById(R.id.txtTelefono);
        botonAgregar = findViewById(R.id.btnGuardarContacto);
        botonRegresar = findViewById(R.id.btnRegresar);

        //Evento (CLICK)
        botonAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Registrar
                registrar();
            }
        });

        botonRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Regresar
                regresar();
            }
        });
    }

    private void regresar(){
        Intent actividad = new Intent(RegistrarContacto.this,MainActivity.class);
        startActivity(actividad);
        RegistrarContacto.this.finish();
    }

    private void registrar(){
        try{
            RequestQueue objetoPeticion = Volley.newRequestQueue(RegistrarContacto.this);
            StringRequest peticion = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        JSONObject objJSONResultado = new JSONObject(response.toString());
                        String estado = objJSONResultado.getString("estado");
                        if(estado.equals("1")){
                            Toast.makeText(RegistrarContacto.this, "Contacto Registrado con exito", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(RegistrarContacto.this, "Error: "+ estado, Toast.LENGTH_SHORT).show();
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(RegistrarContacto.this, "Error: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String,String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String,String>();
                    params.put("accion","registrar");
                    params.put("nombre",nombre.getText().toString());
                    params.put("telefono",telefono.getText().toString());
                    return params;
                }
            };

            objetoPeticion.add(peticion);
        }catch (Exception error){
            Toast.makeText(RegistrarContacto.this, "Error en tiempo de ejecucion: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}
