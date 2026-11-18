package com.elkin.techshopmovil.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

/** Utilidades para leer respuestas JSON. */
public final class Json {

    private Json() {
    }

    /** Busca la primera lista dentro de la respuesta, sin importar cómo se llame el campo. */
    public static JSONArray buscarArreglo(JSONObject objeto) {
        Iterator<String> claves = objeto.keys();
        while (claves.hasNext()) {
            Object valor = objeto.opt(claves.next());
            if (valor instanceof JSONArray) {
                return (JSONArray) valor;
            }
        }
        claves = objeto.keys();
        while (claves.hasNext()) {
            Object valor = objeto.opt(claves.next());
            if (valor instanceof JSONObject) {
                JSONArray interno = buscarArreglo((JSONObject) valor);
                if (interno != null) {
                    return interno;
                }
            }
        }
        return null;
    }
}