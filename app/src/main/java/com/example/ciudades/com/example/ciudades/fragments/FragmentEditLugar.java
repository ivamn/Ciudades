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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ciudades.Operations;
import com.example.ciudades.R;
import com.example.ciudades.com.example.ciudades.adaptadores.AdaptadorLugaresDestacados;
import com.example.ciudades.com.example.ciudades.pojo.Ciudad;
import com.example.ciudades.com.example.ciudades.pojo.Lugar;
import com.example.ciudades.com.example.ciudades.pojo.LugarDestacado;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class FragmentEditLugar extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private static final int COD_ELEGIR_IMAGEN = 0;
    private static final int COD_ELEGIR_IMAGEN_LUGAR = 1;
    private EditText editLugar, editDescripcion;
    private ImageView imageView;
    private Operations.Accion accion;
    private Lugar lugar;
    private String key;
    private Uri selectedImage;
    private AlertDialog dialog;
    private RecyclerView recycler;
    private AdaptadorLugaresDestacados adaptador;

    public FragmentEditLugar(Operations.Accion accion, Lugar lugar, String key) {
        this.accion = accion;
        this.lugar = lugar;
        this.key = key;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.edit_lugar_fragment, container, false);
        showProgressBar();
        recycler = v.findViewById(R.id.recycler_lugares_fotos);
        editLugar = v.findViewById(R.id.editLugar);
        editDescripcion = v.findViewById(R.id.editDescripcion);
        imageView = v.findViewById(R.id.imageViewLugar);
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
            editLugar.setText(lugar.getLugar());
            editDescripcion.setText(lugar.getDescripcion());
            if (!lugar.getImagen().equals("") && lugar.getImagen() != null) {
                Operations.loadIntoImageView(FirebaseStorage.getInstance().getReference(lugar.getImagen()), imageView);
            }
        } else {
            key = Operations.newId();
            Operations.loadIntoImageView(Operations.DEFAULT_IMAGE, imageView);
        }
        Operations.placeDocument = Operations.placeCollection.document(key);
        inicializarAdaptador();

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

        v.findViewById(R.id.fabLugaresDestacados).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, new LugarDestacadoFragment(null, null, Operations.Accion.ADD_REQUEST))
                        .addToBackStack(null).commit();
            }
        });

        v.findViewById(R.id.buttonAceptarLugar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Lugar l = generarLugar();
                if (accion == Operations.Accion.ADD_REQUEST) {
                    Operations.addPlace(l, selectedImage);
                } else {
                    Operations.updatePlace(l, key, selectedImage);
                }
                getParentFragmentManager().popBackStack();
            }
        });

        return v;
    }

    private void inicializarAdaptador() {
        Operations.mainPlaceCollection = Operations.placeDocument.collection("fotos");
        Operations.mainPlaceCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
        FirestoreRecyclerOptions<LugarDestacado> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<LugarDestacado>()
                .setQuery(query, LugarDestacado.class).setLifecycleOwner(this).build();
        adaptador = new AdaptadorLugaresDestacados(firestoreRecyclerOptions);
        adaptador.setOnClickListener(this);
        adaptador.setOnLongClickListener(this);
        recycler.setAdapter(adaptador);
        adaptador.startListening();
        recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
    }

    private void showProgressBar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(getLayoutInflater().inflate(R.layout.progress, null));
        builder.setCancelable(false);
        dialog = builder.create();

    }

    private Lugar generarLugar() {
        return new Lugar(editLugar.getEditableText().toString(), editDescripcion.getText().toString(), key);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COD_ELEGIR_IMAGEN && resultCode == RESULT_OK) {
            selectedImage = data.getData();
            imageView.setImageURI(selectedImage);
        } else if (requestCode == COD_ELEGIR_IMAGEN_LUGAR && resultCode == RESULT_OK) {
            Uri u = data.getData();
            Operations.addMainPlace(new LugarDestacado(), u);
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
        LugarDestacado lugar = adaptador.getSnapshots().get(recycler.getChildAdapterPosition(v));
        String key = adaptador.getSnapshots().getSnapshot(recycler.getChildAdapterPosition(v)).getId();
        getParentFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new LugarDestacadoFragment(lugar, key, Operations.Accion.EDIT_REQUEST))
                .addToBackStack(null).commit();
    }

    @Override
    public boolean onLongClick(View v) {
        String key = adaptador.getSnapshots().getSnapshot(recycler.getChildAdapterPosition(v)).getId();
        Operations.deleteMainPlace(key);
        return false;
    }
}
