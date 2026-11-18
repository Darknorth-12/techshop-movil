package com.elkin.techshopmovil.util;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.elkin.techshopmovil.MainActivity;
import com.elkin.techshopmovil.data.SesionManager;
import com.elkin.techshopmovil.model.Carrito;

/** Navegación común entre pantallas. */
public final class Navegacion {

    private Navegacion() {
    }

    /** Cierra la sesión y vuelve al inicio cuando el token venció. */
    public static void sesionVencida(Context contexto) {
        new SesionManager(contexto).cerrar();
        Carrito.vaciar();
        Toast.makeText(contexto, "Tu sesión venció. Inicia sesión de nuevo.",
                Toast.LENGTH_LONG).show();
        Intent intent = new Intent(contexto, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        contexto.startActivity(intent);
    }
}