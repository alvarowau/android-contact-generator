package org.alvarowau.apicontact.api;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ContactApi extends Thread {
    private final ContentResolver contentResolver;
    private final Context context;
    private int numeroContactos;

    public ContactApi(Context context, ContentResolver contentResolver, int numeroContactos) {
        this.context = context;
        this.contentResolver = contentResolver;
        this.numeroContactos = numeroContactos;
    }

    public void setNumeroContactos(int numeroContactos){
        this.numeroContactos = numeroContactos;
    }

    @Override
    public void run() {
        obtenerContactosExactos();
    }

    private void obtenerContactosExactos() {
        AtomicInteger contactosGenerados = new AtomicInteger();

        while (contactosGenerados.get() < numeroContactos) {
            int contactosRestantes = numeroContactos - contactosGenerados.get();
            String url = "https://randomuser.me/api/?results=" + contactosRestantes;

            String jsonResponse = obtenerDatosDeApi(url);

            if (jsonResponse != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    JSONArray results = jsonObject.getJSONArray("results");

                    int hilosDisponibles = Runtime.getRuntime().availableProcessors();
                    ExecutorService executor = Executors.newFixedThreadPool(hilosDisponibles);

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject contactoJson = results.getJSONObject(i);

                        executor.execute(() -> {
                            try {
                                if (procesarContacto(contactoJson)) {
                                    synchronized (this) {
                                        contactosGenerados.getAndIncrement();
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("ApiContactos", "Error procesando contacto: " + e.getMessage());
                            }
                        });

                        if (contactosGenerados.get() >= numeroContactos) {
                            break;
                        }
                    }

                    executor.shutdown();
                    while (!executor.isTerminated()) {
                        // Esperamos a que todos los hilos terminen
                    }

                } catch (Exception e) {
                    Log.e("ApiContactos", "Error procesando los datos de la API: " + e.getMessage());
                }
            }
        }
    }

    private boolean procesarContacto(JSONObject contactoJson) {
        try {
            String nombre = contactoJson.getJSONObject("name").getString("first") + " " +
                    contactoJson.getJSONObject("name").getString("last");
            if (!esNombreValido(nombre)) return false;

            String telefono = contactoJson.has("phone") ? contactoJson.getString("phone") :
                    contactoJson.has("cell") ? contactoJson.getString("cell") : "Desconocido";

            String email = contactoJson.getString("email");
            if (!esEmailValido(email)) return false;

            String fotoUrl = contactoJson.getJSONObject("picture").getString("medium");
            String fechaNacimiento = contactoJson.getJSONObject("dob").getString("date");

            crearContactoEnDispositivo(nombre, telefono, email, fotoUrl, fechaNacimiento);
            return true;
        } catch (Exception e) {
            Log.e("ApiContactos", "Error al procesar contacto individual: " + e.getMessage());
            return false;
        }
    }

    private boolean esNombreValido(String nombre) {
        return nombre.matches("[\\p{L} ]+");
    }

    private boolean esEmailValido(String email) {
        return email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    }

    private String obtenerDatosDeApi(String urlString) {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            int data;
            while ((data = inputStream.read()) != -1) {
                result.append((char) data);
            }
            return result.toString();
        } catch (Exception e) {
            Log.e("ApiContactos", "Error al obtener los datos de la API: " + e.getMessage());
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ApiContactos", "Error al cerrar las conexiones: " + e.getMessage());
            }
        }
    }

    private void crearContactoEnDispositivo(String nombre, String telefono, String email, String fotoUrl, String fechaNacimiento) {
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(() -> {
                Uri uri = ContactsContract.RawContacts.CONTENT_URI;
                ContentValues values = new ContentValues();
                Uri contactoUri = contentResolver.insert(uri, values);

                if (contactoUri != null) {
                    long contactoId = ContentUris.parseId(contactoUri);

                    values.clear();
                    values.put(ContactsContract.Data.RAW_CONTACT_ID, contactoId);
                    values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
                    values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, nombre);
                    contentResolver.insert(ContactsContract.Data.CONTENT_URI, values);

                    values.clear();
                    values.put(ContactsContract.Data.RAW_CONTACT_ID, contactoId);
                    values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                    values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, telefono);
                    values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
                    contentResolver.insert(ContactsContract.Data.CONTENT_URI, values);

                    values.clear();
                    values.put(ContactsContract.Data.RAW_CONTACT_ID, contactoId);
                    values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
                    values.put(ContactsContract.CommonDataKinds.Email.ADDRESS, email);
                    values.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_HOME);
                    contentResolver.insert(ContactsContract.Data.CONTENT_URI, values);

                    values.clear();
                    values.put(ContactsContract.Data.RAW_CONTACT_ID, contactoId);
                    values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE);
                    values.put(ContactsContract.CommonDataKinds.Event.START_DATE, fechaNacimiento);
                    values.put(ContactsContract.CommonDataKinds.Event.TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY);
                    contentResolver.insert(ContactsContract.Data.CONTENT_URI, values);

                    Picasso.get()
                            .load(fotoUrl)
                            .into(new com.squareup.picasso.Target() {
                                @Override
                                public void onBitmapLoaded(android.graphics.Bitmap bitmap, Picasso.LoadedFrom from) {
                                    guardarFotoDeContacto(contactoId, bitmap);
                                }

                                @Override
                                public void onBitmapFailed(Exception e, android.graphics.drawable.Drawable errorDrawable) {
                                    Log.e("ApiContactos", "Error al cargar la imagen de perfil: " + e.getMessage());
                                }

                                @Override
                                public void onPrepareLoad(android.graphics.drawable.Drawable placeHolderDrawable) {
                                }
                            });
                } else {
                    Log.e("ApiContactos", "Error al crear el contacto.");
                }
            });
        } else {
            Log.e("ApiContactos", "El contexto no es una instancia de Activity.");
        }
    }

    private void guardarFotoDeContacto(long contactoId, android.graphics.Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        ContentValues values = new ContentValues();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, contactoId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Photo.PHOTO, byteArray);
        contentResolver.insert(ContactsContract.Data.CONTENT_URI, values);
    }
}
