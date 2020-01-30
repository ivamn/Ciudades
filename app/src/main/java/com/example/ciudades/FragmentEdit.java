package com.example.ciudades;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class FragmentEdit extends Fragment {

    private String key;
    private EditText editCiudad, pais;
    private ImageView imageView;
    private String imagen;
    private CiudadViewModel ciudadViewModel;
    private CiudadViewModel.Accion accion;
    private Ciudad ciudad;

    public FragmentEdit(CiudadContainer ciudadContainer) {
        accion = ciudadContainer.getAccion();
        ciudad = ciudadContainer.getCiudad();
        this.key = ciudadContainer.getKey();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.edit_fragment, container, false);
        editCiudad = v.findViewById(R.id.editCiudad);
        pais = v.findViewById(R.id.editPais);
        imageView = v.findViewById(R.id.imageView);

        if (accion == CiudadViewModel.Accion.EDIT_REQUEST) {
            editCiudad.setText(ciudad.getCiudad());
            pais.setText(ciudad.getPais());
            if (ciudad.getImagen() != "" && ciudad.getImagen() != null) {
                FirebaseStorage.getInstance().getReference(ciudad.getImagen()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Picasso.get().load(task.getResult()).into(imageView);
                    }
                });
            }
        }

        ciudadViewModel = new ViewModelProvider(getActivity()).get(CiudadViewModel.class);
        v.findViewById(R.id.buttonAceptar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accion == CiudadViewModel.Accion.ADD_REQUEST) {
                    ciudadViewModel.setData(new CiudadContainer(null, CiudadViewModel.Accion.ADD_ACTION));
                } else {
                    Ciudad c = generarCiudad();
                    ciudadViewModel.setData(new CiudadContainer(c, CiudadViewModel.Accion.EDIT_ACTION, key));
                }
            }
        });
        return v;
    }

    private Ciudad generarCiudad(){
        return new Ciudad(editCiudad.getText().toString(), pais.getText().toString(), imagen);
    }
}
