package org.alvarowau.apicontact;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import org.alvarowau.apicontact.api.ContactApi;
import org.alvarowau.apicontact.databinding.ActivityMainBinding;
import org.alvarowau.apicontact.utils.PermissionUtils;

import java.util.Map;

/**
 * Actividad principal de la aplicación donde se manejan la creación de contactos
 * y los permisos necesarios para interactuar con la agenda.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ActivityResultLauncher<String[]> permisosLauncher;
    private ContactApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        initComponents();
    }

    /**
     * Inicializa los componentes de la actividad, incluyendo la configuración
     * de permisos y la interacción con la API para manejar los contactos.
     */
    private void initComponents() {
        permisosLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                this::onPermisosResult
        );
        initPermissions();
        api = new ContactApi(MainActivity.this, getContentResolver());

        // Configurar el botón de generar contactos
        binding.buttonGenerate.setOnClickListener(view -> {
            String numeroTxt = binding.editText.getText().toString();
            if (numeroTxt.isEmpty()) {
                Toast.makeText(MainActivity.this, "Por favor, ingresa un número", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    int numero = Integer.parseInt(numeroTxt);
                    binding.progressBar.setVisibility(View.VISIBLE); // Mostrar ProgressBar

                    // Llamar a la función que genera los contactos
                    generarContacts(numero);
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Por favor, ingresa un número válido", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Genera contactos en un hilo separado para evitar bloquear el hilo principal.
     *
     * @param numero El número de contactos que se generarán.
     */
    private void generarContacts(int numero) {
        // Crear un nuevo hilo para evitar bloquear el hilo principal
        new Thread(() -> {
            // Generar los contactos
            api.obtenerContactosSiNoHay(numero);

            // Después de generar los contactos, volver al hilo principal
            runOnUiThread(() -> {
                binding.progressBar.setVisibility(View.GONE); // Ocultar ProgressBar
                binding.textView.setText("Se han creado " + numero + " contactos");
                Toast.makeText(MainActivity.this, "Contactos generados correctamente", Toast.LENGTH_SHORT).show();
            });
        }).start(); // Iniciar el hilo
    }

    /**
     * Solicita los permisos necesarios para acceder y modificar los contactos.
     */
    private void initPermissions() {
        permisosLauncher.launch(new String[]{
                android.Manifest.permission.READ_CONTACTS,
                android.Manifest.permission.WRITE_CONTACTS,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
        });
    }

    /**
     * Maneja el resultado de la solicitud de permisos.
     * Muestra un mensaje indicando si todos los permisos fueron concedidos o no.
     *
     * @param resultados Un mapa con el resultado de cada permiso solicitado.
     */
    private void onPermisosResult(Map<String, Boolean> resultados) {
        boolean todosConcedidos = true;
        for (Boolean concedido : resultados.values()) {
            if (!concedido) {
                todosConcedidos = false;
                break;
            }
        }
        if (!todosConcedidos) {
            Toast.makeText(this, "Permisos necesarios no concedidos. Por favor, habilítalos desde la configuración.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show();
        }
    }
}
