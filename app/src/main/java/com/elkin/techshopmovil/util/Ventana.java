package com.elkin.techshopmovil.util;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/** Evita que el contenido quede debajo de la barra de estado y la de navegación. */
public final class Ventana {

    private Ventana() {
    }

    public static void ajustarBordes(View vista) {
        final int izquierda = vista.getPaddingLeft();
        final int arriba = vista.getPaddingTop();
        final int derecha = vista.getPaddingRight();
        final int abajo = vista.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(vista, (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(izquierda + barras.left, arriba + barras.top,
                    derecha + barras.right, abajo + barras.bottom);
            return insets;
        });
    }
}