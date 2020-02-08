package com.example.ciudades;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class FragmentPerfil extends Fragment {

    private TextView textViewNombre;
    private TextView textViewApellido;
    private ImageView imagePerfil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.perfil_fragment, container, false);
        textViewApellido = v.findViewById(R.id.textViewApellidos);
        textViewNombre = v.findViewById(R.id.textViewNombre);
        imagePerfil = v.findViewById(R.id.imagePerfil);
        FirebaseFirestore.getInstance().collection("usuarios").document(Operations.user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot s = task.getResult();
                    textViewApellido.setText(s.getString("apellido"));
                    textViewNombre.setText(s.getString("nombre"));
                    String i = s.getString("imagen");
                    if (i != null && !i.equals("")){
                        cargarImagenUsuario(i);
                    }
                } else {
                    Toast.makeText(getContext(),"No se han podido obtener los datos del usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }

    private void cargarImagenUsuario(String ref){
        FirebaseStorage.getInstance().getReference(ref).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Picasso.get().load(task.getResult()).into(imagePerfil);
                } else {
                    Toast.makeText(getContext(), "No se ha podido obtener la imagen del usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
