package com.example.miagenda;

import android.annotation.SuppressLint;
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
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ModificarContacto extends AppCompatActivity {
    EditText nombre, telefono;
    Button botonAgregar, botonRegresar, botonEliminar;
    String id_contacto, nombre_contacto, telefono_contacto;
    Configuraciones objConfiguracion = new Configuraciones();
    String URL = objConfiguracion.URLserver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_modificar_contacto);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.baseline_book_24);
        setTitle("My Agenda");

        nombre = findViewById(R.id.txtNombreCompletoEditar);
        telefono = findViewById(R.id.txtTelefonoEditar);

        botonAgregar = findViewById(R.id.btnGuardarContactoEditar);
        botonRegresar = findViewById(R.id.btnRegresarEditar);
        botonEliminar = findViewById(R.id.btnEliminarEditar);

        botonAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modificar();
            }
        });

        botonRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regresar();
            }
        });

        botonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminar();
            }
        });
    }

    private void modificar(){
        try{
            final RequestQueue objetoPeticion = Volley.newRequestQueue(ModificarContacto.this);
            StringRequest peticion = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(String response) {
                    try{
                        JSONObject objJSONResultado = new JSONObject(response.toString());
                        String estado = objJSONResultado.getString("estado");
                        if(estado.equals("1")){
                            Toast.makeText(ModificarContacto.this, "Contacto Modificado con exito", Toast.LENGTH_SHORT).show();
                            objetoPeticion.stop();
                        }else{
                            Toast.makeText(ModificarContacto.this, "Error: "+ estado, Toast.LENGTH_SHORT).show();
                            objetoPeticion.stop();
                        }
                        objetoPeticion.stop();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    objetoPeticion.stop();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ModificarContacto.this, "Error: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    objetoPeticion.stop();
                }
            }){
                @Override
                protected Map<String,String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String,String>();
                    params.put("accion","modificar");
                    params.put("id_contacto",id_contacto);
                    params.put("nombre",nombre.getText().toString());
                    params.put("telefono",telefono.getText().toString());
                    return params;
                }
            };

            objetoPeticion.add(peticion);
        }catch (Exception error){
            Toast.makeText(ModificarContacto.this, "Error en tiempo de ejecucion: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void regresar(){
        Intent actividad = new Intent(ModificarContacto.this, MainActivity.class);
        startActivity(actividad);
        ModificarContacto.this.finish();
    }

    private void eliminar(){
        try{
            RequestQueue objetoPeticion = Volley.newRequestQueue(ModificarContacto.this);
            StringRequest peticion = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        JSONObject objJSONResultado = new JSONObject(response.toString());
                        String estado = objJSONResultado.getString("estado");
                        if(estado.equals("1")){
                            Toast.makeText(ModificarContacto.this, "Contacto Eliminado con exito", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ModificarContacto.this, "Error: "+ estado, Toast.LENGTH_SHORT).show();
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ModificarContacto.this, "Error: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String,String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String,String>();
                    params.put("accion","eliminar");
                    params.put("id_contacto",id_contacto);
                    return params;
                }
            };

            objetoPeticion.add(peticion);
        }catch (Exception error){
            Toast.makeText(ModificarContacto.this, "Error en tiempo de ejecucion: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle valoresAdicionales = getIntent().getExtras();
        if(valoresAdicionales==null){
            Toast.makeText(ModificarContacto.this, "Debe enviar un ID de contacto", Toast.LENGTH_SHORT).show();
            id_contacto = "";
            regresar();
        }else{
            id_contacto = valoresAdicionales.getString("id_contacto");
            nombre_contacto = valoresAdicionales.getString("nombre");
            telefono_contacto = valoresAdicionales.getString("telefono");
            verContacto();
        }
    }

    private void verContacto(){
        nombre.setText(nombre_contacto);
        telefono.setText(telefono_contacto);
    }

}