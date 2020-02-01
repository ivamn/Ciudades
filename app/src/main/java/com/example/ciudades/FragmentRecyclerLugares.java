package com.example.ciudades;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class FragmentRecyclerLugares extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private RecyclerView recyclerView;
    private LugarViewModel lugarViewModel;
    private AdaptadorLugares adaptador;
    private CollectionReference lugaresReference;

    public FragmentRecyclerLugares(CollectionReference ciudadRefence) {
        this.lugaresReference = ciudadRefence;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);
        recyclerView = view.findViewById(R.id.recycler);
        inicializarAdaptador();
        lugarViewModel = new ViewModelProvider(getActivity()).get(LugarViewModel.class);
        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarFragmentEdit();
            }
        });
        return view;
    }

    private void mostrarFragmentEdit() {
        lugarViewModel.setData(new LugarContainer(null, Util.Accion.ADD_REQUEST));
    }

    private void cargarRecycler(Query query) {
        FirestoreRecyclerOptions<Lugar> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Lugar>()
                .setQuery(query, Lugar.class).setLifecycleOwner(this).build();
        adaptador = new AdaptadorLugares(firestoreRecyclerOptions);
        recyclerView.setAdapter(adaptador);
        adaptador.startListening();
        adaptador.setOnClickListener(this);
        adaptador.setOnLongClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void inicializarAdaptador() {
        lugaresReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
        Lugar l = adaptador.getItem(recyclerView.getChildAdapterPosition(v));
        lugarViewModel.setData(new LugarContainer(l, Util.Accion.EDIT_REQUEST, key));
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
        Lugar l = adaptador.getSnapshots().get(recyclerView.getChildAdapterPosition(v));
        String key = adaptador.getSnapshots().getSnapshot(recyclerView.getChildAdapterPosition(v)).getId();
        lugarViewModel.setData(new LugarContainer(l, Util.Accion.DELETE, key));
        return false;
    }
}
