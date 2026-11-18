package com.elkin.techshopmovil.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class CarritoTest {

    private final Producto mouse = new Producto(1, "Mouse", "Optico", 49.99, 2);
    private final Producto teclado = new Producto(2, "Teclado", "Luces", 129.50, 5);
    private final Producto agotado = new Producto(3, "Monitor", "HD", 899.99, 0);

    @Before
    public void limpiar() {
        Carrito.vaciar();
    }

    @Test
    public void agregarUnProductoLoDejaConUnaUnidad() {
        assertTrue(Carrito.agregar(mouse));
        assertEquals(1, Carrito.totalUnidades());
    }

    @Test
    public void noPermiteSuperarElStockDisponible() {
        assertTrue(Carrito.agregar(mouse));
        assertTrue(Carrito.agregar(mouse));
        assertFalse(Carrito.agregar(mouse));
        assertEquals(2, Carrito.totalUnidades());
    }

    @Test
    public void noAgregaProductosAgotados() {
        assertFalse(Carrito.agregar(agotado));
        assertEquals(0, Carrito.totalUnidades());
    }

    @Test
    public void calculaElTotalDeLaCompra() {
        Carrito.agregar(mouse);
        Carrito.agregar(mouse);
        Carrito.agregar(teclado);
        assertEquals(229.48, Carrito.totalPrecio(), 0.001);
        assertEquals(3, Carrito.totalUnidades());
    }

    @Test
    public void quitarEliminaLaLineaCompleta() {
        Carrito.agregar(mouse);
        Carrito.agregar(teclado);
        Carrito.quitar(mouse.id);
        assertEquals(1, Carrito.lineas().size());
        assertEquals(129.50, Carrito.totalPrecio(), 0.001);
    }

    @Test
    public void vaciarDejaElCarritoEnCero() {
        Carrito.agregar(mouse);
        Carrito.vaciar();
        assertEquals(0, Carrito.totalUnidades());
        assertEquals(0.0, Carrito.totalPrecio(), 0.001);
    }
}