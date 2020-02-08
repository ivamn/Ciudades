package com.example.ciudades;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class FragmentRecycler extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private RecyclerView recyclerView;
    private CiudadViewModel ciudadViewModel;
    private Adaptador adaptador;
    private DocumentReference userReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);
        userReference = FirebaseFirestore.getInstance().collection("usuarios")
                .document(Operations.user.getEmail());
        recyclerView = view.findViewById(R.id.recycler);
        inicializarAdaptador();
        ciudadViewModel = new ViewModelProvider(getActivity()).get(CiudadViewModel.class);
        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarFragmentEdit();
            }
        });
        return view;
    }

    private void mostrarFragmentEdit() {
        ciudadViewModel.setData(new CiudadContainer(null, Util.Accion.ADD_REQUEST));
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
        ciudadViewModel.setData(new CiudadContainer(c, Util.Accion.EDIT_REQUEST, key));
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
