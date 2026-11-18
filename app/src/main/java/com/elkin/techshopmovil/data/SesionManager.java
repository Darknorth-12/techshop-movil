package com.elkin.techshopmovil.data;

import android.content.Context;
import android.content.SharedPreferences;

/** Guarda en el teléfono el token y los datos del usuario que inició sesión. */
public class SesionManager {

    private static final String ARCHIVO = "sesion_techshop";
    private static final String TOKEN = "token";
    private static final String USUARIO = "usuario";
    private static final String ROL = "rol";

    private final SharedPreferences preferencias;

    public SesionManager(Context contexto) {
        preferencias = contexto.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE);
    }

    public void guardar(String token, String usuario, String rol) {
        preferencias.edit()
                .putString(TOKEN, token)
                .putString(USUARIO, usuario)
                .putString(ROL, rol)
                .apply();
    }

    public String getToken() {
        return preferencias.getString(TOKEN, null);
    }

    public String getUsuario() {
        return preferencias.getString(USUARIO, "");
    }

    public String getRol() {
        return preferencias.getString(ROL, "");
    }

    public boolean haySesion() {
        String token = getToken();
        return token != null && !token.isEmpty();
    }

    public void cerrar() {
        preferencias.edit().clear().apply();
    }
}