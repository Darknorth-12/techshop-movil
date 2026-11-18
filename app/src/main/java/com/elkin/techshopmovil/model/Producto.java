package com.elkin.techshopmovil.model;

/** Producto del catálogo. */
public class Producto {

    public final int id;
    public final String nombre;
    public final String descripcion;
    public final double precio;
    public final int stock;

    public Producto(int id, String nombre, String descripcion, double precio, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
    }
}