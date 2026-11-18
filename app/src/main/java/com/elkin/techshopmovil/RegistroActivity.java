package com.elkin.techshopmovil;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.elkin.techshopmovil.data.ApiClient;
import com.elkin.techshopmovil.util.Ventana;

import org.json.JSONException;
import org.json.JSONObject;

/** Pantalla de registro de usuarios (módulo de autenticación). */
public class RegistroActivity extends AppCompatActivity {

    private EditText etUsuario;
    private EditText etContrasena;
    private EditText etConfirmar;
    private TextView tvMensaje;
    private ProgressBar progreso;
    private Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        Ventana.ajustarBordes(findViewById(R.id.raiz));

        etUsuario = findViewById(R.id.etUsuario);
        etContrasena = findViewById(R.id.etContrasena);
        etConfirmar = findViewById(R.id.etConfirmar);
        tvMensaje = findViewById(R.id.tvMensaje);
        progreso = findViewById(R.id.progreso);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(v -> registrar());
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
    }

    private void registrar() {
        String usuario = etUsuario.getText().toString().trim();
        String contrasena = etContrasena.getText().toString();
        String confirmar = etConfirmar.getText().toString();
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mostrarMensaje("Escribe un usuario y una contraseña.");
            return;
        }
        if (!contrasena.equals(confirmar)) {
            mostrarMensaje("Las contraseñas no coinciden.");
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
        ApiClient.llamar("POST", "/api/registro", cuerpo, null, respuesta -> {
            enEspera(false);
            if (!respuesta.exitosa()) {
                mostrarMensaje(respuesta.mensaje());
                return;
            }
            Toast.makeText(this, "Cuenta creada. Ahora inicia sesión.",
                    Toast.LENGTH_LONG).show();
            finish();
        });
    }

    private void enEspera(boolean esperando) {
        progreso.setVisibility(esperando ? View.VISIBLE : View.GONE);
        btnRegistrar.setEnabled(!esperando);
        if (esperando) {
            tvMensaje.setVisibility(View.GONE);
        }
    }

    private void mostrarMensaje(String texto) {
        tvMensaje.setText(texto);
        tvMensaje.setVisibility(View.VISIBLE);
    }
}