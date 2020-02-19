package com.example.ciudades.fragments;

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

import com.example.ciudades.Operations;
import com.example.ciudades.R;
import com.example.ciudades.pojo.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class FragmentPerfil extends Fragment {

    private final int COD_ELEGIR_IMAGEN = 0;
    private EditText editNombre, editApellidos, editTelefono;
    private TextView textViewMail;
    private ImageView imagePerfil;
    private FloatingActionButton fab;
    private Uri selectedImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.perfil_fragment, container, false);
        editApellidos = v.findViewById(R.id.editApellidos);
        editNombre = v.findViewById(R.id.editNombre);
        editTelefono = v.findViewById(R.id.editTelefono);
        textViewMail = v.findViewById(R.id.textViewMail);
        fab = v.findViewById(R.id.fabPerfil);
        textViewMail.setText(Operations.user.getEmail());
        imagePerfil = v.findViewById(R.id.imagePerfil);
        FirebaseFirestore.getInstance().collection("usuarios").document(Operations.user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot s = task.getResult();
                    String apellido = s.getString("apellido");
                    if (apellido != null)
                        editApellidos.setText(apellido);
                    String nombre = s.getString("nombre");
                    if (nombre != null)
                        editNombre.setText(nombre);
                    String telefono = s.getString("telefono");
                    if (telefono != null)
                        editTelefono.setText(telefono);
                    String i = s.getString("imagen");
                    if (i != null && !i.equals("")) {
                        cargarImagenUsuario(i);
                    }
                } else {
                    Toast.makeText(getContext(), "No se han podido obtener los datos del usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imagePerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, COD_ELEGIR_IMAGEN);
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editNombre.getText().toString().equals("")) {
                    Toast.makeText(Operations.applicationContext, "Introduce un nombre válido", Toast.LENGTH_SHORT).show();
                } else {
                    Operations.updateUser(generarUsuario(), selectedImage);
                }
            }
        });
        return v;
    }

    private void cargarImagenUsuario(String ref) {
        StorageReference reference = FirebaseStorage.getInstance().getReference(ref);
        reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Picasso.get().load(task.getResult()).into(imagePerfil);
            }
        });
    }

    private Usuario generarUsuario() {
        return new Usuario(
                editApellidos.getText().toString(),
                editNombre.getText().toString(),
                Operations.user.getEmail(),
                editTelefono.getText().toString(),
                false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COD_ELEGIR_IMAGEN && resultCode == RESULT_OK) {
            selectedImage = data.getData();
            imagePerfil.setImageURI(selectedImage);
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getActivity(), "Se ha cancelado la operación", Toast.LENGTH_SHORT).show();
        }
    }
}
