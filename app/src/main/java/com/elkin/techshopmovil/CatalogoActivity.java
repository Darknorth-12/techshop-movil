package com.elkin.techshopmovil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elkin.techshopmovil.data.ApiClient;
import com.elkin.techshopmovil.data.SesionManager;
import com.elkin.techshopmovil.model.Carrito;
import com.elkin.techshopmovil.model.Producto;
import com.elkin.techshopmovil.ui.ProductoAdapter;
import com.elkin.techshopmovil.util.Json;
import com.elkin.techshopmovil.util.Ventana;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/** Catálogo de productos con búsqueda y carrito (módulo de catálogo). */
public class CatalogoActivity extends AppCompatActivity {

    private SesionManager sesion;
    private ProductoAdapter adaptador;
    private EditText etBuscar;
    private TextView tvEstado;
    private Button btnCarrito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);
        Ventana.ajustarBordes(findViewById(R.id.raiz));

        sesion = new SesionManager(this);
        etBuscar = findViewById(R.id.etBuscar);
        tvEstado = findViewById(R.id.tvEstado);
        btnCarrito = findViewById(R.id.btnCarrito);

        TextView tvSaludo = findViewById(R.id.tvSaludo);
        tvSaludo.setText("Hola, " + sesion.getUsuario());

        findViewById(R.id.btnSalir).setOnClickListener(v -> cerrarSesion());
        findViewById(R.id.btnBuscar).setOnClickListener(v -> cargar());
        findViewById(R.id.btnPedidos).setOnClickListener(
                v -> startActivity(new Intent(this, PedidosActivity.class)));
        btnCarrito.setOnClickListener(
                v -> startActivity(new Intent(this, CarritoActivity.class)));
        etBuscar.setOnEditorActionListener((vista, accion, evento) -> {
            if (accion == EditorInfo.IME_ACTION_SEARCH) {
                cargar();
                return true;
            }
            return false;
        });

        adaptador = new ProductoAdapter(producto -> {
            if (Carrito.agregar(producto)) {
                actualizarCarrito();
                Toast.makeText(this, producto.nombre + " agregado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No hay más unidades disponibles",
                        Toast.LENGTH_SHORT).show();
            }
        });
        RecyclerView lista = findViewById(R.id.rvProductos);
        lista.setLayoutManager(new LinearLayoutManager(this));
        lista.setAdapter(adaptador);
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarCarrito();
        cargar();
    }

    private void actualizarCarrito() {
        btnCarrito.setText("Carrito (" + Carrito.totalUnidades() + ")");
    }

    private void cargar() {
        String texto = etBuscar.getText().toString().trim();
        String ruta = "/api/productos";
        if (!texto.isEmpty()) {
            ruta += "?buscar=" + Uri.encode(texto);
        }
        ApiClient.llamar("GET", ruta, null, null, respuesta -> {
            if (!respuesta.exitosa()) {
                mostrarEstado(respuesta.mensaje());
                return;
            }
            List<Producto> productos = leerProductos(respuesta.cuerpo);
            adaptador.mostrar(productos);
            if (productos.isEmpty()) {
                mostrarEstado("No hay productos que coincidan.");
            } else {
                tvEstado.setVisibility(View.GONE);
            }
        });
    }

    private void mostrarEstado(String texto) {
        tvEstado.setText(texto);
        tvEstado.setVisibility(View.VISIBLE);
    }

    private List<Producto> leerProductos(JSONObject cuerpo) {
        List<Producto> productos = new ArrayList<>();
        JSONArray arreglo = Json.buscarArreglo(cuerpo);
        if (arreglo == null) {
            return productos;
        }
        for (int i = 0; i < arreglo.length(); i++) {
            JSONObject o = arreglo.optJSONObject(i);
            if (o != null) {
                productos.add(new Producto(o.optInt("id"), o.optString("nombre"),
                        o.optString("descripcion"), o.optDouble("precio"), o.optInt("stock")));
            }
        }
        return productos;
    }

    private void cerrarSesion() {
        sesion.cerrar();
        Carrito.vaciar();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}