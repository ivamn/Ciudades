package com.example.ciudades.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.ciudades.adaptadores.Adaptador;
import com.example.ciudades.pojo.Ciudad;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class FragmentRecycler extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private RecyclerView recyclerView;
    private Adaptador adaptador;
    private DocumentReference userReference;
    private boolean isFABOpen;
    private FloatingActionButton fab1, fab2, fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);
        // Inicialización de la colección de ciudades del usuario en la clase estática
        userReference = FirebaseFirestore.getInstance().collection("usuarios")
                .document(Operations.user.getEmail());
        fab1 = view.findViewById(R.id.fabA);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarFragmentEdit(Operations.Accion.ADD_REQUEST, null, null);
            }
        });
        fab2 = view.findViewById(R.id.fabB);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adaptador.notifyDataSetChanged();
            }
        });
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });
        recyclerView = view.findViewById(R.id.recycler);
        inicializarAdaptador();
        return view;
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fab.animate().rotation(0);
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
    }

    private void showFABMenu() {
        isFABOpen = true;
        fab.animate().rotation(180);
        fab1.animate().translationY(-160);
        fab2.animate().translationY(-320);
    }

    private void mostrarFragmentEdit(Operations.Accion accion, Ciudad ciudad, String key) {
        FragmentManager fm = getParentFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        FragmentEdit fragment = new FragmentEdit(accion, ciudad, key);
        ft.add(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void cargarRecycler(Query query) {
        FirestoreRecyclerOptions<Ciudad> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Ciudad>()
                .setQuery(query, Ciudad.class).setLifecycleOwner(this).build();
        adaptador = new Adaptador(firestoreRecyclerOptions);
        recyclerView.setAdapter(adaptador);
        adaptador.startListening();
        adaptador.setOnClickListener(this);
        adaptador.setOnLongClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void inicializarAdaptador() {
        userReference.collection("ciudades").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

    @Override
    public void onClick(View v) {
        String key = adaptador.getSnapshots().getSnapshot(recyclerView.getChildAdapterPosition(v)).getId();
        Ciudad c = adaptador.getItem(recyclerView.getChildAdapterPosition(v));
        mostrarFragmentEdit(Operations.Accion.EDIT_REQUEST, c, key);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adaptador != null) {
            adaptador.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        adaptador.stopListening();
    }

    @Override
    public boolean onLongClick(View v) {
        String key = adaptador.getSnapshots().getSnapshot(recyclerView.getChildAdapterPosition(v)).getId();
        Operations.deleteCity(key);
        return false;
    }
}
