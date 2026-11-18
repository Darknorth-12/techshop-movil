package com.elkin.techshopmovil.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elkin.techshopmovil.R;
import com.elkin.techshopmovil.model.Producto;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Muestra la lista de productos en un RecyclerView. */
public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.Holder> {

    /** Se llama al pulsar el botón Agregar de un producto. */
    public interface AlAgregar {
        void agregar(Producto producto);
    }

    private final List<Producto> productos = new ArrayList<>();
    private final AlAgregar alAgregar;

    public ProductoAdapter(AlAgregar alAgregar) {
        this.alAgregar = alAgregar;
    }

    public void mostrar(List<Producto> nuevos) {
        productos.clear();
        productos.addAll(nuevos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup padre, int tipo) {
        View vista = LayoutInflater.from(padre.getContext())
                .inflate(R.layout.item_producto, padre, false);
        return new Holder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int posicion) {
        Producto producto = productos.get(posicion);
        holder.nombre.setText(producto.nombre);
        holder.descripcion.setText(producto.descripcion);
        holder.precio.setText(String.format(Locale.US, "$ %.2f", producto.precio));
        holder.stock.setText(producto.stock > 0
                ? "Disponibles: " + producto.stock : "Agotado");
        holder.agregar.setEnabled(producto.stock > 0);
        holder.agregar.setOnClickListener(v -> alAgregar.agregar(producto));
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final TextView nombre;
        final TextView descripcion;
        final TextView precio;
        final TextView stock;
        final Button agregar;

        Holder(@NonNull View vista) {
            super(vista);
            nombre = vista.findViewById(R.id.tvNombre);
            descripcion = vista.findViewById(R.id.tvDescripcion);
            precio = vista.findViewById(R.id.tvPrecio);
            stock = vista.findViewById(R.id.tvStock);
            agregar = vista.findViewById(R.id.btnAgregar);
        }
    }
}