package com.example.ciudades;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class FragmentEdit extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private final int COD_ELEGIR_IMAGEN = 0;
    private String key;
    private EditText editCiudad, pais;
    private ImageView imageView;
    private CiudadViewModel ciudadViewModel;
    private Util.Accion accion;
    private Ciudad ciudad;
    private LugarViewModel lugarViewModel;
    private Uri selectedImage;
    private RecyclerView recycler;
    private AdaptadorLugares adaptador;
    private AlertDialog dialog;

    public FragmentEdit(CiudadContainer ciudadContainer) {
        accion = ciudadContainer.getAccion();
        ciudad = ciudadContainer.getCiudad();
        key = ciudadContainer.getKey();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        showProgressBar();
        View v = inflater.inflate(R.layout.edit_fragment, container, false);
        editCiudad = v.findViewById(R.id.editCiudad);
        pais = v.findViewById(R.id.editPais);
        imageView = v.findViewById(R.id.imageView);
        recycler = v.findViewById(R.id.recycler_lugares);

        if (accion == Util.Accion.EDIT_REQUEST) {
            // Progress bar
            FirebaseStorage.getInstance().getReference(key).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    selectedImage = task.getResult();
                    dialog.cancel();
                }
            });
            editCiudad.setText(ciudad.getCiudad());
            pais.setText(ciudad.getPais());
            if (!ciudad.getImagen().equals("") && ciudad.getImagen() != null) {
                Operations.loadIntoImageView(FirebaseStorage.getInstance().getReference(ciudad.getImagen()), imageView);
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
        } else {
            key = Operations.newId();
        }
        Operations.cityDocument = Operations.cityCollection.document(key);

        inicializarAdaptador();



        lugarViewModel = new ViewModelProvider(getActivity()).get(LugarViewModel.class);
        ciudadViewModel = new ViewModelProvider(getActivity()).get(CiudadViewModel.class);
        v.findViewById(R.id.buttonAceptar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Ciudad c = generarCiudad();
                if (accion == Util.Accion.ADD_REQUEST) {
                    Operations.addCity(c, selectedImage);
                    getParentFragmentManager().popBackStack();
                } else {
                    Operations.updateCity(c, key, selectedImage);
                    getParentFragmentManager().popBackStack();
                }
            }
        });
        v.findViewById(R.id.fabLugares).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lugarViewModel.setData(new LugarContainer(null, Util.Accion.ADD_REQUEST, null));
            }
        });
        /*
        lugarViewModel.getData().observe(getViewLifecycleOwner(), new Observer<LugarContainer>() {
            @Override
            public void onChanged(LugarContainer lugarContainer) {
                if (lugarContainer != null) {
                    switch (lugarContainer.getAccion()) {
                        case ADD_REQUEST:
                        case EDIT_REQUEST:
                            //replaceFragmentEditLugar(lugarContainer);
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

         */

        return v;
    }

    private void showProgressBar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(getLayoutInflater().inflate(R.layout.progress, null));
        builder.setCancelable(false);
        dialog = builder.create();

    }

    private void inicializarAdaptador() {
        Operations.placeCollection = Operations.cityDocument.collection("lugares");
        Operations.placeCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Query query = task.getResult().getQuery();
                    cargarRecycler(query);
                } else {
                    Toast.makeText(getContext(), "Error al obtener datos de la base de datos", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void borrarLugar(String key) {
        /*
        ciudadReference.collection("lugares").document(key).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "Se ha eliminado la ciudad", Toast.LENGTH_SHORT).show();
            }
        });

         */
    }

    private void addLugarFirebase(Lugar lugar) {
        editarLugar(lugar, "");
    }

    private void editarLugar(final Lugar l, final String key) {
        eliminarUltimoFragment();
        if (l.getImagen() == null || l.getImagen().equals("")) {
            actualizarRegistro(l, key, "");
        } else {
            Uri uri = Uri.parse(l.getImagen());
            //String fileName = getFileNameFromUri(uri);
            final String downloadURL = "lugares/" + "";
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
        /*
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("lugar", c.getLugar());
        hashMap.put("descripcion", c.getDescripcion());
        hashMap.put("imagen", downloadURL);
        ciudadReference.collection("lugares").document(key).set(hashMap);

         */
    }

    private void eliminarUltimoFragment() {
        getChildFragmentManager().popBackStack();
    }

    private void cargarRecycler(Query query) {
        FirestoreRecyclerOptions<Lugar> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Lugar>()
                .setQuery(query, Lugar.class).setLifecycleOwner(this).build();
        adaptador = new AdaptadorLugares(firestoreRecyclerOptions);
        recycler.setAdapter(adaptador);
        adaptador.startListening();
        adaptador.setOnClickListener(this);
        adaptador.setOnLongClickListener(this);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private Ciudad generarCiudad() {
        return new Ciudad(editCiudad.getText().toString(), pais.getText().toString(), key);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COD_ELEGIR_IMAGEN && resultCode == RESULT_OK) {
            selectedImage = data.getData();
            imageView.setImageURI(selectedImage);
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getActivity(), "Se ha cancelado la operación", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adaptador.stopListening();
    }


    @Override
    public void onClick(View v) {
        Lugar l = adaptador.getItem(recycler.getChildAdapterPosition(v));
        String key = adaptador.getSnapshots().getSnapshot(recycler.getChildAdapterPosition(v)).getId();
        lugarViewModel.setData(new LugarContainer(l, Util.Accion.EDIT_REQUEST, key));
    }

    @Override
    public boolean onLongClick(View v) {
        String key = adaptador.getSnapshots().getSnapshot(recycler.getChildAdapterPosition(v)).getId();
        Operations.deletePlace(key);
        return false;
    }
}
