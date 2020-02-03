package com.example.ciudades;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Random;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class FragmentEdit extends Fragment {

    private final int COD_ELEGIR_IMAGEN = 0;
    private String key;
    private EditText editCiudad, pais;
    private ImageView imageView;
    private String imagen;
    private CiudadViewModel ciudadViewModel;
    private Util.Accion accion;
    private Ciudad ciudad;
    private DocumentReference ciudadReference;
    private LugarViewModel lugarViewModel;

    public FragmentEdit(CiudadContainer ciudadContainer) {
        accion = ciudadContainer.getAccion();
        ciudad = ciudadContainer.getCiudad();
        key = ciudadContainer.getKey();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.edit_fragment, container, false);
        String user = ((MainApplication) getActivity()).getUser().getEmail();
        editCiudad = v.findViewById(R.id.editCiudad);
        pais = v.findViewById(R.id.editPais);
        imageView = v.findViewById(R.id.imageView);

        if (accion == Util.Accion.EDIT_REQUEST) {
            editCiudad.setText(ciudad.getCiudad());
            pais.setText(ciudad.getPais());
            imagen = ciudad.getImagen();
            if (!ciudad.getImagen().equals("") && ciudad.getImagen() != null) {
                FirebaseStorage.getInstance().getReference(ciudad.getImagen()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Picasso.get().load(task.getResult()).into(imageView);
                    }
                });
            }
            ciudadReference = FirebaseFirestore.getInstance().collection("usuarios").document(user)
                    .collection("ciudades").document(key);
            mostrarFragmentRecycler();
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, COD_ELEGIR_IMAGEN);
                }
            }
        });

        ciudadViewModel = new ViewModelProvider(getActivity()).get(CiudadViewModel.class);
        v.findViewById(R.id.buttonAceptar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ciudad c = generarCiudad();
                if (accion == Util.Accion.ADD_REQUEST) {
                    ciudadViewModel.setData(new CiudadContainer(c, Util.Accion.ADD_ACTION));
                } else {
                    ciudadViewModel.setData(new CiudadContainer(c, Util.Accion.EDIT_ACTION, key));
                }
            }
        });

        lugarViewModel = new ViewModelProvider(getActivity()).get(LugarViewModel.class);
        lugarViewModel.getData().observe(getViewLifecycleOwner(), new Observer<LugarContainer>() {
            @Override
            public void onChanged(LugarContainer lugarContainer) {
                if (lugarContainer != null) {
                    switch (lugarContainer.getAccion()) {
                        case ADD_REQUEST:
                        case EDIT_REQUEST:
                            replaceFragmentEditLugar(lugarContainer);
                            break;
                        case ADD_ACTION:
                            addLugarFirebase(lugarContainer.getLugar());
                            break;
                        case EDIT_ACTION:
                            editarLugar(lugarContainer.getLugar(), lugarContainer.getKey());
                            break;
                        case DELETE:
                            borrarLugar(lugarContainer.getKey());
                            break;
                    }
                }
            }
        });

        return v;
    }

    private void borrarLugar(String key) {
        ciudadReference.collection("lugares").document(key).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "Se ha eliminado la ciudad", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addLugarFirebase(Lugar lugar) {
        editarLugar(lugar, getKey());
    }

    private void editarLugar(final Lugar l, final String key) {
        eliminarUltimoFragment();
        if (l.getImagen() == null || l.getImagen().equals("")) {
            actualizarRegistro(l, key, "");
        } else {
            Uri uri = Uri.parse(l.getImagen());
            String fileName = getFileNameFromUri(uri);
            final String downloadURL = "lugares/" + fileName;
            StorageReference refSubida = FirebaseStorage.getInstance().getReference().child(downloadURL);
            refSubida.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Se ha subido la imagen", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getContext(), "Ya existe la imagen", Toast.LENGTH_SHORT).show();
                    }
                    actualizarRegistro(l, key, downloadURL);
                }
            });
        }
    }

    private void actualizarRegistro(Lugar c, String key, String downloadURL) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("lugar", c.getLugar());
        hashMap.put("descripcion", c.getDescripcion());
        hashMap.put("imagen", downloadURL);
        ciudadReference.collection("lugares").document(key).set(hashMap);
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

    private void eliminarUltimoFragment() {
        getChildFragmentManager().popBackStack();
    }

    private void replaceFragmentEditLugar(LugarContainer lugarContainer) {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        FragmentEditLugar fragment = new FragmentEditLugar(lugarContainer);
        ft.add(R.id.fragment_container_lugares, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void mostrarFragmentRecycler() {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        FragmentRecyclerLugares fragmentRecyclerLugares = new FragmentRecyclerLugares(ciudadReference.collection("lugares"));
        ft.replace(R.id.fragment_container_lugares, fragmentRecyclerLugares);
        ft.commit();
    }

    private Ciudad generarCiudad() {
        return new Ciudad(editCiudad.getText().toString(), pais.getText().toString(), imagen);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COD_ELEGIR_IMAGEN && resultCode == RESULT_OK) {
            Uri rutaImagen = data.getData();
            imagen = rutaImagen.toString();
            imageView.setImageURI(rutaImagen);
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getActivity(), "Se ha cancelado la operación", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        lugarViewModel.setData(null);
    }


}
