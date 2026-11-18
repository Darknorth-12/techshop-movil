package com.elkin.techshopmovil.data;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Acceso a la API REST de TechShop.
 * Ejecuta cada llamada en un hilo secundario y entrega el resultado en el hilo principal.
 */
public final class ApiClient {

    /** Con "adb reverse tcp:3000 tcp:3000" el celular alcanza el servicio del computador. */
    public static final String BASE_URL = "http://localhost:3000";

    private static final MediaType MEDIA_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient CLIENTE = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build();
    private static final ExecutorService EJECUTOR = Executors.newFixedThreadPool(2);
    private static final Handler HILO_PRINCIPAL = new Handler(Looper.getMainLooper());

    private ApiClient() {
    }

    /** Recibe el resultado de una llamada. */
    public interface Callback {
        void alTerminar(Respuesta respuesta);
    }

    /** Resultado de una llamada a la API. */
    public static final class Respuesta {
        public final int codigo;
        public final JSONObject cuerpo;
        public final String errorDeRed;

        public Respuesta(int codigo, JSONObject cuerpo, String errorDeRed) {
            this.codigo = codigo;
            this.cuerpo = cuerpo;
            this.errorDeRed = errorDeRed;
        }

        public boolean exitosa() {
            return errorDeRed == null && codigo >= 200 && codigo < 300;
        }

        /** Mensaje listo para mostrar al usuario. */
        public String mensaje() {
            if (errorDeRed != null) {
                return errorDeRed;
            }
            JSONArray errores = cuerpo.optJSONArray("errores");
            if (errores != null && errores.length() > 0) {
                StringBuilder texto = new StringBuilder();
                for (int i = 0; i < errores.length(); i++) {
                    if (i > 0) {
                        texto.append("\n");
                    }
                    JSONObject objeto = errores.optJSONObject(i);
                    String linea = objeto != null
                            ? objeto.optString("mensaje", objeto.toString())
                            : errores.optString(i);
                    texto.append("• ").append(linea);
                }
                return texto.toString();
            }
            return cuerpo.optString("mensaje", "Ocurrió un error (código " + codigo + ").");
        }
    }

    /** Llama a la API sin bloquear la pantalla. El token es opcional. */
    public static void llamar(String metodo, String ruta, JSONObject cuerpo, String token,
                              Callback callback) {
        EJECUTOR.execute(() -> {
            Respuesta respuesta = ejecutar(metodo, ruta, cuerpo, token);
            HILO_PRINCIPAL.post(() -> callback.alTerminar(respuesta));
        });
    }

    private static Respuesta ejecutar(String metodo, String ruta, JSONObject cuerpo,
                                      String token) {
        try {
            String texto = cuerpo != null ? cuerpo.toString() : "{}";
            Request.Builder pedido = new Request.Builder().url(BASE_URL + ruta);
            if (token != null && !token.isEmpty()) {
                pedido.header("Authorization", "Bearer " + token);
            }
            switch (metodo) {
                case "POST":
                    pedido.post(RequestBody.create(texto, MEDIA_JSON));
                    break;
                case "PUT":
                    pedido.put(RequestBody.create(texto, MEDIA_JSON));
                    break;
                case "PATCH":
                    pedido.patch(RequestBody.create(texto, MEDIA_JSON));
                    break;
                case "DELETE":
                    pedido.delete();
                    break;
                default:
                    pedido.get();
                    break;
            }
            try (Response respuesta = CLIENTE.newCall(pedido.build()).execute()) {
                String contenido = respuesta.body() != null ? respuesta.body().string() : "";
                JSONObject json = contenido.isEmpty() ? new JSONObject() : new JSONObject(contenido);
                return new Respuesta(respuesta.code(), json, null);
            }
        } catch (IOException e) {
            return new Respuesta(0, new JSONObject(),
                    "No se pudo conectar con el servicio. Revisa que esté iniciado "
                            + "y que el cable USB esté conectado.");
        } catch (JSONException e) {
            return new Respuesta(0, new JSONObject(),
                    "La respuesta del servicio no tiene el formato esperado.");
        }
    }
}