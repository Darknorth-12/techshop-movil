package com.elkin.techshopmovil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.elkin.techshopmovil.data.ApiClient;
import com.elkin.techshopmovil.data.SesionManager;
import com.elkin.techshopmovil.util.Ventana;

import org.json.JSONException;
import org.json.JSONObject;

/** Pantalla de inicio de sesión (módulo de autenticación). */
public class MainActivity extends AppCompatActivity {

    private EditText etUsuario;
    private EditText etContrasena;
    private TextView tvMensaje;
    private ProgressBar progreso;
    private Button btnIngresar;
    private SesionManager sesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sesion = new SesionManager(this);
        if (sesion.haySesion()) {
            irAlCatalogo();
            return;
        }
        setContentView(R.layout.activity_main);
        Ventana.ajustarBordes(findViewById(R.id.raiz));

        etUsuario = findViewById(R.id.etUsuario);
        etContrasena = findViewById(R.id.etContrasena);
        tvMensaje = findViewById(R.id.tvMensaje);
        progreso = findViewById(R.id.progreso);
        btnIngresar = findViewById(R.id.btnIngresar);

        btnIngresar.setOnClickListener(v -> iniciarSesion());
        findViewById(R.id.btnIrRegistro).setOnClickListener(
                v -> startActivity(new Intent(this, RegistroActivity.class)));
    }

    private void iniciarSesion() {
        String usuario = etUsuario.getText().toString().trim();
        String contrasena = etContrasena.getText().toString();
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mostrarMensaje("Escribe tu usuario y tu contraseña.");
            return;
        }
        JSONObject cuerpo = new JSONObject();
        try {
            cuerpo.put("usuario", usuario);
            cuerpo.put("contrasena", contrasena);
        } catch (JSONException e) {
            mostrarMensaje("No se pudo preparar la solicitud.");
            return;
        }
        enEspera(true);
        ApiClient.llamar("POST", "/api/login", cuerpo, null, respuesta -> {
            enEspera(false);
            if (!respuesta.exitosa()) {
                mostrarMensaje(respuesta.mensaje());
                return;
            }
            JSONObject datos = respuesta.cuerpo.optJSONObject("usuario");
            sesion.guardar(respuesta.cuerpo.optString("token"),
                    datos != null ? datos.optString("usuario") : usuario,
                    datos != null ? datos.optString("rol") : "");
            irAlCatalogo();
        });
    }

    private void enEspera(boolean esperando) {
        progreso.setVisibility(esperando ? View.VISIBLE : View.GONE);
        btnIngresar.setEnabled(!esperando);
        if (esperando) {
            tvMensaje.setVisibility(View.GONE);
        }
    }

    private void mostrarMensaje(String texto) {
        tvMensaje.setText(texto);
        tvMensaje.setVisibility(View.VISIBLE);
    }

    private void irAlCatalogo() {
        startActivity(new Intent(this, CatalogoActivity.class));
        finish();
    }
}