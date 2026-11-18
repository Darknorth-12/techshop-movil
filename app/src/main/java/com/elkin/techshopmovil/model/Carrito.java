package com.elkin.techshopmovil.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Carrito de compras en memoria, compartido por todas las pantallas. */
public final class Carrito {

    /** Un producto con la cantidad elegida. */
    public static final class Linea {
        public final Producto producto;
        public int cantidad;

        Linea(Producto producto, int cantidad) {
            this.producto = producto;
            this.cantidad = cantidad;
        }
    }

    private static final Map<Integer, Linea> LINEAS = new LinkedHashMap<>();

    private Carrito() {
    }

    /** Agrega una unidad sin pasar del stock disponible. Devuelve false si no hay más. */
    public static boolean agregar(Producto producto) {
        Linea linea = LINEAS.get(producto.id);
        if (linea == null) {
            if (producto.stock < 1) {
                return false;
            }
            LINEAS.put(producto.id, new Linea(producto, 1));
            return true;
        }
        if (linea.cantidad >= producto.stock) {
            return false;
        }
        linea.cantidad++;
        return true;
    }

    public static void quitar(int productoId) {
        LINEAS.remove(productoId);
    }

    public static void vaciar() {
        LINEAS.clear();
    }

    public static List<Linea> lineas() {
        return new ArrayList<>(LINEAS.values());
    }

    public static int totalUnidades() {
        int total = 0;
        for (Linea linea : LINEAS.values()) {
            total += linea.cantidad;
        }
        return total;
    }

    public static double totalPrecio() {
        double total = 0;
        for (Linea linea : LINEAS.values()) {
            total += linea.producto.precio * linea.cantidad;
        }
        return total;
    }
}