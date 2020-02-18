package com.example.ciudades.com.example.ciudades.fragments;

import android.app.AlertDialog;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ciudades.Operations;
import com.example.ciudades.R;
import com.example.ciudades.com.example.ciudades.adaptadores.AdaptadorLugares;
import com.example.ciudades.com.example.ciudades.pojo.Ciudad;
import com.example.ciudades.com.example.ciudades.pojo.Lugar;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class FragmentEdit extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private final int COD_ELEGIR_IMAGEN = 0;
    private String key;
    private EditText editCiudad, pais;
    private ImageView imageView;
    private Operations.Accion accion;
    private Ciudad ciudad;
    private Uri selectedImage;
    private RecyclerView recycler;
    private AdaptadorLugares adaptador;
    private AlertDialog dialog;

    public FragmentEdit(Operations.Accion accion, Ciudad ciudad, String key) {
        this.accion = accion;
        this.ciudad = ciudad;
        this.key = key;
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

        if (accion == Operations.Accion.EDIT_REQUEST) {
            FirebaseStorage.getInstance().getReference(key).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        selectedImage = task.getResult();
                    }
                    dialog.cancel();
                }
            });
            editCiudad.setText(ciudad.getCiudad());
            pais.setText(ciudad.getPais());
            if (!ciudad.getImagen().equals("") && ciudad.getImagen() != null) {
                Operations.loadIntoImageView(FirebaseStorage.getInstance().getReference(ciudad.getImagen()), imageView);
            }
        } else {
            key = Operations.newId();
            Operations.loadIntoImageView(Operations.DEFAULT_IMAGE, imageView);
        }
        Operations.cityDocument = Operations.cityCollection.document(key);

        inicializarAdaptador();


        v.findViewById(R.id.buttonAceptar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Ciudad c = generarCiudad();
                if (accion == Operations.Accion.ADD_REQUEST) {
                    Operations.addCity(c, selectedImage);
                } else {
                    Operations.updateCity(c, key, selectedImage);
                }
                getParentFragmentManager().popBackStack();
            }
        });
        v.findViewById(R.id.fabLugares).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarFragmentEditLugar(null, Operations.Accion.ADD_REQUEST, null);
            }
        });

        return v;
    }

    private void mostrarFragmentEditLugar(Lugar lugar, Operations.Accion accion, String key) {
        FragmentManager fm = getParentFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        FragmentEditLugar fragment = new FragmentEditLugar(accion, lugar, key);
        ft.add(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void showProgressBar() {
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
        mostrarFragmentEditLugar(l, Operations.Accion.EDIT_REQUEST, key);
    }

    @Override
    public boolean onLongClick(View v) {
        String key = adaptador.getSnapshots().getSnapshot(recycler.getChildAdapterPosition(v)).getId();
        Operations.deletePlace(key);
        return false;
    }
}
