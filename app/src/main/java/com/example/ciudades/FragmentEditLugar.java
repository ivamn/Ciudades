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
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class FragmentEditLugar extends Fragment {

    private static final int COD_ELEGIR_IMAGEN = 0;
    private EditText editLugar, editDescripcion;
    private ImageView imageView;
    private Util.Accion accion;
    private Lugar lugar;
    private String key;
    private String imagen;
    private LugarViewModel lugarViewModel;

    public FragmentEditLugar(LugarContainer lugarContainer) {
        accion = lugarContainer.getAccion();
        lugar = lugarContainer.getLugar();
        key = lugarContainer.getKey();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.edit_lugar_fragment, container, false);
        editLugar = v.findViewById(R.id.editLugar);
        editDescripcion = v.findViewById(R.id.editDescripcion);
        imageView = v.findViewById(R.id.imageViewLugar);

        if (accion == Util.Accion.EDIT_REQUEST) {
            editLugar.setText(lugar.getLugar());
            editDescripcion.setText(lugar.getDescripcion());
            imagen = lugar.getImagen();
            if (!lugar.getImagen().equals("") && lugar.getImagen() != null) {
                FirebaseStorage.getInstance().getReference(lugar.getImagen()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Picasso.get().load(task.getResult()).into(imageView);
                    }
                });
            }
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

        lugarViewModel = new ViewModelProvider(getActivity()).get(LugarViewModel.class);
        v.findViewById(R.id.buttonAceptarLugar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Lugar l = generarLugar();
                if (accion == Util.Accion.ADD_REQUEST) {
                    lugarViewModel.setData(new LugarContainer(l, Util.Accion.ADD_ACTION));
                } else {
                    lugarViewModel.setData(new LugarContainer(l, Util.Accion.EDIT_ACTION, key));
                }
            }
        });

        return v;
    }

    private Lugar generarLugar() {
        return new Lugar(editLugar.getEditableText().toString(), editDescripcion.getText().toString(), imagen);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COD_ELEGIR_IMAGEN && resultCode == RESULT_OK) {
            Uri rutaImagen = data.getData();
            imagen = rutaImagen.toString();
            imageView.setImageURI(rutaImagen);
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getActivity(), "Se ha cancelado la operación", Toast.LENGTH_SHORT).show();
        }
    }
}
