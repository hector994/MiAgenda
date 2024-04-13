package com.example.miagenda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button botonAgregar, botonBuscar;
    ListView listaContactos;
    EditText txtCriterio;
    Configuraciones objConfiguracion = new Configuraciones();
    String URL = objConfiguracion.URLserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        botonAgregar = findViewById(R.id.button2);
        botonAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ventana = new Intent(MainActivity.this, RegistrarContacto.class);
                startActivity(ventana);
            }
        });

        txtCriterio = findViewById(R.id.editTextText);
        listaContactos = findViewById(R.id.listView);

        botonBuscar = findViewById(R.id.button);
        botonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llenarLista();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            llenarLista();
        }catch (Exception error){
            Toast.makeText(MainActivity.this, "Error: "+ error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void llenarLista(){
        final String criterio = txtCriterio.getText().toString();
        RequestQueue objetoPeticion = Volley.newRequestQueue(MainActivity.this);
        StringRequest peticion = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject objJSONResultado = new JSONObject(response.toString());
                    JSONArray aDatosResultado = objJSONResultado.getJSONArray("resultado");

                    AdaptadorListaContacto miAdaptador = new AdaptadorListaContacto();
                    miAdaptador.arregloDatos = aDatosResultado;
                    listaContactos.setAdapter(miAdaptador);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("accion","listar");
                params.put("filtro",criterio);
                return params;
            }
        };

        objetoPeticion.add(peticion);


    }

    class AdaptadorListaContacto extends BaseAdapter {
        public JSONArray arregloDatos;

        @Override
        public int getCount() {
            return arregloDatos.length();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            v = getLayoutInflater().inflate(R.layout.fila_contactos,null);
            TextView txtTitulo = v.findViewById(R.id.tvTituloFilaContacto);
            TextView txtTelefono = v.findViewById(R.id.tvTelefonoFilaContacto);
            Button btnVer = v.findViewById(R.id.btnVerContacto);

            JSONObject objJSON = null;
            try{
                objJSON = arregloDatos.getJSONObject(position);
                final String id_contacto, nombre, telefono;
                id_contacto = objJSON.getString("id_contacto");
                nombre = objJSON.getString("nombre");
                telefono = objJSON.getString("telefono");

                txtTitulo.setText(nombre);
                txtTelefono.setText(telefono);;

                btnVer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent ventanaModificar = new Intent(MainActivity.this, ModificarContacto.class);
                        ventanaModificar.putExtra("id_contacto", id_contacto);
                        ventanaModificar.putExtra("nombre", nombre);
                        ventanaModificar.putExtra("telefono", telefono);
                        startActivity(ventanaModificar);
                    }
                });

            }catch (JSONException e){
                e.printStackTrace();
            }
            return v;
        }
    }
}
