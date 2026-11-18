package com.elkin.techshopmovil.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.Test;

public class RespuestaTest {

    @Test
    public void unCodigo201EsExitoso() {
        assertTrue(new ApiClient.Respuesta(201, new JSONObject(), null).exitosa());
    }

    @Test
    public void unCodigo404NoEsExitoso() {
        assertFalse(new ApiClient.Respuesta(404, new JSONObject(), null).exitosa());
    }

    @Test
    public void unErrorDeRedNuncaEsExitoso() {
        assertFalse(new ApiClient.Respuesta(200, new JSONObject(), "sin conexión").exitosa());
    }

    @Test
    public void elMensajeDeRedTienePrioridad() {
        ApiClient.Respuesta r = new ApiClient.Respuesta(0, new JSONObject(), "sin conexión");
        assertEquals("sin conexión", r.mensaje());
    }

    @Test
    public void usaElMensajeDelServicio() throws Exception {
        JSONObject cuerpo = new JSONObject("{\"mensaje\":\"Ruta no encontrada\"}");
        assertEquals("Ruta no encontrada", new ApiClient.Respuesta(404, cuerpo, null).mensaje());
    }

    @Test
    public void listaTodosLosErroresDeValidacion() throws Exception {
        JSONObject cuerpo = new JSONObject("{\"errores\":["
                + "{\"mensaje\":\"Usuario muy corto\"},{\"mensaje\":\"Falta un número\"}]}");
        assertEquals("• Usuario muy corto\n• Falta un número",
                new ApiClient.Respuesta(400, cuerpo, null).mensaje());
    }

    @Test
    public void ofreceUnMensajePorDefecto() {
        assertEquals("Ocurrió un error (código 500).",
                new ApiClient.Respuesta(500, new JSONObject(), null).mensaje());
    }
}