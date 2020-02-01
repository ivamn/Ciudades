package com.example.ciudades;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Random;

public class MainApplication extends AppCompatActivity {

    private FirebaseStorage storage;
    private DocumentReference userReference;
    private FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.application_main);
        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
        initUserReference();
        mostrarFragmentTabs();

        // Delete
        LugarViewModel lugarViewModel = new ViewModelProvider(this).get(LugarViewModel.class);
        //

        CiudadViewModel ciudadViewModel = new ViewModelProvider(this).get(CiudadViewModel.class);
        ciudadViewModel.getData().observe(this, new Observer<CiudadContainer>() {
            @Override
            public void onChanged(CiudadContainer ciudadContainer) {
                switch (ciudadContainer.getAccion()) {
                    case ADD_REQUEST:
                    case EDIT_REQUEST:
                        replaceFragmentEdit(ciudadContainer);
                        break;
                    case ADD_ACTION:
                        addCiudadFirebase(ciudadContainer.getCiudad());
                        break;
                    case EDIT_ACTION:
                        editarCiudad(ciudadContainer.getCiudad(), ciudadContainer.getKey());
                        break;
                    case DELETE:
                        borrarCiudad(ciudadContainer.getKey());
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void borrarCiudad(String key) {
        userReference.collection("ciudades").document(key).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(MainApplication.this, "Se ha eliminado la ciudad", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void replaceFragmentEdit(CiudadContainer ciudadContainer) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        FragmentEdit fragment = new FragmentEdit(ciudadContainer);
        ft.add(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void mostrarFragmentTabs() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
        FragmentTransaction ft = fm.beginTransaction();
        FragmentTabs fragment = new FragmentTabs();
        ft.replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void addCiudadFirebase(Ciudad c) {
        editarCiudad(c, getKey());
    }

    private void eliminarUltimoFragment() {
        getSupportFragmentManager().popBackStack();
    }

    private void actualizarRegistro(Ciudad c, String key, String downloadURL) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("ciudad", c.getCiudad());
        hashMap.put("pais", c.getPais());
        hashMap.put("imagen", downloadURL);
        userReference.collection("ciudades").document(key).set(hashMap);
    }

    private String getFileNameFromUri(Uri uri) {
        String result = uri.toString();
        int cut = result.lastIndexOf('/');
        if (cut != -1) {
            result = result.substring(cut + 1);
        }
        return result;
    }

    private String getKey() {
        Random r = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            builder.append(r.nextInt(96) + 32);
        }
        return builder.toString();
    }

    private void editarCiudad(final Ciudad c, final String key) {
        eliminarUltimoFragment();
        if (c.getImagen() == null || c.getImagen().equals("")) {
            actualizarRegistro(c, key, "");
        } else {
            Uri uri = Uri.parse(c.getImagen());
            String fileName = getFileNameFromUri(uri);
            final String downloadURL = "images/" + fileName;
            StorageReference refSubida = storage.getReference().child(downloadURL);
            refSubida.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainApplication.this, "Se ha subido la imagen", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainApplication.this, "No se ha podido subir la imagen", Toast.LENGTH_SHORT).show();
                    }
                    actualizarRegistro(c, key, downloadURL);
                }
            });
        }
    }

    private void initUserReference() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().exists()) {
                        db.collection("usuarios").document(user.getEmail()).set(new Usuario("asdasd", "asdasd"));
                    }
                    userReference = db.collection("usuarios").document(user.getEmail());
                } else {
                    Toast.makeText(MainApplication.this, "Error al acceder a la colección de usuarios", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public FirebaseUser getUser() {
        return user;
    }
}
