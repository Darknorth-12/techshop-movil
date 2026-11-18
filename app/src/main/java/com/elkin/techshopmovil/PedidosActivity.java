package com.elkin.techshopmovil;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.elkin.techshopmovil.data.ApiClient;
import com.elkin.techshopmovil.data.SesionManager;
import com.elkin.techshopmovil.util.Json;
import com.elkin.techshopmovil.util.Navegacion;
import com.elkin.techshopmovil.util.Ventana;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;

/** Lista de pedidos del usuario y cancelación (módulo de pedidos). */
public class PedidosActivity extends AppCompatActivity {

    private SesionManager sesion;
    private LinearLayout contenedor;
    private TextView tvEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        Ventana.ajustarBordes(findViewById(R.id.raiz));

        sesion = new SesionManager(this);
        contenedor = findViewById(R.id.contenedor);
        tvEstado = findViewById(R.id.tvEstado);
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
        cargar();
    }

    private void cargar() {
        ApiClient.llamar("GET", "/api/pedidos", null, sesion.getToken(), respuesta -> {
            if (respuesta.codigo == 401) {
                Navegacion.sesionVencida(this);
                return;
            }
            contenedor.removeAllViews();
            if (!respuesta.exitosa()) {
                mostrarEstado(respuesta.mensaje());
                return;
            }
            JSONArray pedidos = Json.buscarArreglo(respuesta.cuerpo);
            if (pedidos == null || pedidos.length() == 0) {
                mostrarEstado("Aún no tienes pedidos.");
                return;
            }
            tvEstado.setVisibility(View.GONE);
            for (int i = 0; i < pedidos.length(); i++) {
                JSONObject pedido = pedidos.optJSONObject(i);
                if (pedido != null) {
                    contenedor.addView(crearFila(pedido));
                }
            }
        });
    }

    private View crearFila(JSONObject pedido) {
        View fila = getLayoutInflater().inflate(R.layout.item_pedido, contenedor, false);
        int id = pedido.optInt("id");
        String estado = pedido.optString("estado", "");

        ((TextView) fila.findViewById(R.id.tvTitulo)).setText("Pedido #" + id);
        ((TextView) fila.findViewById(R.id.tvEstado)).setText(estado.isEmpty() ? ""
                : estado.substring(0, 1).toUpperCase() + estado.substring(1));
        ((TextView) fila.findViewById(R.id.tvTotal)).setText(String.format(Locale.US,
                "Total: $ %.2f", pedido.optDouble("total")));

        TextView tvFecha = fila.findViewById(R.id.tvFecha);
        String fecha = pedido.optString("fechaCreacion", pedido.optString("fecha", ""));
        if (fecha.length() >= 10) {
            fecha = fecha.substring(0, 10);
        }
        if (fecha.isEmpty()) {
            tvFecha.setVisibility(View.GONE);
        } else {
            tvFecha.setText("Fecha: " + fecha);
        }

        JSONArray lineas = pedido.optJSONArray("items");
        if (lineas == null) {
            lineas = pedido.optJSONArray("lineas");
        }
        StringBuilder texto = new StringBuilder();
        if (lineas != null) {
            for (int i = 0; i < lineas.length(); i++) {
                JSONObject linea = lineas.optJSONObject(i);
                if (linea != null) {
                    if (texto.length() > 0) {
                        texto.append("\n");
                    }
                    texto.append("• ").append(linea.optString("nombre", "Producto"))
                            .append(" x").append(linea.optInt("cantidad"));
                }
            }
        }
        ((TextView) fila.findViewById(R.id.tvLineas)).setText(texto.toString());

        Button btnCancelar = fila.findViewById(R.id.btnCancelar);
        if ("pendiente".equalsIgnoreCase(estado)) {
            btnCancelar.setVisibility(View.VISIBLE);
            btnCancelar.setOnClickListener(v -> cancelar(id));
        }
        return fila;
    }

    private void cancelar(int id) {
        ApiClient.llamar("PATCH", "/api/pedidos/" + id + "/cancelar", null,
                sesion.getToken(), respuesta -> {
                    if (respuesta.codigo == 401) {
                        Navegacion.sesionVencida(this);
                        return;
                    }
                    Toast.makeText(this, respuesta.exitosa()
                            ? "Pedido cancelado" : respuesta.mensaje(), Toast.LENGTH_LONG).show();
                    cargar();
                });
    }

    private void mostrarEstado(String texto) {
        tvEstado.setText(texto);
        tvEstado.setVisibility(View.VISIBLE);
    }
}