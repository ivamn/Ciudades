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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class LugarDestacadoFragment extends Fragment {

    private static final int COD_ELEGIR_IMAGEN = 0;

    private EditText nombre;
    private EditText descripcion;
    private ImageView imagen;
    private Util.Accion accion;
    private String key;
    private Uri selectedImage;
    private LugarDestacado lugar;

    public LugarDestacadoFragment(LugarDestacado lugarDestacado, String key, Util.Accion accion) {
        this.lugar = lugarDestacado;
        this.key = key;
        this.accion = accion;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lugar_destacado, container, false);
        nombre = v.findViewById(R.id.textNombreLugarFrag);
        descripcion = v.findViewById(R.id.textDescripcionLugar);
        imagen = v.findViewById(R.id.imagenLugarDestacadoFrag);
        if (accion == Util.Accion.EDIT_REQUEST) {
            FirebaseStorage.getInstance().getReference(key).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    selectedImage = task.getResult();
                }
            });
            nombre.setText(lugar.getNombre());
            descripcion.setText(lugar.getDescripcion());
            if (!lugar.getImage().equals("") && lugar.getImage() != null) {
                Operations.loadIntoImageView(FirebaseStorage.getInstance().getReference(lugar.getImage()), imagen);
            }
        } else {
            key = Operations.newId();
        }
        Operations.mainPlaceDocument = Operations.mainPlaceCollection.document(key);

        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, COD_ELEGIR_IMAGEN);
                }
            }
        });

        v.findViewById(R.id.fabLugaresFragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LugarDestacado lugar = generarLugarDestacado();
                if (accion == Util.Accion.ADD_REQUEST) {
                    Operations.addMainPlace(lugar, selectedImage);
                    getParentFragmentManager().popBackStack();
                } else {
                    Operations.updateMainPlace(lugar, key, selectedImage);
                    getParentFragmentManager().popBackStack();
                }
            }
        });

        return v;
    }

    private LugarDestacado generarLugarDestacado() {
        return new LugarDestacado(key, nombre.getText().toString(), descripcion.getText().toString());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COD_ELEGIR_IMAGEN && resultCode == RESULT_OK) {
            selectedImage = data.getData();
            imagen.setImageURI(selectedImage);
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getActivity(), "Se ha cancelado la operación", Toast.LENGTH_SHORT).show();
        }
    }

}
