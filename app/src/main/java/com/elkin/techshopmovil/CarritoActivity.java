package com.elkin.techshopmovil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.elkin.techshopmovil.data.ApiClient;
import com.elkin.techshopmovil.data.SesionManager;
import com.elkin.techshopmovil.model.Carrito;
import com.elkin.techshopmovil.util.Navegacion;
import com.elkin.techshopmovil.util.Ventana;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

/** Carrito de compras y confirmación del pedido (módulo de pedidos). */
public class CarritoActivity extends AppCompatActivity {

    private SesionManager sesion;
    private LinearLayout contenedor;
    private TextView tvTotal;
    private TextView tvVacio;
    private TextView tvMensaje;
    private ProgressBar progreso;
    private Button btnConfirmar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);
        Ventana.ajustarBordes(findViewById(R.id.raiz));

        sesion = new SesionManager(this);
        contenedor = findViewById(R.id.contenedor);
        tvTotal = findViewById(R.id.tvTotal);
        tvVacio = findViewById(R.id.tvVacio);
        tvMensaje = findViewById(R.id.tvMensaje);
        progreso = findViewById(R.id.progreso);
        btnConfirmar = findViewById(R.id.btnConfirmar);

        btnConfirmar.setOnClickListener(v -> confirmar());
        findViewById(R.id.btnSeguir).setOnClickListener(v -> finish());
        mostrar();
    }

    private void mostrar() {
        contenedor.removeAllViews();
        List<Carrito.Linea> lineas = Carrito.lineas();
        boolean vacio = lineas.isEmpty();
        tvVacio.setVisibility(vacio ? View.VISIBLE : View.GONE);
        btnConfirmar.setEnabled(!vacio);

        for (Carrito.Linea linea : lineas) {
            View fila = getLayoutInflater().inflate(R.layout.item_carrito, contenedor, false);
            ((TextView) fila.findViewById(R.id.tvNombre)).setText(linea.producto.nombre);
            ((TextView) fila.findViewById(R.id.tvDetalle)).setText(String.format(Locale.US,
                    "%d x $ %.2f", linea.cantidad, linea.producto.precio));
            ((TextView) fila.findViewById(R.id.tvSubtotal)).setText(String.format(Locale.US,
                    "$ %.2f", linea.producto.precio * linea.cantidad));
            fila.findViewById(R.id.btnQuitar).setOnClickListener(v -> {
                Carrito.quitar(linea.producto.id);
                mostrar();
            });
            contenedor.addView(fila);
        }
        tvTotal.setText(String.format(Locale.US, "Total: $ %.2f", Carrito.totalPrecio()));
    }

    private void confirmar() {
        JSONObject cuerpo = new JSONObject();
        JSONArray items = new JSONArray();
        try {
            for (Carrito.Linea linea : Carrito.lineas()) {
                JSONObject item = new JSONObject();
                item.put("productoId", linea.producto.id);
                item.put("cantidad", linea.cantidad);
                items.put(item);
            }
            cuerpo.put("items", items);
        } catch (JSONException e) {
            mostrarMensaje("No se pudo preparar el pedido.");
            return;
        }
        enEspera(true);
        ApiClient.llamar("POST", "/api/pedidos", cuerpo, sesion.getToken(), respuesta -> {
            enEspera(false);
            if (respuesta.codigo == 401) {
                Navegacion.sesionVencida(this);
                return;
            }
            if (!respuesta.exitosa()) {
                mostrarMensaje(respuesta.mensaje());
                return;
            }
            Carrito.vaciar();
            Toast.makeText(this, "Pedido creado con éxito", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, PedidosActivity.class));
            finish();
        });
    }

    private void enEspera(boolean esperando) {
        progreso.setVisibility(esperando ? View.VISIBLE : View.GONE);
        btnConfirmar.setEnabled(!esperando);
        if (esperando) {
            tvMensaje.setVisibility(View.GONE);
        }
    }

    private void mostrarMensaje(String texto) {
        tvMensaje.setText(texto);
        tvMensaje.setVisibility(View.VISIBLE);
    }
}