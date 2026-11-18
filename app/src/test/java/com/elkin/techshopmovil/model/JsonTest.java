package com.elkin.techshopmovil.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

public class JsonTest {

    @Test
    public void encuentraLaListaEnElPrimerNivel() throws Exception {
        JSONObject cuerpo = new JSONObject(
                "{\"exito\":true,\"productos\":[{\"id\":1},{\"id\":2}]}");
        JSONArray lista = Json.buscarArreglo(cuerpo);
        assertNotNull(lista);
        assertEquals(2, lista.length());
    }

    @Test
    public void encuentraLaListaEnUnObjetoAnidado() throws Exception {
        JSONObject cuerpo = new JSONObject("{\"datos\":{\"items\":[1,2,3]}}");
        JSONArray lista = Json.buscarArreglo(cuerpo);
        assertNotNull(lista);
        assertEquals(3, lista.length());
    }

    @Test
    public void devuelveNuloSiNoHayLista() throws Exception {
        JSONObject cuerpo = new JSONObject("{\"exito\":true,\"mensaje\":\"ok\"}");
        assertNull(Json.buscarArreglo(cuerpo));
    }
}